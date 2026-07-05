package com.hh.oneplusplus.sender;

import com.hh.oneplusplus.dto.NotificationResponseDto;

public interface NotificationSender {
    void send(Long userId, String email, NotificationResponseDto notificationResponseDto);
}
