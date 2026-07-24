package com.hh.oneplusplus.repository;

import com.hh.oneplusplus.dto.notification.NotificationEventType;
import com.hh.oneplusplus.dto.notification.NotificationType;
import com.hh.oneplusplus.model.MessageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {
    Optional<MessageTemplate> findByEventTypeAndChannel(NotificationEventType eventType, NotificationType channel);

    Optional<MessageTemplate>  findByEventType(NotificationEventType eventType);
}
