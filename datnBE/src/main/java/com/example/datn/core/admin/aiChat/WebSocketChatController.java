package com.example.datn.core.admin.aiChat;

import com.example.datn.entity.ChatMessages;
import com.example.datn.entity.ChatRoom;
import com.example.datn.repository.ChatMessagesRepository;
import com.example.datn.repository.ChatRoomRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Controller
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final AiChatService aiChatService;
    private final ChatMessagesRepository messageRepo;
    private final ChatRoomRepository roomRepo;

    public WebSocketChatController(SimpMessagingTemplate messagingTemplate,
                                   AiChatService aiChatService,
                                   ChatMessagesRepository messageRepo,
                                   ChatRoomRepository roomRepo) {
        this.messagingTemplate = messagingTemplate;
        this.aiChatService = aiChatService;
        this.messageRepo = messageRepo;
        this.roomRepo = roomRepo;
    }


    // Lắng nghe các tin nhắn gửi đến đích: /app/chat/{roomId}
    @MessageMapping("/chat/{roomId}")
    public void processMessage(@Payload ChatMessages message, @DestinationVariable String roomId) {

        // 1. Cập nhật thông tin cơ bản và lưu tin nhắn của khách vào Database
        message.setRoomId(roomId);
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }
        messageRepo.save(message);

        // 2. Phát sóng (Broadcast) tin nhắn của khách lên kênh chung để Client cập nhật UI ngay lập tức
        // Bất kỳ ai (Khách hoặc Admin) đang subscribe vào /topic/room/{roomId} đều sẽ nhận được
        messagingTemplate.convertAndSend("/topic/room/" + roomId, message);

        // 3. Xử lý luồng rẽ nhánh: Hỏi AI hay Cần gặp Nhân viên?
        if ("AI".equalsIgnoreCase(message.getTarget())) {

            // Gọi AI xử lý (đã bao gồm lịch sử chat context bên trong service)
            String aiReplyContent = aiChatService.getConsultation(roomId, message.getContent());

            // Tạo đối tượng tin nhắn mới cho AI
            ChatMessages aiReply = new ChatMessages();
            aiReply.setRoomId(roomId);
            aiReply.setSenderType("AI");
            aiReply.setContent(aiReplyContent);
            aiReply.setTimestamp(LocalDateTime.now());

            // Lưu tin nhắn AI vào DB và phát sóng lại cho Client
            messageRepo.save(aiReply);
            messagingTemplate.convertAndSend("/topic/room/" + roomId, aiReply);

        } else if ("STAFF".equalsIgnoreCase(message.getTarget())) {

            // Khách hàng muốn gặp nhân viên.
            // Cập nhật trạng thái phòng chat thành WAITING để hiển thị lên màn hình Admin
            ChatRoom room = roomRepo.findByRoomId(roomId).orElseGet(() -> {
                ChatRoom newRoom = new ChatRoom();
                newRoom.setRoomId(roomId);
                return newRoom;
            });

            room.setStatus("WAITING");
            roomRepo.save(room);

            // (Tùy chọn) Có thể bắn một tin nhắn Notification chung lên kênh /topic/admin/notifications
            // để màn hình Admin reo chuông báo có khách cần hỗ trợ.
        }
    }
}
