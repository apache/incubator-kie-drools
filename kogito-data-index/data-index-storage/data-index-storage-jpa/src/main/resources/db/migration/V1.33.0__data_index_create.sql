create table attachments
(
    id         varchar(255) not null,
    content    varchar(255),
    name       varchar(255),
    updated_at timestamp,
    updated_by varchar(255),
    task_id    varchar(255) not null,
    CONSTRAINT attachment_pk PRIMARY KEY (id)
);

create table comments
(
    id         varchar(255) not null,
    content    varchar(255),
    updated_at timestamp,
    updated_by varchar(255),
    task_id    varchar(255) not null,
    CONSTRAINT comment_pk PRIMARY KEY (id)
);

create table jobs
(
    id                       varchar(255) not null,
    callback_endpoint        varchar(255),
    endpoint                 varchar(255),
    execution_counter        NUMBER(10),
    expiration_time          timestamp,
    last_update              timestamp,
    node_instance_id         varchar(255),
    priority                 NUMBER(10),
    process_id               varchar(255),
    process_instance_id      varchar(255),
    repeat_interval          NUMBER(19),
    repeat_limit             NUMBER(10),
    retries                  NUMBER(10),
    root_process_id          varchar(255),
    root_process_instance_id varchar(255),
    scheduled_id             varchar(255),
    status                   varchar(255),
    CONSTRAINT jobs_pk PRIMARY KEY (id)
);

create table milestones
(
    id                  varchar(255) not null,
    process_instance_id varchar(255) not null,
    name                varchar(255),
    status              varchar(255),
    CONSTRAINT milestones_pk PRIMARY KEY (id, process_instance_id)
);

create table nodes
(
    id                  varchar(255) not null,
    definition_id       varchar(255),
    enter               timestamp,
    exit                timestamp,
    name                varchar(255),
    node_id             varchar(255),
    type                varchar(255),
    process_instance_id varchar(255) not null,
    CONSTRAINT nodes_pk PRIMARY KEY (id)
);

create table processes
(
    id                         varchar(255) not null,
    business_key               varchar(255),
    end_time                   timestamp,
    endpoint                   varchar(255),
    message                    varchar(255),
    node_definition_id         varchar(255),
    last_update_time           timestamp,
    parent_process_instance_id varchar(255),
    process_id                 varchar(255),
    process_name               varchar(255),
    root_process_id            varchar(255),
    root_process_instance_id   varchar(255),
    start_time                 timestamp,
    state                      NUMBER(10),
    variables                  blob,
    CONSTRAINT processes_pk PRIMARY KEY (id),
    CONSTRAINT processes_variables_json CHECK (variables IS JSON)
);

create table processes_addons
(
    process_id varchar(255) not null,
    addon      varchar(255) not null,
    CONSTRAINT processes_addons_pk PRIMARY KEY (process_id, addon)
);

create table processes_roles
(
    process_id varchar(255) not null,
    role       varchar(255) not null,
    CONSTRAINT processes_roles_pk PRIMARY KEY (process_id, role)
);

create table tasks
(
    id                       varchar(255) not null,
    actual_owner             varchar(255),
    completed                timestamp,
    description              varchar(255),
    endpoint                 varchar(255),
    inputs                   blob,
    last_update              timestamp,
    name                     varchar(255),
    outputs                  blob,
    priority                 varchar(255),
    process_id               varchar(255),
    process_instance_id      varchar(255),
    reference_name           varchar(255),
    root_process_id          varchar(255),
    root_process_instance_id varchar(255),
    started                  timestamp,
    state                    varchar(255),
    CONSTRAINT tasks_pk PRIMARY KEY (id),
    CONSTRAINT inputs_json CHECK (inputs IS JSON),
    CONSTRAINT outputs_json CHECK (outputs IS JSON)
);
create table tasks_admin_groups
(
    task_id  varchar(255) not null,
    group_id varchar(255) not null,
    CONSTRAINT tasks_admin_groups_pk PRIMARY KEY (task_id, group_id)
);

create table tasks_admin_users
(
    task_id varchar(255) not null,
    user_id varchar(255) not null,
    CONSTRAINT tasks_admin_users_pk PRIMARY KEY (task_id, user_id)
);

create table tasks_excluded_users
(
    task_id varchar(255) not null,
    user_id varchar(255) not null,
    CONSTRAINT tasks_excluded_users_pk PRIMARY KEY (task_id, user_id)
);

create table tasks_potential_groups
(
    task_id  varchar(255) not null,
    group_id varchar(255) not null,
    CONSTRAINT tasks_potential_groups_pk PRIMARY KEY (task_id, group_id)
);

create table tasks_potential_users
(
    task_id varchar(255) not null,
    user_id varchar(255) not null,
    CONSTRAINT tasks_potential_users_pk PRIMARY KEY (task_id, user_id)
);

ALTER TABLE attachments
    ADD CONSTRAINT fk_attachments_tasks
        FOREIGN KEY (task_id)
            REFERENCES tasks (id)
            on delete cascade;

ALTER TABLE comments
    ADD CONSTRAINT fk_comments_tasks
        FOREIGN KEY (task_id)
            REFERENCES tasks (id)
            on delete cascade;

ALTER TABLE milestones
    ADD CONSTRAINT fk_milestones_process
        FOREIGN KEY (process_instance_id)
            REFERENCES processes (id)
            on delete cascade;

ALTER TABLE nodes
    ADD CONSTRAINT fk_nodes_process
        FOREIGN KEY (process_instance_id)
            REFERENCES processes (id)
            on delete cascade;

ALTER TABLE processes_addons
    ADD CONSTRAINT fk_processes_addons_processes
        FOREIGN KEY (process_id)
            REFERENCES processes (id)
            on delete cascade;

ALTER TABLE processes_roles
    ADD CONSTRAINT fk_processes_roles_processes
        FOREIGN KEY (process_id)
            REFERENCES processes (id)
            on delete cascade;

ALTER TABLE tasks_admin_groups
    ADD CONSTRAINT fk_tasks_admin_groups_tasks
        FOREIGN KEY (task_id)
            REFERENCES tasks (id)
            on delete cascade;

ALTER TABLE tasks_admin_users
    ADD CONSTRAINT fk_tasks_admin_users_tasks
        FOREIGN KEY (task_id)
            REFERENCES tasks (id)
            on delete cascade;

ALTER TABLE tasks_excluded_users
    ADD CONSTRAINT fk_tasks_excluded_users_tasks
        FOREIGN KEY (task_id)
            REFERENCES tasks (id)
            on delete cascade;

ALTER TABLE tasks_potential_groups
    ADD CONSTRAINT fk_tasks_potential_groups_tasks
        FOREIGN KEY (task_id)
            REFERENCES tasks (id)
            on delete cascade;

ALTER TABLE tasks_potential_users
    ADD CONSTRAINT fk_tasks_potential_users_tasks
        FOREIGN KEY (task_id)
            REFERENCES tasks (id)
            on delete cascade;
