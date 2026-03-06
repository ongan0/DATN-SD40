package com.example.datn.core.admin.aiChat;

import com.example.datn.entity.ChatMessages;
import com.example.datn.entity.ChatRoom;
import com.example.datn.repository.ChatMessagesRepository;
import com.example.datn.repository.ChatRoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatMessagesRepository messageRepo;
    private final ChatRoomRepository roomRepo;

    public ChatController(ChatMessagesRepository messageRepo, ChatRoomRepository roomRepo) {
        this.messageRepo = messageRepo;
        this.roomRepo = roomRepo;
    }

    // API 1: Lấy danh sách các phòng chat đang hoạt động (Dành cho màn hình Admin)
    @GetMapping("/rooms/active")
    public ResponseEntity<List<ChatRoom>> getActiveRooms() {
        // Trả về danh sách khách hàng đang chờ hoặc đang chat
        return ResponseEntity.ok(roomRepo.findByStatusIn(Arrays.asList("WAITING", "ACTIVE")));
    }

    // API 2: Lấy lịch sử tin nhắn của một phòng cụ thể (Dành cho cả Customer và Admin)
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessages>> getChatHistory(@PathVariable String roomId) {
        List<ChatMessages> messages = messageRepo.findByRoomIdOrderByTimestampAsc(roomId);
        return ResponseEntity.ok(messages);
    }
}
