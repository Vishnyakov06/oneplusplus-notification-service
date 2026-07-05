package com.hh.oneplusplus.service;

import com.hh.oneplusplus.dto.NotificationReadyToSendEvent;
import com.hh.oneplusplus.dto.NotificationResponseDto;
import com.hh.oneplusplus.dto.NotificationResponseDtoFactory;
import com.hh.oneplusplus.dto.notification.NotificationEvent;
import com.hh.oneplusplus.dto.notification.NotificationType;
import com.hh.oneplusplus.mapper.NotificationMapper;
import com.hh.oneplusplus.model.Notification;
import com.hh.oneplusplus.repository.NotificationDeliveryRepository;
import com.hh.oneplusplus.repository.NotificationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationResponseDtoFactory factory;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;
    private final NotificationDeliveryRepository notificationDeliveryRepository;
    private final ApplicationEventPublisher eventPublisher;

    public NotificationService(
            NotificationResponseDtoFactory factory,
            NotificationRepository notificationRepository,
            NotificationMapper mapper,
            NotificationDeliveryRepository notificationDeliveryRepository,
            ApplicationEventPublisher eventPublisher) {
        this.factory = factory;
        this.notificationRepository = notificationRepository;
        this.mapper = mapper;
        this.notificationDeliveryRepository = notificationDeliveryRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void handle(NotificationEvent notificationEvent){
        int reserved = notificationDeliveryRepository.tryReserve(notificationEvent.getNotificationId(),
                notificationEvent.getType().name());
        if(reserved == 0){
            return;
        }
        NotificationResponseDto responseDto = factory.create(notificationEvent);

        if (notificationEvent.getType().equals(NotificationType.WEB)) {
            saveNotification(notificationEvent, responseDto);
        }

        String channel = notificationEvent.getType().name();
        eventPublisher.publishEvent(new NotificationReadyToSendEvent(channel,
                notificationEvent.getUserId(), notificationEvent.getEmail(), responseDto));
    }

    private void saveNotification(NotificationEvent notificationEvent, NotificationResponseDto responseDto){
        if (notificationRepository.existsByNotificationId(notificationEvent.getNotificationId())){
            return;
        }
        Notification notification = mapper.toEntity(notificationEvent, responseDto);
        notificationRepository.save(notification);
    }
}
