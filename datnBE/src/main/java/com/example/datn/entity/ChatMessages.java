package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_messages")
public class ChatMessages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId; //customerid
    private String senderId; //id ng gửi (KH, NV, ID)

    @Column(length = 65535)
    private String content;

    private String senderType; // CUSTOMER, STAFF, AI
    private String target; // Mục tiêu chat: "AI" hoặc "STAFF"

    private LocalDateTime timestamp = LocalDateTime.now();
}
