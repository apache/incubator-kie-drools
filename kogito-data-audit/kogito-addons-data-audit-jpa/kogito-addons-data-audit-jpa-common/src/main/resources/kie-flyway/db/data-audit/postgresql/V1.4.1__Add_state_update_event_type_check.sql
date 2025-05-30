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

alter table if exists Process_Instance_Node_Log drop constraint Process_Instance_Node_Log_event_type_check;
alter table if exists Process_Instance_State_Log drop constraint Process_Instance_State_Log_event_type_check;

alter table if exists Process_Instance_Node_Log add constraint Process_Instance_Node_Log_event_type_check check (event_type in ('ENTER','EXIT','ABORTED','ASYNC_ENTER','OBSOLETE','SKIPPED','ERROR','SLA_VIOLATION','STATE_UPDATED'));
alter table if exists Process_Instance_State_Log add constraint Process_Instance_State_Log_event_type_check check (event_type in ('ACTIVE','STARTED','COMPLETED','ABORTED','SLA_VIOLATION','PENDING','SUSPENDING','ERROR','STATE_UPDATED'));