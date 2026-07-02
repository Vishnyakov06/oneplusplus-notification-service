package com.hh.oneplusplus.dto;

import org.springframework.data.domain.Page;

public record NotificationPageResponse(
        Page<NotificationResponseDto> content,
        long totalUnread
) {
}
