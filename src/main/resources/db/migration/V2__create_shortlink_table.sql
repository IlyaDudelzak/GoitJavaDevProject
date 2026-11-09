create table shortlinks
(
    id          bigserial primary key,
    enabled     boolean not null default true,
    original_url text not null,
    short_code  varchar(8) not null unique,
    visits      bigint default 0,
    user_id     bigint not null references users(id) on delete cascade,
    created_at  timestamp not null default current_timestamp,
    edited_at  timestamp not null default current_timestamp,
    expires_at     timestamp
);