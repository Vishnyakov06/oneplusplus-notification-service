package com.hh.oneplusplus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hh.oneplusplus.dto.notification.NotificationEventType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificationResponseDto(
        UUID notificationId,
        NotificationEventType eventType,
        String email,
        Instant createdAt,
        String fallbackMessage,
        Map<String, Object> params
) {
}
