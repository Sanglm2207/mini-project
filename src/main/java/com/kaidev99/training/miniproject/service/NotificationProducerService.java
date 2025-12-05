package com.kaidev99.training.miniproject.service;

import com.kaidev99.training.miniproject.domain.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.notification-topic}")
    private String notificationTopic;

    public void sendNotificationEvent(Notification notification) {
        log.info("Sending notification event to Kafka topic: {}", notificationTopic);

        // Gửi message. KafkaTemplate đã được cấu hình với JsonSerializer.
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(notificationTopic, notification.recipientId(), notification)
                .toCompletableFuture()
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully sent notification for user {}: {}", notification.recipientId(), result.getRecordMetadata());
                    } else {
                        log.error("Failed to send notification for user {}: {}", notification.recipientId(), ex.getMessage());
                    }
                });
    }
}