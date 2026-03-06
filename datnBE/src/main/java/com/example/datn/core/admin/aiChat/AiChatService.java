package com.example.datn.core.admin.aiChat;

import com.example.datn.entity.ChatMessages;
import com.example.datn.repository.ChatMessagesRepository;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import java.util.Collections;
import java.util.List;

@Service
public class AiChatService {

    private final ChatClient chatClient;
    private final ChatMessagesRepository messageRepo;

    public AiChatService(ChatClient.Builder builder, ChatMessagesRepository messageRepo) {
        this.chatClient = builder.build();
        this.messageRepo = messageRepo;
    }

    public String getConsultation(String roomId, String currentMessage) {
        // 1. Lấy lịch sử chat gần đây (ví dụ 5 tin nhắn mới nhất)
        List<ChatMessages> history = messageRepo.findTop5ByRoomIdOrderByTimestampDesc(roomId);
        Collections.reverse(history); // Đảo ngược để theo thứ tự thời gian

        // 2. Build nội dung lịch sử thành một chuỗi văn bản
        StringBuilder contextBuilder = new StringBuilder();
        for (ChatMessages msg : history) {
            String sender = msg.getSenderType().equals("CUSTOMER") ? "Khách hàng" : "AI";
            contextBuilder.append(sender).append(": ").append(msg.getContent()).append("\n");
        }

        // 3. Tạo Prompt kết hợp System Role + Lịch sử + Câu hỏi hiện tại
        String systemPrompt = "Bạn là chuyên gia tư vấn máy ảnh Canon. Dưới đây là lịch sử trò chuyện:\n"
                + contextBuilder.toString() + "\n"
                + "Hãy trả lời câu hỏi tiếp theo của khách hàng.";

        // 4. Gọi Spring AI
        return chatClient.prompt()
                .system(systemPrompt)
                .user(currentMessage)
                .call()
                .content();
    }

}
