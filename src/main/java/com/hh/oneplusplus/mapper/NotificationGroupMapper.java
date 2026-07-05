package com.hh.oneplusplus.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hh.oneplusplus.dto.NotificationResponseDto;
import com.hh.oneplusplus.dto.notification.NotificationEventType;
import com.hh.oneplusplus.service.MessageResolverService;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Component;

import java.time.Instant;
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

    public NotificationResponseDto map(Tuple tuple) {
        Integer groupCount = tuple.get("group_count", Long.class).intValue();
        boolean isGroup = groupCount > 1;

        String groupedIdsRaw = tuple.get("grouped_ids", String.class);
        List<UUID> groupIds = isGroup
                ? Arrays.stream(groupedIdsRaw.split(",")).map(UUID::fromString).toList()
                : null;

        NotificationEventType eventType = NotificationEventType.valueOf(tuple.get("event_type", String.class));
        Map<String, Object> params = parseParams(tuple.get("params", String.class));

        String message = isGroup
                ? messageResolverService.resolveGroupMessage(eventType, groupCount, params)
                : tuple.get("message", String.class);
        return new NotificationResponseDto(
                tuple.get("notification_id", UUID.class),
                eventType,
                null,
                tuple.get("created_at", Instant.class),
                message,
                tuple.get("is_read", Boolean.class),
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
