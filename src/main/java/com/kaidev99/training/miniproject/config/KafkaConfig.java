package com.kaidev99.training.miniproject.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.notification-topic}")
    private String notificationTopic;

    @Bean
    public NewTopic notificationEventsTopic() {
        return TopicBuilder.name(notificationTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
