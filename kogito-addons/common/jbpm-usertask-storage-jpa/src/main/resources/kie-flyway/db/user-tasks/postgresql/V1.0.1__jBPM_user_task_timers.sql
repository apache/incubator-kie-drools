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
create table jbpm_user_tasks_deadline (
    id                  int,
    task_id             varchar(50) not null,
    notification_type   varchar(255) not null,
    notification_value  bytea,
    java_type           varchar(255),
    primary key (id)
);


create table jbpm_user_tasks_reassignment (
    id                  int,
    task_id             varchar(50) not null,
    reassignment_type   varchar(255) not null,
    reassignment_value  bytea,
    java_type           varchar(255),
    primary key (id)
);


create table jbpm_user_tasks_deadline_timer (
    task_id             varchar(50) not null,
    notification_job_id varchar(255) not null,
    notification_type   varchar(255) not null,
    notification_value  bytea,
    java_type           varchar(255),
    primary key (task_id, notification_job_id)
);



create table jbpm_user_tasks_reassignment_timer (
    task_id             varchar(50) not null,
    reassignment_job_id varchar(255) not null,
    reassignment_type   varchar(255) not null,
    reassignment_value  bytea,
    java_type           varchar(255),
    primary key (task_id, reassignment_job_id)
);

CREATE SEQUENCE jbpm_user_tasks_deadline_seq INCREMENT BY 50 OWNED BY jbpm_user_tasks_deadline.id;
CREATE SEQUENCE jbpm_user_tasks_reassignment_seq INCREMENT BY 50 OWNED BY jbpm_user_tasks_reassignment.id;

alter table if exists jbpm_user_tasks_deadline
add constraint fk_jbpm_user_tasks_deadline_tid foreign key (task_id) references jbpm_user_tasks(id) on delete cascade;

alter table if exists jbpm_user_tasks_reassignment
add constraint fk_jbpm_user_tasks_reassignment_tid foreign key (task_id) references jbpm_user_tasks(id) on delete cascade;

alter table if exists jbpm_user_tasks_deadline_timer
add constraint fk_jbpm_user_tasks_deadline_timer_tid foreign key (task_id) references jbpm_user_tasks(id) on delete cascade;

alter table if exists jbpm_user_tasks_reassignment_timer
add constraint fk_jbpm_user_tasks_reassignment_timer_tid foreign key (task_id) references jbpm_user_tasks(id) on delete cascade;
