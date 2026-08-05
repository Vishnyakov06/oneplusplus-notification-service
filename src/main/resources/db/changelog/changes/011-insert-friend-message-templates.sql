INSERT INTO message_templates (event_type, channel, template) VALUES
     ('ADD_FRIEND','WEB',  '${userName} ${userSurname} отправил приглашение в друзья}'),
     ('DELETE_FRIEND','WEB',  '${userName} ${userSurname} удалил вас из друзей}'),
     ('CREATE_POST','WEB',  'Ваш друг ${userName} ${userSurname} создал новый пост!}'),
     ('ACCEPT_FRIEND','WEB',  '${userName} ${userSurname} принял ваш запрос в друзья!}'),
     ('REJECT_FRIEND','WEB',  '${userName} ${userSurname} отклонил ваш запрос в друзья!}')
