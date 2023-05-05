DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS events_compilations CASCADE;
DROP TABLE IF EXISTS requests CASCADE;

CREATE TABLE users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name    VARCHAR(100),
    email   VARCHAR UNIQUE
);

CREATE TABLE categories
(
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255) UNIQUE
);

CREATE TABLE events
(
    event_id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation                VARCHAR(512),
    description               VARCHAR(1024),
    initiator_id              BIGINT REFERENCES users (user_id),
    category_id               BIGINT REFERENCES categories (category_id),
    confirmed_requests        BIGINT,
    created_on                TIMESTAMP WITHOUT TIME ZONE,
    event_date                TIMESTAMP WITHOUT TIME ZONE,
    location_longitude        DOUBLE PRECISION,
    location_latitude         DOUBLE PRECISION,
    paid                      BOOLEAN,
    participant_limit         INTEGER,
    available_to_participants BOOLEAN,
    published_on              TIMESTAMP WITHOUT TIME ZONE,
    request_moderation        BOOLEAN,
    state                     VARCHAR(255),
    title                     VARCHAR(255),
    views                     BIGINT
);

CREATE TABLE compilations
(
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title          VARCHAR(255),
    pinned         BOOLEAN
);

CREATE TABLE events_compilations
(
    event_id       BIGINT,
    compilation_id BIGINT,
    PRIMARY KEY (event_id, compilation_id)
);

CREATE TABLE requests
(
    request_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created      TIMESTAMP WITHOUT TIME ZONE,
    event_id     BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status       VARCHAR(20),
    CONSTRAINT UNQ_REQUEST UNIQUE (event_id, requester_id)
);
