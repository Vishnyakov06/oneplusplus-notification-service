package com.hh.oneplusplus.model;

import com.hh.oneplusplus.dto.notification.NotificationEventType;
import com.hh.oneplusplus.dto.notification.NotificationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "message_templates")
public class MessageTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private NotificationEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationType channel;

    @Column(name = "template", nullable = false)
    private String template;

    @Column(name = "group_template")
    private String groupTemplate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getChannel() {
        return channel;
    }

    public void setChannel(NotificationType channel) {
        this.channel = channel;
    }

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

    public String getGroupTemplate() {
        return groupTemplate;
    }

    public void setGroupTemplate(String groupTemplate) {
        this.groupTemplate = groupTemplate;
    }
}
