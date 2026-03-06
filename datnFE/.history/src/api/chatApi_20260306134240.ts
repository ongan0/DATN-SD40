// --- src/api/chatApi.ts ---
import axiosClient from "./axiosClient";
import type { ResponseObject } from "../models/base";
import type { ChatMessage } from "../models/chat";

const BASE_URL = "/api/v1/chat"; // Sửa lại cho đúng endpoint backend của bạn

export const getChatHistory = async (roomId: string): Promise<ChatMessage[]> => {
    // API backend của bạn trả về ResponseEntity.ok(messages)
    // Nếu backend bạn bọc trong ResponseObject (Giống list customer) thì hãy chỉnh lại nhé.
    const res = await axiosClient.get<ChatMessage[]>(`${BASE_URL}/${roomId}/messages`);
    return res.data; 
};

export const chatApi = {
    getChatHistory,
};