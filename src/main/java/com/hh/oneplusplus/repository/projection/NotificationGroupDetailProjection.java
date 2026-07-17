package com.hh.oneplusplus.repository.projection;

import java.time.Instant;
import java.util.UUID;

public interface NotificationGroupDetailProjection {
    String getGroupKey();
    UUID getNotificationId();
    String getEventType();
    String getMessage();
    String getParams();
    String getGroupedIds();
    Instant getCreatedAt();
    Integer getRn();
}
