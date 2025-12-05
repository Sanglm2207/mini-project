package com.kaidev99.training.miniproject.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaidev99.training.miniproject.domain.model.Notification;
import com.kaidev99.training.miniproject.repository.NotificationRepository;
import com.kaidev99.training.miniproject.service.NotificationProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;
    private final NotificationProducerService notificationProducerService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = (String) Objects.requireNonNull(session.getAttributes().get("userId"));
        sessionManager.registerSession(userId, session);
        log.info("WebSocket connection established for user ID: {}, Session ID: {}", userId, session.getId());
        session.sendMessage(new TextMessage("Connection established! Welcome user " + userId));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String userId = (String) Objects.requireNonNull(session.getAttributes().get("userId"));
        String payload = message.getPayload();
        log.info("Received message from user {}: {}", userId, payload);

        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            String type = jsonNode.get("type").asText();

            switch (type) {
                case "SEND_NOTIFICATION":
                    handleSendNotification(userId, jsonNode.get("payload"));
                    break;
                // TODO: Các case khác cho các yêu cầu sau
                // case "GET_USER_STATUS":
                //     ...
                //     break;
                default:
                    log.warn("Unknown message type: {}", type);
                    session.sendMessage(new TextMessage("Error: Unknown message type '" + type + "'"));
            }
        } catch (Exception e) {
            log.error("Error processing message from user {}: {}", userId, e.getMessage());
            session.sendMessage(new TextMessage("Error: Invalid message format."));
        }
    }

    private void handleSendNotification(String senderId, JsonNode payload) {
        String recipientId = payload.get("recipientId").asText();
        String content = payload.get("content").asText();

        // Yêu cầu 1: Lưu vào DB
        Notification notification = Notification.builder()
                .senderId(senderId)
                .recipientId(recipientId)
                .content(content)
                .timestamp(Instant.now())
                .status(Notification.Status.UNREAD)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Saved notification to DB: {}", savedNotification);

        // Yêu cầu 2: Gửi sự kiện đến Kafka
        notificationProducerService.sendNotificationEvent(savedNotification);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) Objects.requireNonNull(session.getAttributes().get("userId"));
        sessionManager.removeSession(userId);
        log.info("WebSocket connection closed for user: {} with status: {}", userId, status);
    }
}
