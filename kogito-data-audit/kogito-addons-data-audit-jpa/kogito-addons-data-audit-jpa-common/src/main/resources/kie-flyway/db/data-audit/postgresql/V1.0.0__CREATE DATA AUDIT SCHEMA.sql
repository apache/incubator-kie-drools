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

create sequence JOB_EXECUTION_HISTORY_ID_SEQ start with 1 increment by 50;
create sequence PROCESS_INSTANCE_ERROR_LOG_SEQ_ID start with 1 increment by 50;
create sequence PROCESS_INSTANCE_NODE_LOG_ID_SEQ start with 1 increment by 50;
create sequence PROCESS_INSTANCE_STATE_LOG_ID_SEQ start with 1 increment by 50;
create sequence PROCESS_INSTANCE_VARIABLE_LOG_ID_SEQ start with 1 increment by 50;
create sequence TASK_INSTANCE_ASSIGNMENT_LOG_ID_SEQ start with 1 increment by 50;
create sequence TASK_INSTANCE_ATTACHMENT_LOG_ID_SEQ start with 1 increment by 50;
create sequence TASK_INSTANCE_COMMENT_LOG_ID_SEQ start with 1 increment by 50;
create sequence TASK_INSTANCE_DEADLINE_LOG_ID_SEQ start with 1 increment by 50;
create sequence TASK_INSTANCE_STATE_LOG_ID_SEQ start with 1 increment by 50;
create sequence TASK_INSTANCE_VARIABLE_LOG_ID_SEQ start with 1 increment by 50;
create table Job_Execution_Log (id bigint not null, event_date timestamp(6), execution_counter integer, expiration_time timestamp(6), job_id varchar(255), node_instance_id varchar(255), priority integer, process_instance_id varchar(255), repeat_interval bigint, repeat_limit integer, retries integer, scheduled_id varchar(255), status varchar(255), primary key (id));
create table Process_Instance_Error_Log (id bigint not null, business_key varchar(255), event_date timestamp(6), event_id varchar(255), parent_process_instance_id varchar(255), process_id varchar(255), process_instance_id varchar(255), process_type varchar(255), process_version varchar(255), root_process_id varchar(255), root_process_instance_id varchar(255), error_message varchar(255), node_definition_id varchar(255), node_instance_id varchar(255), primary key (id));
create table Process_Instance_Node_Log (id bigint not null, business_key varchar(255), event_date timestamp(6), event_id varchar(255), parent_process_instance_id varchar(255), process_id varchar(255), process_instance_id varchar(255), process_type varchar(255), process_version varchar(255), root_process_id varchar(255), root_process_instance_id varchar(255), connection varchar(255), event_data varchar(255), event_type varchar(255) check (event_type in ('ENTER','EXIT','ABORTED','ASYNC_ENTER','OBSOLETE','SKIPPED','ERROR','SLA_VIOLATION')), node_definition_id varchar(255), node_instance_id varchar(255), node_name varchar(255), node_type varchar(255), sla_due_date timestamp(6), work_item_id varchar(255), primary key (id));
create table Process_Instance_State_Log (id bigint not null, business_key varchar(255), event_date timestamp(6), event_id varchar(255), parent_process_instance_id varchar(255), process_id varchar(255), process_instance_id varchar(255), process_type varchar(255), process_version varchar(255), root_process_id varchar(255), root_process_instance_id varchar(255), event_type varchar(255) not null check (event_type in ('ACTIVE','STARTED','COMPLETED','ABORTED','SLA_VIOLATION','PENDING','SUSPENDING','ERROR')), outcome varchar(255), sla_due_date timestamp(6), state varchar(255), primary key (id));
create table Process_Instance_State_Roles_Log (process_instance_state_log_id bigint not null, role varchar(255));
create table Process_Instance_Variable_Log (id bigint not null, business_key varchar(255), event_date timestamp(6), event_id varchar(255), parent_process_instance_id varchar(255), process_id varchar(255), process_instance_id varchar(255), process_type varchar(255), process_version varchar(255), root_process_id varchar(255), root_process_instance_id varchar(255), variable_id varchar(255), variable_name varchar(255), variable_value varchar(255), primary key (id));
create table Task_Instance_Assignment_Log (id bigint not null, business_key varchar(255), event_date timestamp(6), event_id varchar(255), event_user varchar(255), process_instance_id varchar(255), user_task_definition_id varchar(255), user_task_instance_id varchar(255), assignment_type varchar(255), task_name varchar(255), primary key (id));
create table Task_Instance_Assignment_Users_Log (task_instance_assignment_log_id bigint not null, user_id varchar(255));
create table Task_Instance_Attachment_Log (id bigint not null, business_key varchar(255), event_date timestamp(6), event_id varchar(255), event_user varchar(255), process_instance_id varchar(255), user_task_definition_id varchar(255), user_task_instance_id varchar(255), attachment_id varchar(255), attachment_name varchar(255), attachment_uri varchar(255), event_type integer, primary key (id));
create table Task_Instance_Comment_Log (id bigint not null, business_key varchar(255), event_date timestamp(6), event_id varchar(255), event_user varchar(255), process_instance_id varchar(255), user_task_definition_id varchar(255), user_task_instance_id varchar(255), comment_content varchar(255), comment_id varchar(255), event_type integer, primary key (id));
create table Task_Instance_Deadline_Log (id bigint not null, business_key varchar(255), event_date timestamp(6), event_id varchar(255), event_user varchar(255), process_instance_id varchar(255), user_task_definition_id varchar(255), user_task_instance_id varchar(255), event_type varchar(255), primary key (id));
create table Task_Instance_State_Log (id bigint not null, business_key varchar(255), event_date timestamp(6), event_id varchar(255), event_user varchar(255), process_instance_id varchar(255), user_task_definition_id varchar(255), user_task_instance_id varchar(255), actual_user varchar(255), description varchar(255), event_type varchar(255), name varchar(255), state varchar(255), primary key (id));
create table Task_Instance_Variable_Log (id bigint not null, business_key varchar(255), event_date timestamp(6), event_id varchar(255), event_user varchar(255), process_instance_id varchar(255), user_task_definition_id varchar(255), user_task_instance_id varchar(255), variable_id varchar(255), variable_name varchar(255), variable_type varchar(255) check (variable_type in ('INPUT','OUTPUT')), variable_value varchar(255), primary key (id));
create table Task_Instance_Deadline_Notification_Log (task_instance_deadline_log_id bigint not null, property_value varchar(255), property_name varchar(255) not null, primary key (task_instance_deadline_log_id, property_name));
create index ix_jel_pid on Job_Execution_Log (process_instance_id);
create index ix_jel_jid on Job_Execution_Log (job_id);
create index ix_jel_status on Job_Execution_Log (status);
create index ix_piel_pid on Process_Instance_Error_Log (process_instance_id);
create index ix_piel_key on Process_Instance_Error_Log (business_key);
create index ix_piel_event_date on Process_Instance_Error_Log (event_date);
create index ix_pinl_pid on Process_Instance_Node_Log (process_instance_id);
create index ix_pinl_key on Process_Instance_Node_Log (business_key);
create index ix_pinl_event_date on Process_Instance_Node_Log (event_date);
create index ix_pisl_pid on Process_Instance_State_Log (process_instance_id);
create index ix_pisl_state on Process_Instance_State_Log (state);
create index ix_pisl_key on Process_Instance_State_Log (business_key);
create index ix_pisl_event_date on Process_Instance_State_Log (event_date);
create index ix_pivl_pid on Process_Instance_Variable_Log (process_instance_id);
create index ix_pivl_key on Process_Instance_Variable_Log (business_key);
create index ix_pivl_event_date on Process_Instance_Variable_Log (event_date);
create index ix_pivl_var_id on Process_Instance_Variable_Log (variable_id);
create index ix_utasl_utid on Task_Instance_Assignment_Log (user_task_instance_id);
create index ix_utasl_pid on Task_Instance_Assignment_Log (process_instance_id);
create index ix_utasl_key on Task_Instance_Assignment_Log (business_key);
create index ix_utasl_event_date on Task_Instance_Assignment_Log (event_date);
create index ix_utatl_utid on Task_Instance_Attachment_Log (user_task_instance_id);
create index ix_utatl_pid on Task_Instance_Attachment_Log (process_instance_id);
create index ix_utatl_key on Task_Instance_Attachment_Log (business_key);
create index ix_utatl_event_date on Task_Instance_Attachment_Log (event_date);
create index ix_utcl_utid on Task_Instance_Comment_Log (user_task_instance_id);
create index ix_utcl_pid on Task_Instance_Comment_Log (process_instance_id);
create index ix_utcl_key on Task_Instance_Comment_Log (business_key);
create index ix_utcl_event_date on Task_Instance_Comment_Log (event_date);
create index ix_utdl_utid on Task_Instance_Deadline_Log (user_task_instance_id);
create index ix_utdl_pid on Task_Instance_Deadline_Log (process_instance_id);
create index ix_utdl_key on Task_Instance_Deadline_Log (business_key);
create index ix_utdl_event_date on Task_Instance_Deadline_Log (event_date);
create index ix_utsl_utid on Task_Instance_State_Log (user_task_instance_id);
create index ix_utsl_state on Task_Instance_State_Log (state);
create index ix_utsl_pid on Task_Instance_State_Log (process_instance_id);
create index ix_utsl_key on Task_Instance_State_Log (business_key);
create index ix_utsl_event_date on Task_Instance_State_Log (event_date);
create index ix_tavl_utid on Task_Instance_Variable_Log (user_task_instance_id);
create index ix_tavl_pid on Task_Instance_Variable_Log (process_instance_id);
create index ix_tavl_key on Task_Instance_Variable_Log (business_key);
create index ix_tavl_event_date on Task_Instance_Variable_Log (event_date);
alter table if exists Process_Instance_State_Roles_Log add constraint fk_process_instance_state_pid foreign key (process_instance_state_log_id) references Process_Instance_State_Log;
alter table if exists Task_Instance_Assignment_Users_Log add constraint fk_task_instance_assignment_log_tid foreign key (task_instance_assignment_log_id) references Task_Instance_Assignment_Log;
alter table if exists Task_Instance_Deadline_Notification_Log add constraint fk_task_instance_deadline_tid foreign key (task_instance_deadline_log_id) references Task_Instance_Deadline_Log;
