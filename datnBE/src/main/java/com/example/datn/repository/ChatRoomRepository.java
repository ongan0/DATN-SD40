package com.example.datn.repository;

import com.example.datn.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByStatusIn(Collection<String> statuses);

    Optional<ChatRoom> findByRoomId(String roomId);
}
