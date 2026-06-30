package com.hh.oneplusplus.dto;

import com.hh.oneplusplus.dto.notification.NotificationEvent;
import com.hh.oneplusplus.mapper.NotificationMapper;
import com.hh.oneplusplus.service.MessageResolverService;
import org.springframework.stereotype.Component;

@Component
public class NotificationResponseDtoFactory {
    private final MessageResolverService resolverService;
    private final NotificationMapper mapper;

    public NotificationResponseDtoFactory(MessageResolverService resolverService, NotificationMapper mapper) {
        this.resolverService = resolverService;
        this.mapper = mapper;
    }
    public NotificationResponseDto create(NotificationEvent event){
        String message = resolverService.resolveMessage(event);
        return mapper.toResponseDto(event, message);
    }
}
