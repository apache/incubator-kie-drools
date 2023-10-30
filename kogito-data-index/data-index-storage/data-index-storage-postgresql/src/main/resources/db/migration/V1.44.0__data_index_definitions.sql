create table IF NOT EXISTS definitions
(
    id       varchar(255) not null,
    version  varchar(255) not null,
    name     varchar(255),
    type     varchar(255),
    source   bytea,
    endpoint varchar(255),
    primary key (id, version)
);

create table IF NOT EXISTS definitions_addons
(
    process_id      varchar(255) not null,
    process_version varchar(255) not null,
    addon           varchar(255) not null,
    primary key (process_id, process_version, addon)
);

create table IF NOT EXISTS definitions_roles
(
    process_id      varchar(255) not null,
    process_version varchar(255) not null,
    role            varchar(255) not null,
    primary key (process_id, process_version, role)
);

alter table if exists definitions_addons
drop constraint if exists fk_definitions_addons_definitions
cascade;

alter table if exists definitions_addons
    add constraint fk_definitions_addons_definitions
    foreign key (process_id, process_version)
    references definitions
    on
delete
cascade;

alter table if exists definitions_roles
drop constraint if exists fk_definitions_roles_definitions
cascade;

alter table if exists definitions_roles
    add constraint fk_definitions_roles_definitions
    foreign key (process_id, process_version)
    references definitions
    on
delete
cascade;

alter table if exists processes
    add column IF NOT EXISTS version varchar (255);
