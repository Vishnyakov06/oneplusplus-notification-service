package com.hh.oneplusplus.repository.projection;

import java.time.Instant;

public interface NotificationGroupSummaryProjection {
    String getGroupKey();
    Instant getSortTs();
    Integer getGroupCount();
    Boolean getGroupIsRead();
}
