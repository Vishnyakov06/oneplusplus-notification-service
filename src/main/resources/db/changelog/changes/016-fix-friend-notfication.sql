UPDATE message_templates SET template = '{userName} ${userSurname} отправил приглашение в друзья!'
                         WHERE event_type = 'ADD_FRIEND' AND channel = 'WEB';

UPDATE message_templates SET template = '{userName} ${userSurname} удалил вас из друзей!'
WHERE event_type = 'DELETE_FRIEND' AND channel = 'WEB';

UPDATE message_templates SET template = 'Ваш друг ${userName} ${userSurname} создал новый пост!'
WHERE event_type = 'CREATE_POST' AND channel = 'WEB';

UPDATE message_templates SET template = '${userName} ${userSurname} принял ваш запрос в друзья!'
WHERE event_type = 'ACCEPT_FRIEND' AND channel = 'WEB';

UPDATE message_templates SET template = '${userName} ${userSurname} отклонил ваш запрос в друзья!'
WHERE event_type = 'REJECT_FRIEND' AND channel = 'WEB';