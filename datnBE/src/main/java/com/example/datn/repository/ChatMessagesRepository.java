package com.example.datn.repository;

import com.example.datn.entity.ChatMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessagesRepository extends JpaRepository<ChatMessages, Long> {
    List<ChatMessages> findTop5ByRoomIdOrderByTimestampDesc(String roomId);

    List<ChatMessages> findByRoomIdOrderByTimestampAsc(String roomId);
}
