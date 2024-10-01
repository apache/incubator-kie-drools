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

create table IF NOT EXISTS definitions_annotations
(
    value           varchar(255) not null,
    process_id      varchar(255) not null,
    process_version varchar(255) not null,
    primary key (value, process_id, process_version)
    );

create table IF NOT EXISTS definitions_metadata
(
    process_id      varchar(255) not null,
    process_version varchar(255) not null,
    value           varchar(255),
    key             varchar(255) not null,
    primary key (process_id, process_version, key)
    );

alter table if exists definitions_annotations
drop constraint if exists fk_definitions_annotations
cascade;

alter table if exists definitions_annotations
    add constraint fk_definitions_annotations
    foreign key (process_id, process_version)
    references definitions
    on
delete
cascade;

alter table if exists definitions_metadata
drop constraint if exists fk_definitions_metadata
cascade;

alter table if exists definitions_metadata
    add constraint fk_definitions_metadata
    foreign key (process_id, process_version)
    references definitions
    on
delete
cascade;

alter table if exists definitions
    add column IF NOT EXISTS description varchar (255);