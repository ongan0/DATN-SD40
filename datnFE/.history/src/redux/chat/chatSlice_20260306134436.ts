// --- src/redux/slices/chatSlice.ts ---
import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import type { ChatMessage, ChatState } from "../../models/chat";

const initialState: ChatState = {
    messages: [],
    isConnected: false,
    roomId: null,
    error: null,
    chatTarget: 'AI', // Mặc định chat với AI
};

const chatSlice = createSlice({
    name: "chat",
    initialState,
    reducers: {
        // --- WebSocket Actions (Dùng để Saga bắt) ---
        connectChat: (state, action: PayloadAction<string>) => {
            state.roomId = action.payload;
            state.error = null;
        },
        disconnectChat: (state) => {
            state.isConnected = false;
        },
        sendMessage: (_state, _action: PayloadAction<{ roomId: string; content: string; target: 'AI' | 'STAFF' }>) => {
            // Chỉ dùng để trigger Saga gửi tin nhắn qua WebSocket
        },

        // --- State Update Actions ---
        connectChatSuccess: (state) => {
            state.isConnected = true;
            state.error = null;
        },
        receiveMessage: (state, action: PayloadAction<ChatMessage>) => {
            state.messages.push(action.payload);
        },
        setChatTarget: (state, action: PayloadAction<'AI' | 'STAFF'>) => {
            state.chatTarget = action.payload;
        },
        chatError: (state, action: PayloadAction<string>) => {
            state.error = action.payload;
            state.isConnected = false;
        },

        // --- API REST Actions (Lấy lịch sử cũ) ---
        fetchChatHistory: (_state, _action: PayloadAction<string>) => {
             // Đợi lấy lịch sử
        },
        fetchChatHistorySuccess: (state, action: PayloadAction<ChatMessage[]>) => {
            state.messages = action.payload;
        }
    },
});

export const chatActions = chatSlice.actions;
export default chatSlice.reducer;