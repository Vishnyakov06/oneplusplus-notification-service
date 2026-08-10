INSERT INTO message_templates (event_type, channel, template) VALUES
    ('NEW_CHAT_MESSAGE_EVENT', 'WEB', '${actor.name} ${actor.surname} написал(а) в чате события "${eventTitle}": ${messagePreview}'),
    ('NEW_CHAT_MESSAGE_MODERATION', 'WEB', '${actor.name} ${actor.surname} написал(а) в чате модерации заявки на событие "${eventTitle}": ${messagePreview}'),
    ('NEW_CHAT_MESSAGE_PERSONAL', 'WEB', '${actor.name} ${actor.surname}: ${messagePreview}');