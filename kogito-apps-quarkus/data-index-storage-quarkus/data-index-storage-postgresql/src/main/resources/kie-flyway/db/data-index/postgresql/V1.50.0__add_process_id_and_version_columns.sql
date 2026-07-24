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

ALTER TABLE processes ADD COLUMN IF NOT EXISTS root_process_version VARCHAR(255);
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS process_version VARCHAR(255);
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS root_process_version VARCHAR(255);
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS process_version VARCHAR(255);
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS root_process_version VARCHAR(255);

CREATE INDEX IF NOT EXISTS idx_processes_process_id ON processes (process_id);
CREATE INDEX IF NOT EXISTS idx_processes_process_version ON processes (process_id, version);
CREATE INDEX IF NOT EXISTS idx_processes_root_version ON processes (root_process_id, root_process_version);

CREATE INDEX IF NOT EXISTS idx_jobs_process_id ON jobs (process_id);
CREATE INDEX IF NOT EXISTS idx_jobs_process_version ON jobs (process_id, process_version);
CREATE INDEX IF NOT EXISTS idx_jobs_root_version ON jobs (root_process_id, root_process_version);

CREATE INDEX IF NOT EXISTS idx_tasks_process_id ON tasks (process_id);
CREATE INDEX IF NOT EXISTS idx_tasks_process_version ON tasks (process_id, process_version);
CREATE INDEX IF NOT EXISTS idx_tasks_root_version ON tasks (root_process_id, root_process_version);
