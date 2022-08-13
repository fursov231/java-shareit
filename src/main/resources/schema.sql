drop type if exists status;
create type status as enum ('waiting', 'approved', 'rejected', 'canceled');

create table if not exists bookings
(
    id         bigserial
        primary key,
    booker_id  bigint,
    end_date   timestamp,
    item_id    bigint,
    start_date timestamp,
    status     varchar(255)
);

alter table bookings
    owner to root;

create table if not exists comments
(
    id           bigserial
        primary key,
    author_name  varchar(64),
    created_date timestamp,
    item_id      bigint,
    text         varchar(255)
);

alter table comments
    owner to root;

create table if not exists items
(
    id           bigserial
        primary key,
    is_available boolean,
    description  varchar(255),
    name         varchar(255),
    owner_id     bigint,
    request_id   bigint
);

alter table items
    owner to root;

create table if not exists requests
(
    id           bigserial
        primary key,
    created_date timestamp,
    description  varchar(255),
    requestor_id bigint
);

alter table requests
    owner to root;

create table if not exists users
(
    id    bigserial
        primary key,
    email varchar(255),
    name  varchar(255)
);

alter table users
    owner to root;

create unique index if not exists users_email_uindex
    on users (email);

