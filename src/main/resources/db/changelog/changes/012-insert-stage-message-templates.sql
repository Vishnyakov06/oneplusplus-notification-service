INSERT INTO message_templates (event_type, channel, template) VALUES
    ('EVENT_STAGE_MANUAL','WEB',  'В ивенте, ${eventTitle}, организатор ' ||
                                  '${userName} ${userSurname} начал этап ${stageTitle}!'),
    ('EVENT_STAGE','WEB',  'В ивенте, ${eventTitle}, начался ' ||
                                  'этап ${stageTitle}!');

