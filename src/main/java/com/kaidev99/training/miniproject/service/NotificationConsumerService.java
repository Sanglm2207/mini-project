package com.kaidev99.training.miniproject.service;

import com.kaidev99.training.miniproject.domain.model.Notification;
import com.kaidev99.training.miniproject.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumerService {

    private final WebSocketSessionManager webSocketSessionManager;

    @KafkaListener(
            topics = "${app.kafka.notification-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenNotificationEvents(@Payload Notification notification) {
        log.info("Received notification from Kafka: {}", notification);

        // TODO: Yêu cầu 2 - Hoàn thiện logic ở đây
        // 1. Kiểm tra xem người nhận (notification.recipientId()) có đang online không
        //    (sử dụng webSocketSessionManager).
        //
        // 2. Nếu có, tạo một message WebSocket mới (ví dụ type: "RECEIVE_NOTIFICATION")
        //    và gửi đến client của người nhận.
        //    Lưu ý: Bạn cần chuyển đổi object Notification thành chuỗi JSON trước khi gửi.

    }
}
