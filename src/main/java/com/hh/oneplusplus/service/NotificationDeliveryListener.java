package com.hh.oneplusplus.service;

import com.hh.oneplusplus.dto.NotificationReadyToSendEvent;
import com.hh.oneplusplus.dto.notification.NotificationType;
import com.hh.oneplusplus.repository.NotificationDeliveryRepository;
import com.hh.oneplusplus.sender.NotificationSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
public class NotificationDeliveryListener {
    private final Map<String, NotificationSender> notificationSenders;
    private final NotificationDeliveryRepository notificationDeliveryRepository;

    public NotificationDeliveryListener(Map<String, NotificationSender> notificationSenders,
                                        NotificationDeliveryRepository notificationDeliveryRepository) {
        this.notificationSenders = notificationSenders;
        this.notificationDeliveryRepository = notificationDeliveryRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotificationReadyToSend(NotificationReadyToSendEvent event){
        NotificationSender sender = notificationSenders.get(event.channel());
        try {
            sender.send(event.userId(), event.email(), event.responseDto());
        }
        catch (Exception ex){
            notificationDeliveryRepository.deleteByNotificationIdAndChannel(event.responseDto().notificationId(),
                    NotificationType.valueOf(event.channel()));
            throw ex;
        }
    }
}
