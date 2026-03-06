package com.example.datn.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Cấu hình endpoint "/ws" để Client (React) kết nối vào.
        // setAllowedOriginPatterns("*") giúp tránh lỗi CORS khi gọi từ port 3000 (React)
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // "/topic" dùng để phát sóng chung (broadcast)
        registry.enableSimpleBroker("/topic", "/queue");

        // "/app" là tiền tố cho các tin nhắn từ Client gửi lên Server
        registry.setApplicationDestinationPrefixes("/app");
    }
}
