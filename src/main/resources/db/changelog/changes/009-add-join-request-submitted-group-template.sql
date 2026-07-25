UPDATE message_templates
SET group_template = '${params.actor.name} ${params.actor.surname} и ещё ${othersCount} ${othersWord} подали заявки на участие в событии "${params.eventTitle}"'
WHERE event_type = 'JOIN_REQUEST_SUBMITTED';