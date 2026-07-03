CREATE TABLE notification_deliveries (
    id BIGSERIAL primary key ,
    notification_id UUID not null ,
    channel varchar(50) not null ,
    constraint uq_notification_deliveries_notification_channel unique (notification_id, channel)
);