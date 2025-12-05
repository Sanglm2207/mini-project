package com.kaidev99.training.miniproject.repository;

import com.kaidev99.training.miniproject.domain.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
}
