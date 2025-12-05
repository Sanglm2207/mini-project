package com.kaidev99.training.miniproject.config;

import com.kaidev99.training.miniproject.interceptor.WebSocketHandshakeInterceptor;
import com.kaidev99.training.miniproject.websocket.CustomWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final CustomWebSocketHandler customWebSocketHandler;
    private final WebSocketHandshakeInterceptor handshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customWebSocketHandler, "/ws/notifications")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*");
    }
}
