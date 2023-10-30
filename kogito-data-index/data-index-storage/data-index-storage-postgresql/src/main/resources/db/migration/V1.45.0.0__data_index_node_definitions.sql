create table IF NOT EXISTS definitions_nodes
(
    id              varchar(255) not null,
    name            varchar(255),
    unique_id       varchar(255),
    type            varchar(255),
    process_id      varchar(255) not null,
    process_version varchar(255) not null,
    primary key (id, process_id, process_version)
);

create table IF NOT EXISTS definitions_nodes_metadata
(
    node_id         varchar(255) not null,
    process_id      varchar(255) not null,
    process_version varchar(255) not null,
    value           varchar(255),
    key             varchar(255) not null,
    primary key (node_id, process_id, process_version, key)
);
alter table if exists definitions_nodes
drop constraint if exists fk_definitions_nodes_definitions
cascade;

alter table if exists definitions_nodes
    add constraint fk_definitions_nodes_definitions
    foreign key (process_id, process_version)
    references definitions
    on
delete
cascade;

alter table if exists definitions_nodes_metadata
drop constraint if exists fk_definitions_nodes_metadata_definitions_nodes
cascade;

alter table if exists definitions_nodes_metadata
    add constraint fk_definitions_nodes_metadata_definitions_nodes
    foreign key (node_id, process_id, process_version)
    references definitions_nodes
    on
delete
cascade;
