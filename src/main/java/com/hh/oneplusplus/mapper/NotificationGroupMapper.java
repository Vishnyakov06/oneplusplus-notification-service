package com.hh.oneplusplus.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hh.oneplusplus.dto.NotificationResponseDto;
import com.hh.oneplusplus.dto.notification.NotificationEventType;
import com.hh.oneplusplus.repository.projection.NotificationGroupDetailProjection;
import com.hh.oneplusplus.repository.projection.NotificationGroupSummaryProjection;
import com.hh.oneplusplus.service.MessageResolverService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class NotificationGroupMapper {

    private final ObjectMapper objectMapper;
    private final MessageResolverService messageResolverService;

    public NotificationGroupMapper(ObjectMapper objectMapper,
                                   MessageResolverService messageResolverService) {
        this.objectMapper = objectMapper;
        this.messageResolverService = messageResolverService;
    }

    public NotificationResponseDto map(NotificationGroupSummaryProjection summary,
                                       NotificationGroupDetailProjection head) {
        Integer groupCount = summary.getGroupCount().intValue();
        boolean isGroup = groupCount > 1;

        List<UUID> groupIds = isGroup
                ? Arrays.stream(head.getGroupedIds().split(",")).map(UUID::fromString).toList()
                : null;

        NotificationEventType eventType = NotificationEventType.valueOf(head.getEventType());
        Map<String, Object> params = parseParams(head.getParams());

        String message = isGroup
                ? messageResolverService.resolveGroupMessage(eventType, groupCount, params)
                : head.getMessage();

        return new NotificationResponseDto(
                head.getNotificationId(),
                eventType,
                null,
                head.getCreatedAt(),
                message,
                Boolean.TRUE.equals(summary.getGroupIsRead()),
                params,
                isGroup ? groupCount : null,
                isGroup ? groupIds : null
        );
    }

    private Map<String, Object> parseParams(String json) {
        if (json == null) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse params", e);
        }
    }
}
