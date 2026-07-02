package com.hh.oneplusplus.repository;

import com.hh.oneplusplus.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserId(Long id, Pageable pageable);
    List<Notification> findByUserId(Long id);
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    boolean existsByNotificationId(UUID notificationId);
    Long countByUserIdAndIsReadFalse(Long userId);
    void deleteByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM Notification n where n.notificationId = :notificationId and n.userId = :userId")
    int deleteByNotificationIdAndUserId(@Param("notificationId") UUID notificationId, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Notification n where n.isRead = true and n.createdAt < :targetTime")
    int deleteOldReadNotifications(@Param("targetTime")Instant targetTime);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true where n.userId = :userId and n.isRead = false")
    void markAllAsRead(@Param("userId")Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true" +
            " where n.notificationId in :ids and n.userId = :userId and n.isRead = false")
    void markSelectedAsRead(@Param("ids") List<UUID> ids, @Param("userId") Long userId);
}
