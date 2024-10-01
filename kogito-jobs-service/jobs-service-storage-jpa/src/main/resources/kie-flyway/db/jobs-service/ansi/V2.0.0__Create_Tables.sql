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

create table job_details
(
    id                     varchar(50) primary key,
    correlation_id         varchar(50),
    status                 varchar(40),
    last_update            timestamp,
    retries                integer,
    execution_counter      integer,
    scheduled_id           varchar(40),
    priority               integer,
    recipient              varbinary(max),
    trigger                varbinary(max),
    fire_time              timestamp,
    execution_timeout      bigint,
    execution_timeout_unit varchar(40),
    created                timestamp
);

create index job_details_fire_time_idx on job_details (fire_time);
create index job_details_created_idx on job_details (created);

CREATE TABLE job_service_management
(
    id             varchar(40) primary key,
    last_heartbeat timestamp,
    token          varchar(40) unique
);