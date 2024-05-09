/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

create table IF NOT EXISTS attachments
(
    id         varchar(255) not null,
    content    varchar(255),
    name       varchar(255),
    updated_at timestamp,
    updated_by varchar(255),
    task_id    varchar(255) not null,
    primary key (id)
);

create table IF NOT EXISTS comments
(
    id         varchar(255) not null,
    content    varchar(255),
    updated_at timestamp,
    updated_by varchar(255),
    task_id    varchar(255) not null,
    primary key (id)
);

create table IF NOT EXISTS jobs
(
    id                       varchar(255) not null,
    callback_endpoint        varchar(255),
    endpoint                 varchar(255),
    execution_counter        int4,
    expiration_time          timestamp,
    last_update              timestamp,
    node_instance_id         varchar(255),
    priority                 int4,
    process_id               varchar(255),
    process_instance_id      varchar(255),
    repeat_interval          int8,
    repeat_limit             int4,
    retries                  int4,
    root_process_id          varchar(255),
    root_process_instance_id varchar(255),
    scheduled_id             varchar(255),
    status                   varchar(255),
    primary key (id)
);

create table IF NOT EXISTS milestones
(
    id                  varchar(255) not null,
    process_instance_id varchar(255) not null,
    name                varchar(255),
    status              varchar(255),
    primary key (id, process_instance_id)
);

create table IF NOT EXISTS nodes
(
    id                  varchar(255) not null,
    definition_id       varchar(255),
    enter               timestamp,
    exit                timestamp,
    name                varchar(255),
    node_id             varchar(255),
    type                varchar(255),
    process_instance_id varchar(255) not null,
    primary key (id)
);

create table IF NOT EXISTS processes
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
    state                      int4,
    variables                  jsonb,
    primary key (id)
);

create table IF NOT EXISTS processes_addons
(
    process_id varchar(255) not null,
    addon      varchar(255) not null,
    primary key (process_id, addon)
);

create table IF NOT EXISTS processes_roles
(
    process_id varchar(255) not null,
    role       varchar(255) not null,
    primary key (process_id, role)
);

create table IF NOT EXISTS tasks
(
    id                       varchar(255) not null,
    actual_owner             varchar(255),
    completed                timestamp,
    description              varchar(255),
    endpoint                 varchar(255),
    inputs                   jsonb,
    last_update              timestamp,
    name                     varchar(255),
    outputs                  jsonb,
    priority                 varchar(255),
    process_id               varchar(255),
    process_instance_id      varchar(255),
    reference_name           varchar(255),
    root_process_id          varchar(255),
    root_process_instance_id varchar(255),
    started                  timestamp,
    state                    varchar(255),
    primary key (id)
);

create table IF NOT EXISTS tasks_admin_groups
(
    task_id  varchar(255) not null,
    group_id varchar(255) not null,
    primary key (task_id, group_id)
);

create table IF NOT EXISTS tasks_admin_users
(
    task_id varchar(255) not null,
    user_id varchar(255) not null,
    primary key (task_id, user_id)
);

create table IF NOT EXISTS tasks_excluded_users
(
    task_id varchar(255) not null,
    user_id varchar(255) not null,
    primary key (task_id, user_id)
);

create table IF NOT EXISTS tasks_potential_groups
(
    task_id  varchar(255) not null,
    group_id varchar(255) not null,
    primary key (task_id, group_id)
);

create table IF NOT EXISTS tasks_potential_users
(
    task_id varchar(255) not null,
    user_id varchar(255) not null,
    primary key (task_id, user_id)
);

alter table if exists attachments
  drop constraint if exists fk_attachments_tasks
cascade;

alter table if exists attachments
    add constraint fk_attachments_tasks
    foreign key (task_id)
    references tasks
    on
delete
cascade;

alter table if exists comments
  drop constraint if exists fk_comments_tasks
cascade;

alter table if exists comments
    add constraint fk_comments_tasks
    foreign key (task_id)
    references tasks
    on
delete
cascade;

alter table if exists milestones
drop constraint if exists fk_milestones_process
cascade;

alter table if exists milestones
    add constraint fk_milestones_process
    foreign key (process_instance_id)
    references processes
    on
delete
cascade;

alter table if exists nodes
drop constraint if exists fk_nodes_process
cascade;

alter table if exists nodes
    add constraint fk_nodes_process
    foreign key (process_instance_id)
    references processes
    on
delete
cascade;

alter table if exists processes_addons
drop constraint if exists fk_processes_addons_processes
cascade;

alter table if exists processes_addons
    add constraint fk_processes_addons_processes
    foreign key (process_id)
    references processes
    on
delete
cascade;

alter table if exists processes_roles
drop constraint if exists fk_processes_roles_processes
cascade;

alter table if exists processes_roles
    add constraint fk_processes_roles_processes
    foreign key (process_id)
    references processes
    on
delete
cascade;

alter table if exists tasks_admin_groups
drop constraint if exists fk_tasks_admin_groups_tasks
cascade;

alter table if exists tasks_admin_groups
    add constraint fk_tasks_admin_groups_tasks
    foreign key (task_id)
    references tasks
    on
delete
cascade;

alter table if exists tasks_admin_users
drop constraint if exists fk_tasks_admin_users_tasks
cascade;

alter table if exists tasks_admin_users
    add constraint fk_tasks_admin_users_tasks
    foreign key (task_id)
    references tasks
    on
delete
cascade;

alter table if exists tasks_excluded_users
drop constraint if exists fk_tasks_excluded_users_tasks
cascade;

alter table if exists tasks_excluded_users
    add constraint fk_tasks_excluded_users_tasks
    foreign key (task_id)
    references tasks
    on
delete
cascade;

alter table if exists tasks_potential_groups
drop constraint if exists fk_tasks_potential_groups_tasks
cascade;

alter table if exists tasks_potential_groups
    add constraint fk_tasks_potential_groups_tasks
    foreign key (task_id)
    references tasks
    on
delete
cascade;

alter table if exists tasks_potential_users
drop constraint if exists fk_tasks_potential_users_tasks
cascade;

alter table if exists tasks_potential_users
    add constraint fk_tasks_potential_users_tasks
    foreign key (task_id)
    references tasks
    on
delete
cascade;
