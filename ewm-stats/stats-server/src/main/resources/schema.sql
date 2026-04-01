create table if not exists endpoint_hit(
id serial not null  primary key,
app VARCHAR(255) NOT NULL,
ip varchar(255) not null,
uri varchar(255) not null,
"timestamp" timestamp WITHOUT TIME ZONE not null
);