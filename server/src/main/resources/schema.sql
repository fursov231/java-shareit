--drop type if exists status;
--create type status as enum ('waiting', 'approved', 'rejected', 'canceled');

create table if not exists users
(
    id    bigint auto_increment
        primary key,
    email varchar(255) not null,
    name  varchar(255) not null
);


create table if not exists items
(
    id           bigint auto_increment
        primary key,
    is_available boolean      not null,
    description  varchar(255) not null,
    name         varchar(255) not null,
    owner_id     bigint       not null
        constraint items_users_id_fk
            references users,
    request_id   bigint       not null
);


create table if not exists bookings
(
    id         bigint auto_increment
        primary key,
    end_date   timestamp    not null,
    start_date timestamp    not null,
    status     varchar(255) not null,
    booker_id  bigint       not null
        constraint bookings_users_id_fk
            references users,
    item_id    bigint       not null
        constraint bookings_items_id_fk
            references items
);

create table if not exists comments
(
    id           bigint auto_increment
        primary key,
    author_name  varchar(255) not null,
    created_date timestamp    not null,
    item_id      bigint       not null
        constraint comments_items_id_fk
            references items,
    text         varchar(255) not null
);


create table if not exists requests
(
    id           bigint auto_increment
        primary key,
    created_date timestamp    not null,
    description  varchar(255) not null,
    requestor_id bigint       not null
        constraint requests_users_id_fk
            references users
);


create unique index if not exists users_email_uindex
    on users (email);

