create TABLE if NOT EXISTS users(
    id serial NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL);

create TABLE if NOT EXISTS categories(
    id serial NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL);

create TABLE if NOT EXISTS location(
    id serial NOT NULL PRIMARY KEY,
    latitude FLOAT  NOT NULL,
    longitude FLOAT  NOT NULL
     );

create TABLE IF NOT EXISTS events(
    id SERIAL PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    confirmed_requests INTEGER DEFAULT 0,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    participant_limit INTEGER DEFAULT 0,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN DEFAULT TRUE,
    state VARCHAR(50) NOT NULL,
    title VARCHAR(120) NOT NULL,
    views BIGINT DEFAULT 0,
    category_id INT REFERENCES categories(id),
    initiator_id INT REFERENCES users(id),
    location_id INT REFERENCES location(id)
    );

create TABLE IF NOT EXISTS compilations(
    id SERIAL PRIMARY KEY,
    pinned BOOLEAN DEFAULT FALSE,
    title VARCHAR(255) NOT NULL);

create TABLE IF NOT EXISTS requests(
    id SERIAL PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    status VARCHAR(50) NOT NULL,
    event_id INT NOT NULL REFERENCES events(id) ON delete CASCADE,
    requester_id INT NOT NULL REFERENCES users(id) ON delete CASCADE,
    UNIQUE (event_id, requester_id));



create TABLE IF NOT EXISTS compilation_events (
    compilation_id INT REFERENCES compilations(id) ON delete CASCADE,
    event_id INT REFERENCES events(id) ON delete CASCADE,
    PRIMARY KEY (compilation_id, event_id));