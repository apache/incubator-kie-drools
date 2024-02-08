create table definitions
(
    id       varchar2(255 char) not null,
    version  varchar2(255 char) not null,
    name     varchar2(255 char),
    source   blob,
    type     varchar2(255 char),
    endpoint varchar2(255 char),
    primary key (id, version)
);

create table definitions_addons
(
    process_id      varchar2(255 char) not null,
    process_version varchar2(255 char) not null,
    addon           varchar2(255 char) not null,
    primary key (process_id, process_version, addon)
);

create table definitions_roles
(
    process_id      varchar2(255 char) not null,
    process_version varchar2(255 char) not null,
    role            varchar2(255 char) not null,
    primary key (process_id, process_version, role)
);

alter table definitions_addons
    add constraint fk_definitions_addons_definitions
        foreign key (process_id, process_version)
            references definitions
            on delete cascade;

alter table definitions_roles
    add constraint fk_definitions_roles_definitions
        foreign key (process_id, process_version)
            references definitions
            on delete cascade;

alter table processes
    add version varchar2(255 char);