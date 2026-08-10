INSERT INTO message_templates (event_type, channel, template) VALUES
        ('EVENT_DELETE_BY_ADMIN','WEB',
         'Событие "${eventTitle}" было удалено администратором!'),
        ('BAN_USER','MAIL',
         'Вам была выдана блокировка до ${expireAt}, по причине ${reason}!'),
        ('WARNING_USER','WEB',
         'Ввм было выдано предупреждение по причине ${reason}! Предупреждение снимется автоматически в ${expireAt}'),
        ('BAN_USER_PERMANENT','MAIL',
         'Вам была выдана постоянная блокировка по причине ${reason}!')