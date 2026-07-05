package com.hh.oneplusplus.dto;

public record NotificationReadyToSendEvent(
        String channel,
        Long userId,
        String email,
        NotificationResponseDto responseDto
) {
}
