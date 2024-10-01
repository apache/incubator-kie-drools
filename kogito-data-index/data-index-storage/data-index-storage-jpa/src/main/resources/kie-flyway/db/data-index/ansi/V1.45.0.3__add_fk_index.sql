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

create index idx_attachments_tid on attachments(task_id);
create index idx_comments_tid on comments(task_id);
create index idx_definitions_addons_pid_pv on definitions_addons(process_id, process_version);
create index idx_definitions_annotations_pid_pv on definitions_annotations(process_id, process_version);
create index idx_definitions_metadata_pid_pv on definitions_metadata(process_id, process_version);
create index idx_definitions_nodes_pid_pv on definitions_nodes(process_id, process_version);
create index idx_definitions_nodes_metadata_pid_pv on definitions_nodes_metadata(process_id, process_version);
create index idx_definitions_roles_pid_pv on definitions_roles(process_id, process_version);
create index idx_milestones_piid on milestones(process_instance_id);
create index idx_nodes_piid on nodes(process_instance_id);
create index idx_processes_addons_pid on processes_addons(process_id);
create index idx_processes_roles_pid on processes_roles(process_id);
create index idx_tasks_admin_groups_tid on tasks_admin_groups(task_id);
create index idx_tasks_admin_users_tid on tasks_admin_users(task_id);
create index idx_tasks_excluded_users_tid on tasks_excluded_users(task_id);
create index idx_tasks_potential_groups_tid on tasks_potential_groups(task_id);
create index idx_tasks_potential_users_tid on tasks_potential_users(task_id);
