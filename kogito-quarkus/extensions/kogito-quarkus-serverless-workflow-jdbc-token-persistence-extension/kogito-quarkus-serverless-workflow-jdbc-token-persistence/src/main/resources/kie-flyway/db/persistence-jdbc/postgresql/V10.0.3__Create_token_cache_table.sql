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

-- Create OAuth2 token cache table for database-backed token caching
-- This table stores OAuth2 tokens with their expiration times for persistence across restarts

CREATE TABLE kogito_oauth2_token_cache (
    process_instance_id VARCHAR(36) NOT NULL,
    auth_name VARCHAR(255) NOT NULL,
    access_token TEXT NOT NULL,
    refresh_token TEXT,
    expiration_time BIGINT NOT NULL,
    PRIMARY KEY (process_instance_id, auth_name),
    CONSTRAINT fk_token_cache_process_instance 
        FOREIGN KEY (process_instance_id) 
        REFERENCES process_instances(id) 
        ON DELETE CASCADE
);

-- Create indexes for performance optimization
CREATE INDEX idx_token_cache_expiration ON kogito_oauth2_token_cache(expiration_time);
CREATE INDEX idx_token_cache_auth_name ON kogito_oauth2_token_cache(auth_name); 