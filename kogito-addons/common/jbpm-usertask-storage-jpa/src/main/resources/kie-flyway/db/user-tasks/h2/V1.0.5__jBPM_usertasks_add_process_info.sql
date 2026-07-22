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

ALTER TABLE jbpm_user_tasks ADD COLUMN process_instance_id VARCHAR(50);
ALTER TABLE jbpm_user_tasks ADD COLUMN process_id VARCHAR(255);
ALTER TABLE jbpm_user_tasks ADD COLUMN process_version VARCHAR(255);
ALTER TABLE jbpm_user_tasks ADD COLUMN parent_process_instance_id VARCHAR(50);
ALTER TABLE jbpm_user_tasks ADD COLUMN root_process_instance_id VARCHAR(50);
ALTER TABLE jbpm_user_tasks ADD COLUMN root_process_id VARCHAR(255);

UPDATE jbpm_user_tasks t
SET t.process_instance_id = (
    SELECT TRIM(BOTH '"' FROM m.metadata_value)
    FROM jbpm_user_tasks_metadata m
    WHERE m.task_id = t.id AND m.metadata_name = 'ProcessInstanceId'
)
WHERE EXISTS (
    SELECT 1
    FROM jbpm_user_tasks_metadata m
    WHERE m.task_id = t.id AND m.metadata_name = 'ProcessInstanceId'
      AND m.metadata_value IS NOT NULL
);

UPDATE jbpm_user_tasks t
SET t.process_id = (
    SELECT TRIM(BOTH '"' FROM m.metadata_value)
    FROM jbpm_user_tasks_metadata m
    WHERE m.task_id = t.id AND m.metadata_name = 'ProcessId'
)
WHERE EXISTS (
    SELECT 1
    FROM jbpm_user_tasks_metadata m
    WHERE m.task_id = t.id AND m.metadata_name = 'ProcessId'
      AND m.metadata_value IS NOT NULL
);

UPDATE jbpm_user_tasks t
SET t.process_version = (
    SELECT TRIM(BOTH '"' FROM m.metadata_value)
    FROM jbpm_user_tasks_metadata m
    WHERE m.task_id = t.id AND m.metadata_name = 'ProcessVersion'
)
WHERE EXISTS (
    SELECT 1
    FROM jbpm_user_tasks_metadata m
    WHERE m.task_id = t.id AND m.metadata_name = 'ProcessVersion'
      AND m.metadata_value IS NOT NULL
);

UPDATE jbpm_user_tasks t
SET t.parent_process_instance_id = (
    SELECT TRIM(BOTH '"' FROM m.metadata_value)
    FROM jbpm_user_tasks_metadata m
    WHERE m.task_id = t.id AND m.metadata_name = 'ParentProcessInstanceId'
)
WHERE EXISTS (
    SELECT 1
    FROM jbpm_user_tasks_metadata m
    WHERE m.task_id = t.id AND m.metadata_name = 'ParentProcessInstanceId'
      AND m.metadata_value IS NOT NULL
);

UPDATE jbpm_user_tasks t
SET t.root_process_instance_id = (
    SELECT TRIM(BOTH '"' FROM m.metadata_value)
    FROM jbpm_user_tasks_metadata m
    WHERE m.task_id = t.id AND m.metadata_name = 'RootProcessInstanceId'
)
WHERE EXISTS (
    SELECT 1
    FROM jbpm_user_tasks_metadata m
    WHERE m.task_id = t.id AND m.metadata_name = 'RootProcessInstanceId'
      AND m.metadata_value IS NOT NULL
);

UPDATE jbpm_user_tasks t
SET t.root_process_id = (
    SELECT TRIM(BOTH '"' FROM m.metadata_value)
    FROM jbpm_user_tasks_metadata m
    WHERE m.task_id = t.id AND m.metadata_name = 'RootProcessId'
)
WHERE EXISTS (
    SELECT 1
    FROM jbpm_user_tasks_metadata m
    WHERE m.task_id = t.id AND m.metadata_name = 'RootProcessId'
      AND m.metadata_value IS NOT NULL
);

