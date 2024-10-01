--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements.  See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership.  The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License.  You may obtain a copy of the License at
--
--   http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

-- To be used with kie-addons-quarkus-persistence-jdbc for Quarkus or kie-addons-springboot-persistence-jdbc for SpringBoot
CREATE TABLE process_instances
(
    id              character(36)     NOT NULL,
    payload         bytea             NOT NULL,
    process_id      character varying NOT NULL,
    version         bigint,
    process_version character varying,
    CONSTRAINT process_instances_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_process_instances_process_id ON process_instances (process_id, id, process_version);

CREATE TABLE correlation_instances
(
    id                     character(36)         NOT NULL,
    encoded_correlation_id character varying(36) NOT NULL UNIQUE,
    correlated_id          character varying(36) NOT NULL,
    correlation            json                  NOT NULL,
    version                bigint,
    CONSTRAINT correlation_instances_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_correlation_instances_encoded_id ON correlation_instances (encoded_correlation_id);
CREATE INDEX idx_correlation_instances_correlated_id ON correlation_instances (correlated_id);
