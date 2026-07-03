package com.hh.oneplusplus.model;

import com.hh.oneplusplus.dto.notification.NotificationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;


import java.util.UUID;


@Entity
@Table(name = "notification_deliveries")
public class NotificationDelivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationType channel;

    @Column(name = "notification_id", nullable = false)
    private UUID notificationId;

    public NotificationDelivery() {
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    public Long getId() {
        return id;
    }

    public NotificationType getChannel() {
        return channel;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNotificationId(UUID notificationId) {
        this.notificationId = notificationId;
    }

    public void setChannel(NotificationType channel) {
        this.channel = channel;
    }
}
