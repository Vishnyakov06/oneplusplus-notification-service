INSERT INTO message_templates (event_type, channel, template) VALUES
                     ('EVENT_COMPLETED','WEB',
                       'Событие "${eventTitle}" было завершено!'),
                     ('EVENT_REOPEN','WEB',
                      'Событие "${eventTitle}" было возобновлено!'),
                     ('EVENT_ARCHIVED','WEB',
                      'Событие "${eventTitle}" было перенесено в архив!')