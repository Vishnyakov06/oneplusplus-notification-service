package com.hh.oneplusplus.service;

import com.hh.oneplusplus.dto.NotificationResponseDto;
import com.hh.oneplusplus.dto.NotificationResponseDtoFactory;
import com.hh.oneplusplus.dto.notification.NotificationEvent;
import com.hh.oneplusplus.dto.notification.NotificationType;
import com.hh.oneplusplus.mapper.NotificationMapper;
import com.hh.oneplusplus.model.Notification;
import com.hh.oneplusplus.repository.NotificationRepository;
import com.hh.oneplusplus.sender.NotificationSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {
    private final Map<String, NotificationSender> notificationSenders;
    private final NotificationResponseDtoFactory factory;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;

    public NotificationService(
            Map<String, NotificationSender> notificationSenders,
            NotificationResponseDtoFactory factory,
            NotificationRepository notificationRepository,
            NotificationMapper mapper) {
        this.notificationSenders = notificationSenders;
        this.factory = factory;
        this.notificationRepository = notificationRepository;
        this.mapper = mapper;
    }

    public void handle(NotificationEvent notificationEvent){
        NotificationResponseDto responseDto = factory.create(notificationEvent);

        if(notificationEvent.getType().equals(NotificationType.WEB)){
            saveNotification(notificationEvent, responseDto);
        }

        NotificationSender sender = notificationSenders.get(notificationEvent.getType().name());
        sender.send(notificationEvent.getUserId(), responseDto);
    }

    private void saveNotification(NotificationEvent notificationEvent, NotificationResponseDto responseDto){
        if(!notificationRepository.existsByNotificationId(notificationEvent.getNotificationId())){
            Notification notification = mapper.toEntity(notificationEvent, responseDto);
            notificationRepository.save(notification);
        }
    }
}
