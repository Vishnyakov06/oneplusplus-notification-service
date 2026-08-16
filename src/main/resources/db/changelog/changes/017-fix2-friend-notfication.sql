UPDATE message_templates SET template = '${userName} ${userSurname} отправил приглашение в друзья!'
                         WHERE event_type = 'ADD_FRIEND' AND channel = 'WEB';

UPDATE message_templates SET template = '${userName} ${userSurname} удалил вас из друзей!'
WHERE event_type = 'DELETE_FRIEND' AND channel = 'WEB';
