package com.hh.oneplusplus.repository;

import com.hh.oneplusplus.dto.notification.NotificationType;
import com.hh.oneplusplus.model.NotificationDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, Long> {
    @Modifying
    @Query(value = """
        INSERT INTO notification_deliveries (notification_id, channel)
        VALUES (:notificationId, :channel)
        ON CONFLICT (notification_id, channel) DO NOTHING
        """, nativeQuery = true)
    int tryReserve(@Param("notificationId") UUID notificationId, @Param("channel") String channel);
    void deleteByNotificationIdAndChannel(UUID notificationId, NotificationType channel);
}
