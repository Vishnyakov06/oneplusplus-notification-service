package com.hh.oneplusplus.mapper;

import com.hh.oneplusplus.dto.NotificationResponseDto;
import com.hh.oneplusplus.dto.notification.NotificationEvent;
import com.hh.oneplusplus.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(source = "event.notificationId", target = "notificationId")
    @Mapping(source = "event.eventType", target = "eventType")
    @Mapping(source = "event.createdAt", target = "createdAt")
    @Mapping(source = "event.params", target = "params")
    NotificationResponseDto toResponseDto(NotificationEvent event, String fallbackMessage);

    @Mapping(source = "event.notificationId", target = "notificationId")
    @Mapping(source = "event.userId", target = "userId")
    @Mapping(source = "event.eventType", target = "eventType")
    @Mapping(source = "event.createdAt", target = "createdAt")
    @Mapping(source = "responseDto.fallbackMessage", target = "message")
    @Mapping(source = "responseDto.params", target = "params")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "read", ignore = true)
    @Mapping(target = "readAt", ignore = true)
    Notification toEntity(NotificationEvent event, NotificationResponseDto responseDto);
}
