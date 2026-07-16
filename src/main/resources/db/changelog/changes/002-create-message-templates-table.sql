create table message_templates
(
    id bigserial primary key,
    event_type varchar(50) not null,
    channel varchar(50) not null,
    template TEXT not null,
    constraint uq_message_templates_event_channel unique (event_type, channel)
);
