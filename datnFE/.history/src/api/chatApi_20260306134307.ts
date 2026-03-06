import axiosClient from "./axiosClient";
import type { ChatMessage } from "../models/chat";

const BASE_URL = "/api/v1/chat";

export const getChatHistory = async (roomId: string): Promise<ChatMessage[]> => {
    // API backend của bạn trả về ResponseEntity.ok(messages)
    const res = await axiosClient.get<ChatMessage[]>(`${BASE_URL}/${roomId}/messages`);
    return res.data; 
};

export const chatApi = {
    getChatHistory,
};