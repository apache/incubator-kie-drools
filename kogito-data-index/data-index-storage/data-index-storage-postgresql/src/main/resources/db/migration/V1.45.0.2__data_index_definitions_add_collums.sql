create table definitions_annotations
(
    value           varchar(255) not null,
    process_id      varchar(255) not null,
    process_version varchar(255) not null,
    primary key (value, process_id, process_version)
);

create table definitions_metadata
(
    process_id      varchar(255) not null,
    process_version varchar(255) not null,
    value           varchar(255),
    key             varchar(255) not null,
    primary key (process_id, process_version, key)
);

alter table if exists definitions_annotations
    add constraint fk_definitions_annotations
    foreign key (process_id, process_version)
    references definitions
    on
delete
cascade;

alter table if exists definitions_metadata
    add constraint fk_definitions_metadata
    foreign key (process_id, process_version)
    references definitions
    on
delete
cascade;

alter table if exists definitions
    add column description varchar (255);