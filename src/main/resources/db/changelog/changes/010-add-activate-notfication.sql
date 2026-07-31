INSERT INTO message_templates (event_type, channel, template) VALUES
('ACTIVATE_ACCOUNT', 'MAIL', 'Ваша ссылка для активации: ${url}');
UPDATE message_templates SET template = 'Добро пожаловать в One++, ${userName} ${userSurname}! Ссылка для активации: ${url}'
                         WHERE event_type = 'WELCOME' AND channel = 'MAIL'