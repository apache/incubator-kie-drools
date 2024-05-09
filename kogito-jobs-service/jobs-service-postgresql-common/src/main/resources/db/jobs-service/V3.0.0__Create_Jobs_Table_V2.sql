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

ALTER TABLE job_details
    RENAME TO job_details_v1;

DROP INDEX job_details_fire_time_idx;
DROP INDEX status_date;

CREATE TABLE job_details
(
  id VARCHAR(50) PRIMARY KEY,
  correlation_id VARCHAR(50),
  status VARCHAR(40),
  last_update TIMESTAMPTZ,
  retries INT4,
  execution_counter INT4,
  scheduled_id VARCHAR(40),
  priority INT4,
  recipient JSONB,
  trigger JSONB,
  fire_time TIMESTAMPTZ
);

CREATE INDEX job_details_fire_time_idx
    ON job_details (fire_time);