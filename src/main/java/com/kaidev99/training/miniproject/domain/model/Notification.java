package com.kaidev99.training.miniproject.domain.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Builder
@Document(collection = "notifications")
public record Notification(
        @Id
        String id,
        String senderId,
        String recipientId,
        String content,
        Instant timestamp,
        Status status
) {
    public enum Status {
        UNREAD, READ
    }
}
