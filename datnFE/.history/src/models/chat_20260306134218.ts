export interface ChatMessage {
    id?: number;
    roomId: string;
    senderId?: string;
    senderType: 'CUSTOMER' | 'AI' | 'STAFF';
    target: 'AI' | 'STAFF';
    content: string;
    timestamp?: string;
}

export interface ChatState {
    messages: ChatMessage[];
    isConnected: boolean;
    roomId: string | null;
    error: string | null;
    chatTarget: 'AI' | 'STAFF';
}