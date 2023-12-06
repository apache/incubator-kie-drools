create table definitions_annotations
(
    value           varchar2(255 char) not null,
    process_id      varchar2(255 char) not null,
    process_version varchar2(255 char) not null,
    primary key (value, process_id, process_version)
);

create table definitions_metadata
(
    process_id      varchar2(255 char) not null,
    process_version varchar2(255 char) not null,
    value           varchar2(255 char),
    key             varchar2(255 char) not null,
    primary key (process_id, process_version, key)
);

alter table definitions_annotations
    add constraint fk_definitions_annotations
        foreign key (process_id, process_version)
            references definitions
            on delete cascade;

alter table definitions_metadata
    add constraint fk_definitions_metadata
        foreign key (process_id, process_version)
            references definitions
            on delete cascade;

alter table definitions
    add (description varchar2(255 char));

