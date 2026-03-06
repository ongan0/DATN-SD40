// --- src/redux/sagas/chatSaga.ts ---
import { take, put, call, takeLatest } from "redux-saga/effects";
import { eventChannel, type EventChannel } from "redux-saga";
import { Client } from "@stomp/stompjs";
import { chatApi } from "../../api/chatApi";
import type { PayloadAction } from "@reduxjs/toolkit";
import type { ChatMessage } from "../../models/chat";

let stompClient: Client | null = null;

// Hàm tạo STOMP Client (giữ nguyên logic)
function connectStompClient(): Promise<Client> {
    return new Promise((resolve, reject) => {
        const socket = new SockJS("http://localhost:8386/ws"); 
        stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            onConnect: () => resolve(stompClient!),
            onStompError: (frame) => reject(frame.headers["message"]),
        });
        stompClient.activate();
    });
}

// Tạo Channel lắng nghe
function createStompChannel(client: Client, roomId: string): EventChannel<ChatMessage> {
    return eventChannel((emit) => {
        const subscription = client.subscribe(`/topic/room/${roomId}`, (message) => {
            const parsedMessage: ChatMessage = JSON.parse(message.body);
            emit(parsedMessage);
        });
        return () => subscription.unsubscribe();
    });
}

// Xử lý lấy lịch sử
function* handleFetchHistory(action: PayloadAction<string>) {
    try {
        const history: ChatMessage[] = yield call(chatApi.getChatHistory, action.payload);
        yield put(chatActions.fetchChatHistorySuccess(history));
    } catch (error: any) {
        console.error("Lỗi tải lịch sử chat:", error);
    }
}

// Xử lý kết nối WebSocket
function* handleConnectChat(action: PayloadAction<string>) {
    const roomId = action.payload;

    try {
        // 1. Fetch lịch sử chat cũ trước
        yield call(handleFetchHistory, { payload: roomId, type: chatActions.fetchChatHistory.type });

        // 2. Mở kết nối WebSocket
        const client: Client = yield call(connectStompClient);
        yield put(chatActions.connectChatSuccess());

        // 3. Lắng nghe tin nhắn mới
        const socketChannel: EventChannel<ChatMessage> = yield call(createStompChannel, client, roomId);

        while (true) {
            const incomingMessage: ChatMessage = yield take(socketChannel);
            yield put(chatActions.receiveMessage(incomingMessage));
        }
    } catch (error: any) {
        yield put(chatActions.chatError(error || "Không thể kết nối Server"));
    }
}

// Xử lý gửi tin nhắn
function* handleSendMessage(action: PayloadAction<{ roomId: string; content: string; target: 'AI' | 'STAFF' }>) {
    const { roomId, content, target } = action.payload;
    
    if (stompClient && stompClient.connected) {
        const chatMessage = { content, target, senderType: 'CUSTOMER' };
        
        stompClient.publish({
            destination: `/app/chat/${roomId}`,
            body: JSON.stringify(chatMessage),
        });
    } else {
        yield put(chatActions.chatError("Mất kết nối với Server"));
    }
}

function* handleDisconnectChat() {
    if (stompClient && stompClient.connected) {
        stompClient.deactivate();
    }
}

// Gom nhóm lại
export default function* watchChatFlow() {
    yield takeLatest(chatActions.connectChat.type, handleConnectChat);
    yield takeLatest(chatActions.sendMessage.type, handleSendMessage);
    yield takeLatest(chatActions.disconnectChat.type, handleDisconnectChat);
    yield takeLatest(chatActions.fetchChatHistory.type, handleFetchHistory);
}