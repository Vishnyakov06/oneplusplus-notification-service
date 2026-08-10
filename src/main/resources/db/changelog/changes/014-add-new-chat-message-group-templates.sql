UPDATE message_templates
SET group_template = 'У вас ${messageCount} ${messageWord} из события "${params.eventTitle}"'
WHERE event_type = 'NEW_CHAT_MESSAGE_EVENT';

UPDATE message_templates
SET group_template = '${params.actor.name} ${params.actor.surname}: ${messageCount} ${messageWord} в чате модерации заявки на событие "${params.eventTitle}"'
WHERE event_type = 'NEW_CHAT_MESSAGE_MODERATION';

UPDATE message_templates
SET group_template = '${params.actor.name} ${params.actor.surname}: ${messageCount} ${messageWord}'
WHERE event_type = 'NEW_CHAT_MESSAGE_PERSONAL';
