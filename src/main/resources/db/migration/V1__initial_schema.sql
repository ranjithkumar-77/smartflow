create table app_user (
    id bigserial primary key,
    name varchar(255) not null,
    email varchar(255) not null unique,
    password varchar(255) not null,
    phone varchar(255) not null,
    role varchar(255) not null
);

create table skill (
    id bigserial primary key,
    name varchar(255) not null unique,
    description varchar(255) not null
);

create table technician (
    id bigserial primary key,
    user_id bigint not null unique references app_user(id),
    availability_status varchar(255) not null,
    location varchar(255) not null,
    latitude double precision not null,
    longitude double precision not null,
    rating double precision,
    current_jobs integer not null
);

create table service_request (
    id bigserial primary key,
    title varchar(255) not null,
    description text not null,
    category varchar(255) not null,
    location varchar(255) not null,
    latitude double precision,
    longitude double precision,
    priority varchar(255) not null,
    status varchar(255) not null,
    customer_id bigint not null references app_user(id),
    assigned_technician_id bigint references technician(id),
    created_at timestamp,
    updated_at timestamp,
    version bigint
);

create table technician_skill (
    id bigserial primary key,
    technician_id bigint not null references technician(id),
    skill_id bigint not null references skill(id),
    constraint uk_technician_skill unique (technician_id, skill_id)
);

create table request_offer (
    id bigserial primary key,
    request_id bigint not null references service_request(id),
    technician_id bigint not null references technician(id),
    status varchar(255) not null,
    offered_at timestamp,
    responded_at timestamp,
    version bigint
);

create table review (
    id bigserial primary key,
    service_request_id bigint not null unique references service_request(id),
    customer_id bigint not null references app_user(id),
    technician_id bigint not null references technician(id),
    rating integer not null,
    comment text,
    created_at timestamp not null
);
