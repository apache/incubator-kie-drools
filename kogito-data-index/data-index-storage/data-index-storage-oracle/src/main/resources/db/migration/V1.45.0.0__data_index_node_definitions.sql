create table definitions_nodes
(
    id              varchar2(255 char) not null,
    name            varchar2(255 char),
    type            varchar2(255 char),
    unique_id       varchar2(255 char),
    process_id      varchar2(255 char) not null,
    process_version varchar2(255 char) not null,
    primary key (id, process_id, process_version)
);

create table definitions_nodes_metadata
(
    node_id         varchar2(255 char) not null,
    process_id      varchar2(255 char) not null,
    process_version varchar2(255 char) not null,
    value           varchar2(255 char),
    key             varchar2(255 char) not null,
    primary key (node_id, process_id, process_version, key)
);

alter table definitions_nodes
    add constraint fk_definitions_nodes_definitions
        foreign key (process_id, process_version)
            references definitions
            on delete cascade;

alter table definitions_nodes_metadata
    add constraint fk_definitions_nodes_metadata_definitions_nodes
        foreign key (node_id, process_id, process_version)
            references definitions_nodes
            on delete cascade;