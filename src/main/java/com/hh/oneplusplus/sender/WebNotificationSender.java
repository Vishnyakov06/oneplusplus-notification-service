package com.hh.oneplusplus.sender;

import com.hh.oneplusplus.dto.NotificationResponseDto;
import com.hh.oneplusplus.service.SseEmitterService;
import org.springframework.stereotype.Component;

@Component("WEB")
public class WebNotificationSender implements NotificationSender{
    private final SseEmitterService sseEmitterService;

    public WebNotificationSender(SseEmitterService sseEmitterService) {
        this.sseEmitterService = sseEmitterService;
    }

    @Override
    public void send(Long userId, String email, NotificationResponseDto notificationResponseDto) {
        sseEmitterService.sendToUser(userId, notificationResponseDto);
    }
}
