package com.hh.oneplusplus.listener;

import com.hh.oneplusplus.service.NotificationService;
import com.hh.oneplusplus.dto.notification.NotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {
    private final NotificationService notificationService;
    public NotificationListener(NotificationService notificationService){
        this.notificationService = notificationService;
    }
    @KafkaListener(
            topics = "${app.kafka.notification-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "${spring.kafka.listener.concurrency}"
    )
    public void listen(
            NotificationEvent notificationEvent,
            Acknowledgment acknowledgment){
        notificationService.handle(notificationEvent);
        acknowledgment.acknowledge();
    }
}
