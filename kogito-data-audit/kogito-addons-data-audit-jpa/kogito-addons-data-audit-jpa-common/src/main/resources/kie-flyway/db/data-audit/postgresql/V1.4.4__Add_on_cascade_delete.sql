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

alter table process_instance_state_roles_log drop constraint fk_process_instance_state_pid;
alter table process_instance_state_roles_log add constraint fk_process_instance_state_pid foreign key (process_instance_state_log_id) references process_instance_state_log on delete cascade;

alter table task_instance_assignment_users_log drop constraint fk_task_instance_assignment_log_tid;
alter table task_instance_assignment_users_log add constraint fk_task_instance_assignment_log_tid foreign key (task_instance_assignment_log_id) references task_instance_assignment_log on delete cascade;

alter table task_instance_deadline_notification_log drop constraint fk_task_instance_deadline_tid;
alter table task_instance_deadline_notification_log add constraint fk_task_instance_deadline_tid foreign key (task_instance_deadline_log_id) references task_instance_deadline_log on delete cascade;
