package com.hh.oneplusplus.repository;
import com.hh.oneplusplus.model.Notification;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    Page<Notification> findByUserId(@Param("userId") Long userId, Pageable pageable);

    List<Notification> findByUserId(Long id);
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    boolean existsByNotificationId(UUID notificationId);
    Long countByUserIdAndIsReadFalse(Long userId);
    void deleteByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM Notification n where n.notificationId = :notificationId and n.userId = :userId")
    int deleteByNotificationIdAndUserId(@Param("notificationId") UUID notificationId, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Notification n where n.isRead = true and n.readAt < :targetTime")
    int deleteReadBefore(@Param("targetTime")Instant targetTime);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP where n.userId = :userId and n.isRead = false")
    void markAllAsRead(@Param("userId")Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP" +
            " where n.notificationId in :ids and n.userId = :userId and n.isRead = false")
    void markSelectedAsRead(@Param("ids") List<UUID> ids, @Param("userId") Long userId);


    @Query(value = """
        with notification_and_group_key as (
            select n.*,
                case
                    when n.event_type in :eventTypes
                    then n.event_type || ':' || (n.params->>'eventId') || ':' || n.is_read::text
                    else 'single:' || n.notification_id::text
                end as group_key
            from notifications as n
            where n.user_id = :userId
        ),
        grouped as (
                select ng.*,
                    COUNT(*) over (partition by group_key) as group_count,
                    ng.is_read as group_is_read,
                    STRING_AGG(notification_id::text, ',') over (partition by group_key) as grouped_ids,
                    ROW_NUMBER() over (partition by group_key order by created_at desc) as rn
                from notification_and_group_key as ng
        )
        select * from grouped
        where rn = 1
        order by created_at desc
        """,
            countQuery = """
                select count(distinct
                    case
                        when n.event_type in :eventTypes
                        then n.event_type || ':' || (n.params->>'eventId') || ':' || n.is_read::text
                        else 'single:' || n.notification_id::text
                    end
                )
                from notifications as n
                where n.user_id = :userId
        """, nativeQuery = true)
    Page<Tuple> findGroupedPage(
            @Param("userId") Long userId,
            @Param("eventTypes") Set<String> eventTypes,
            Pageable pageable
    );
}
