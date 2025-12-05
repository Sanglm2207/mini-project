package com.kaidev99.training.miniproject.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {
    // Maps userId to WebSocketSession
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void registerSession(String userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    public void removeSession(String userId) {
        sessions.remove(userId);
    }

    public WebSocketSession getSession(String userId) {
        return sessions.get(userId);
    }
}
