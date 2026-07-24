INSERT INTO message_templates (event_type, channel, template) VALUES
    ('JOIN_REQUEST_SUBMITTED', 'WEB', '${actor.name} ${actor.surname} подал заявку на участие в событии "${eventTitle}"'),
    ('JOIN_REQUEST_APPROVED', 'WEB', 'Ваша заявка на участие в событии "${eventTitle}" одобрена'),
    ('JOIN_REQUEST_REJECTED', 'WEB', 'Ваша заявка на участие в событии "${eventTitle}" отклонена. ${comment:-}');