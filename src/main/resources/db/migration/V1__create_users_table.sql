create table users
(
    id          bigserial primary key,
    username    varchar(255) not null unique,
    password    varchar(255) not null,
    created_at timestamp    not null default current_timestamp
);