package com.hh.oneplusplus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hh.oneplusplus.dto.notification.NotificationEventType;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificationResponseDto(
        UUID notificationId,
        NotificationEventType notificationType,
        String email,
        Instant createdAt,
        String fallbackMessage,
        boolean read,
        Map<String, Object> params,

        Integer groupCount,
        List<UUID> groupIds
) {
}
