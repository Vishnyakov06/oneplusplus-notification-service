package com.hh.oneplusplus.repository;

import com.hh.oneplusplus.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long id);
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    boolean existsByNotificationId(UUID notificationId);
}
