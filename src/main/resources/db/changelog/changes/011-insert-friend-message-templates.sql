INSERT INTO message_templates (event_type, channel, template) VALUES
     ('ADD_FRIEND','WEB',  'Пользователь ${userName} ${userSurname} отправил приглашение в друзья}'),
     ('DELETE_FRIEND','WEB',  'Пользователь ${userName} ${userSurname} удалил вас из друзей}'),
     ('CREATE_POST','WEB',  'Ваш друг ${userName} ${userSurname} создал новый пост!}')