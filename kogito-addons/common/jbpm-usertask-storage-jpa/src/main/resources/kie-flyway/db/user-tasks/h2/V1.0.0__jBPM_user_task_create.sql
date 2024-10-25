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

create table jbpm_user_tasks (
    id                    varchar(50) not null,
    user_task_id          varchar(255),
    task_priority         varchar(50),
    actual_owner          varchar(255),
    task_description      varchar(255),
    status                varchar(255),
    termination_type      varchar(255),
    external_reference_id varchar(255),
    task_name             varchar(255),
    primary key (id)
);

create table jbpm_user_tasks_potential_users(
    task_id varchar(50) not null,
    user_id varchar(255) not null,
    primary key (task_id, user_id)
);

create table jbpm_user_tasks_potential_groups(
    task_id  varchar(50) not null,
    group_id varchar(255) not null,
    primary key (task_id, group_id)
);

create table jbpm_user_tasks_admin_users (
    task_id varchar(50) not null,
    user_id varchar(255) not null,
    primary key (task_id, user_id)
);

create table jbpm_user_tasks_admin_groups (
    task_id  varchar(50) not null,
    group_id varchar(255) not null,
    primary key (task_id, group_id)
);

create table jbpm_user_tasks_excluded_users(
    task_id varchar(50) not null,
    user_id varchar(255) not null,
    primary key (task_id, user_id)
);

create table jbpm_user_tasks_attachments (
    id          varchar(50) not null,
    name        varchar(255),
    updated_by  varchar(255),
    updated_at  timestamp,
    url         varchar(255),
    task_id     varchar(50) not null,
    primary key (id)
);

create table jbpm_user_tasks_comments (
    id         varchar(50) not null,
    updated_by varchar(255),
    updated_at timestamp,
    comment    varchar(255),
    task_id    varchar(50) not null,
    primary key (id)
);

create table jbpm_user_tasks_inputs (
    task_id         varchar(50) not null,
    input_name      varchar(255) not null,
    input_value     varbinary(max),
    java_type       varchar(255),
    primary key (task_id, input_name)
);

create table jbpm_user_tasks_outputs (
    task_id         varchar(50) not null,
    output_name     varchar(255) not null,
    output_value    varbinary(max),
    java_type       varchar(255),
    primary key (task_id, output_name)
);

create table jbpm_user_tasks_metadata (
    task_id         varchar(50) not null,
    metadata_name   varchar(255) not null,
    metadata_value  varchar(512),
    java_type       varchar(255),
    primary key (task_id, metadata_name)
);

alter table if exists jbpm_user_tasks_potential_users
drop constraint if exists fk_jbpm_user_tasks_potential_users_tid cascade;

alter table if exists jbpm_user_tasks_potential_users
add constraint fk_jbpm_user_fk_tasks_potential_users_tid foreign key (task_id) references jbpm_user_tasks(id) on delete cascade;

alter table if exists jbpm_user_potential_groups
drop constraint if exists fk_jbpm_user_tasks_potential_groups_tid cascade;

alter table if exists jbpm_user_tasks_potential_groups
add constraint fk_jbpm_user_tasks_potential_groups_tid foreign key (task_id) references jbpm_user_tasks(id) on delete cascade;

alter table if exists jbpm_user_tasks_admin_users
drop constraint if exists fk_jbpm_user_tasks_admin_users_tid cascade;

alter table if exists jbpm_user_tasks_admin_users
add constraint fk_jbpm_user_tasks_admin_users_tid foreign key (task_id) references jbpm_user_tasks(id) on delete cascade;

alter table if exists jbpm_user_tasks_admin_groups
drop constraint if exists fk_jbpm_user_tasks_admin_groups_tid cascade;

alter table if exists jbpm_user_tasks_admin_groups
add constraint fk_jbpm_user_tasks_admin_groups_tid foreign key (task_id) references jbpm_user_tasks(id) on delete cascade;

alter table if exists jbpm_user_tasks_excluded_users
drop constraint if exists fk_jbpm_user_tasks_excluded_users_tid cascade;

alter table if exists jbpm_user_tasks_excluded_users
add constraint fk_jbpm_user_tasks_excluded_users_tid foreign key (task_id) references jbpm_user_tasks(id) on delete cascade;

alter table if exists jbpm_user_tasks_attachments
drop constraint if exists fk_user_tasks_attachments_tid cascade;

alter table if exists jbpm_user_tasks_attachments
add constraint fk_user_tasks_attachments_tid foreign key (task_id) references jbpm_user_tasks(id) on delete cascade;

alter table if exists jbpm_user_tasks_comments
drop constraint if exists fk_user_tasks_comments_tid cascade;

alter table if exists jbpm_user_tasks_comments
add constraint fk_user_tasks_comments_tid foreign key (task_id) references jbpm_user_tasks(id) on delete cascade;

alter table if exists jbpm_user_tasks_inputs
drop constraint if exists fk_jbpm_user_tasks_inputs_tid cascade;

alter table if exists jbpm_user_tasks_inputs
add constraint fk_jbpm_user_tasks_inputs_tid foreign key (task_id) references jbpm_user_tasks(id) on delete cascade;

alter table if exists jbpm_user_tasks_outputs
drop constraint if exists fk_jbpm_user_tasks_outputs_tid cascade;

alter table if exists jbpm_user_tasks_outputs
add constraint fk_jbpm_user_tasks_outputs_tid foreign key (task_id) references jbpm_user_tasks(id) on delete cascade;

alter table if exists jbpm_user_tasks_metadata
drop constraint if exists fk_jbpm_user_tasks_metadata_tid cascade;

alter table if exists jbpm_user_tasks_metadata
add constraint fk_jbpm_user_tasks_metadata_tid foreign key (task_id) references jbpm_user_tasks(id) on delete cascade;


