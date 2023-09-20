CREATE SCHEMA if not exists real_estate AUTHORIZATION postgres;
CREATE SCHEMA if not exists agency AUTHORIZATION postgres;

create table if not exists real_estate.address (
	id SERIAL primary key,
    apartment_number varchar(5),
    city varchar(32),
    floor int4,
    house_number varchar(5),
    region varchar(32),
    street varchar(32)
);

create table if not exists real_estate.real_estate (
    id SERIAL PRIMARY KEY,
    area float8 constraint area_size_check check (area>0),
    commission int4 constraint commission_size_check check (commission <= price),
    number_of_rooms int4 constraint rooms_number_check check (number_of_rooms>0),
    price float8,
    type varchar(10),
    address_id integer references real_estate.address (id)
);


create table if not exists agency.contracts (
    internal_id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    real_estate_id INTEGER,
    creation_date TIMESTAMP,
    approved BOOLEAN,
    client_name varchar(255),
    employee_name varchar(255)
);