create table notifications (
    id BIGSERIAL primary key,
    notification_id UUID not null unique,
    user_id BIGINT not null,
    event_type varchar(50) not null,
    message TEXT not null,
    params JSONB,
    is_read BOOLEAN not null default false,
    created_at timestamp not null,
    read_at timestamp
);

create index idx_notifications_user_id on notifications (user_id);
create index idx_notifications_notification_id on notifications (notification_id);