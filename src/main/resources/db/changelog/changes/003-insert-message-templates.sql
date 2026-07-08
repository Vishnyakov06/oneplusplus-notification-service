INSERT INTO message_templates (event_type, template) VALUES
   ('WELCOME',       'Добро пожаловать в One++, ${userName} ${userSurname}!'),
   ('MEMBER_JOINED', '${actor.name} ${actor.surname} вступил в событие "${eventTitle}"'),
   ('MEMBER_LEFT',   '${actor.name} ${actor.surname} покинул событие "${eventTitle}"'),
   ('MEMBER_KICKED', 'Вы были исключены из события "${eventTitle}"'),
   ('EVENT_UPDATED', 'Событие "${eventTitle}" было обновлено'),
   ('EVENT_DELETED', 'Событие "${eventTitle}" было удалено'),
   ('INVITE',        'Вас пригласили в событие "${eventTitle}"');