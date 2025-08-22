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

INSERT INTO job_details (id, correlation_id, status, last_update, fire_time, retries, execution_counter, scheduled_id, priority, trigger, recipient)
    SELECT job.id AS id,
           job.correlation_id AS correlation_id,
           job.status AS status,
           job.last_update AS last_update,
           job.fire_time AS fire_time,
           job.retries AS retries,
           job.execution_counter AS execution_counter,
           job.scheduled_id AS scheduled_id,
           job.priority AS priority,
           job.trigger AS trigger,
           json_build_object('url', job.recipient ->> 'endpoint',
                             'type', 'http',
                             'method', 'POST',
                             'classType', 'org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient',
                             'queryParams', '{}'::jsonb,
                             'headers','{}'::jsonb,
                             'payload', null
               ) AS recipient
    FROM job_details_v1 job WHERE job.id IS NOT NULL;