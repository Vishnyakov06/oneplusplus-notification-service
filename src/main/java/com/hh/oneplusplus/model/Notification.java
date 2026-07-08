package com.hh.oneplusplus.model;

import com.hh.oneplusplus.dto.notification.NotificationEventType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id", nullable = false, unique = true)
    private UUID notificationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private NotificationEventType eventType;

    @Column(name = "message", nullable = false)
    private String message;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "params", columnDefinition = "jsonb")
    private Map<String, Object> params;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "readAt", nullable = true)
    private Instant readAt;

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    public Notification() {
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    public Long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getMessage() {
        return message;
    }

    public NotificationEventType getEventType() {
        return eventType;
    }

    public Long getUserId() {
        return userId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNotificationId(UUID notificationId) {
        this.notificationId = notificationId;
    }

    public void setEventType(NotificationEventType eventType) {
        this.eventType = eventType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
