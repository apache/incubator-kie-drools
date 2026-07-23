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

ALTER TABLE tasks ALTER COLUMN external_reference_id SET DATA TYPE VARCHAR(4000);

ALTER TABLE comments ALTER COLUMN content SET DATA TYPE VARCHAR(1000);

ALTER TABLE nodes ALTER COLUMN error_message SET DATA TYPE VARCHAR(65535);

ALTER TABLE processes ALTER COLUMN message SET DATA TYPE VARCHAR(65535);

ALTER TABLE processes ALTER COLUMN cloud_event_id SET DATA TYPE VARCHAR(1000);

ALTER TABLE processes ALTER COLUMN cloud_event_source SET DATA TYPE VARCHAR(1000);