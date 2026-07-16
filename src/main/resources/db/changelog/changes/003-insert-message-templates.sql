INSERT INTO message_templates (event_type, channel, template) VALUES
    ('WELCOME','MAIL',  'Добро пожаловать в One++, ${userName} ${userSurname}!'),
    ('MEMBER_JOINED','WEB',  '${actor.name} ${actor.surname} вступил в событие "${eventTitle}"'),
    ('MEMBER_LEFT', 'WEB',  '${actor.name} ${actor.surname} покинул событие "${eventTitle}"'),
    ('MEMBER_KICKED','WEB',  'Вы были исключены из события "${eventTitle}"'),
    ('EVENT_UPDATED','WEB',  'Событие "${eventTitle}" было обновлено'),
    ('EVENT_DELETED','WEB',  'Событие "${eventTitle}" было удалено'),
    ('INVITE','WEB',  'Вас пригласили в событие "${eventTitle}"'),
    ('INVITE','MAIL', 'Вас пригласили в событие "${eventTitle}": ${inviteTarget}');