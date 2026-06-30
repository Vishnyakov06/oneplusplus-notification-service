package com.hh.oneplusplus.model;

import com.hh.oneplusplus.dto.notification.NotificationEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
@Table(name = "message_templates")
public class MessageTemplate {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private NotificationEventType eventType;

    @Column(name = "template", nullable = false)
    private String template;

    public MessageTemplate() {
    }

    public NotificationEventType getEventType() {
        return eventType;
    }

    public String getTemplate() {
        return template;
    }

    public void setEventType(NotificationEventType eventType) {
        this.eventType = eventType;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
