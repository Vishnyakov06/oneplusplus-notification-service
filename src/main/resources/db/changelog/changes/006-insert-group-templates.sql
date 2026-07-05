UPDATE message_templates
SET group_template = '${params.actor.name} ${params.actor.surname} и ещё ${othersCount} ${othersWord} вступили в событие "${params.eventTitle}"'
WHERE event_type = 'MEMBER_JOINED';

UPDATE message_templates
SET group_template = '${params.actor.name} ${params.actor.surname} и ещё ${othersCount} ${othersWord} покинули событие "${params.eventTitle}"'
WHERE event_type = 'MEMBER_LEFT';
