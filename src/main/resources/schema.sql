--drop type if exists status;
--create type status as enum ('waiting', 'approved', 'rejected', 'canceled');

create table if not exists USERS
(
    ID    BIGINT auto_increment
        primary key,
    EMAIL CHARACTER VARYING(255) not null,
    NAME  CHARACTER VARYING(255) not null
);

create table if not exists REQUESTS
(
    ID           BIGINT auto_increment
        primary key,
    CREATED_DATE TIMESTAMP              not null,
    DESCRIPTION  CHARACTER VARYING(255) not null,
    REQUESTOR_ID BIGINT                 not null,
    constraint REQUESTS_USERS_ID_FK
        foreign key (REQUESTOR_ID) references USERS
);

create table if not exists ITEMS
(
    ID           BIGINT auto_increment
        primary key,
    IS_AVAILABLE BOOLEAN                not null,
    DESCRIPTION  CHARACTER VARYING(255) not null,
    NAME         CHARACTER VARYING(255) not null,
    OWNER_ID     BIGINT                 not null,
    REQUEST_ID   BIGINT                 not null,
    constraint ITEMS_REQUESTS_ID_FK
        foreign key (REQUEST_ID) references REQUESTS,
    constraint ITEMS_USERS_ID_FK
        foreign key (OWNER_ID) references USERS
);

create table if not exists BOOKINGS
(
    ID         BIGINT auto_increment
        primary key,
    END_DATE   TIMESTAMP,
    START_DATE TIMESTAMP,
    STATUS     CHARACTER VARYING(255),
    BOOKER_ID  BIGINT,
    ITEM_ID    BIGINT,
    constraint BOOKINGS_ITEMS_ID_FK
        foreign key (ITEM_ID) references ITEMS,
    constraint BOOKINGS_USERS_ID_FK
        foreign key (BOOKER_ID) references USERS
);

create table if not exists COMMENTS
(
    ID           BIGINT auto_increment
        primary key,
    AUTHOR_NAME  CHARACTER VARYING(255) not null,
    CREATED_DATE TIMESTAMP              not null,
    ITEM_ID      BIGINT                 not null,
    TEXT         CHARACTER VARYING(255) not null,
    constraint COMMENTS_ITEMS_ID_FK
        foreign key (ITEM_ID) references ITEMS
);

create unique index USERS_EMAIL_UINDEX
    on USERS (EMAIL);

