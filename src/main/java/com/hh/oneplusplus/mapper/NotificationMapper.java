package com.hh.oneplusplus.mapper;

import com.hh.oneplusplus.dto.NotificationResponseDto;
import com.hh.oneplusplus.dto.notification.NotificationEvent;
import com.hh.oneplusplus.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(source = "event.notificationId", target = "notificationId")
    @Mapping(source = "event.eventType", target = "notificationType")
    @Mapping(source = "event.createdAt", target = "createdAt")
    @Mapping(source = "event.params", target = "params")
    @Mapping(target = "email", ignore = true)
    NotificationResponseDto toResponseDto(NotificationEvent event, String fallbackMessage, boolean read);

    @Mapping(source = "event.notificationId", target = "notificationId")
    @Mapping(source = "event.userId", target = "userId")
    @Mapping(source = "event.eventType", target = "notificationType")
    @Mapping(source = "event.createdAt", target = "createdAt")
    @Mapping(source = "responseDto.fallbackMessage", target = "message")
    @Mapping(source = "responseDto.params", target = "params")
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "responseDto.read", target = "read")
    @Mapping(target = "readAt", ignore = true)
    Notification toEntity(NotificationEvent event, NotificationResponseDto responseDto);

    @Mapping(source = "message", target = "fallbackMessage")
    @Mapping(source = "read", target = "read")
    @Mapping(target = "email", ignore = true)
    NotificationResponseDto toResponseDto(Notification entity);
}
