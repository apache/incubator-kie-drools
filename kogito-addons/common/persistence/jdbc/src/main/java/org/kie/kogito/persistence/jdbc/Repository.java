/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.persistence.jdbc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

abstract class Repository {

    static final String INSERT = "INSERT INTO process_instances (id, payload, process_id, version) VALUES (?, ?, ?, ?)";
    static final String FIND_ALL = "SELECT payload FROM process_instances WHERE process_id = ?";
    static final String FIND_BY_ID = "SELECT payload, version FROM process_instances WHERE process_id = ? and id = ?";
    static final String UPDATE = "UPDATE process_instances SET payload = ? WHERE process_id = ? and id = ?";
    static final String UPDATE_WITH_LOCK = "UPDATE process_instances SET payload = ?, version = ? WHERE process_id = ? and id = ? and version = ?";
    static final String DELETE = "DELETE FROM process_instances WHERE process_id = ? and id = ?";
    static final String COUNT = "SELECT COUNT(id) as count FROM process_instances WHERE process_id = ?";

    abstract boolean tableExists();

    abstract void createTable();

    abstract void insertInternal(String processId, UUID id, byte[] payload);

    abstract void updateInternal(String processId, UUID id, byte[] payload);

    abstract boolean updateWithLock(String processId, UUID id, byte[] payload, long version);

    abstract boolean deleteInternal(String processId, UUID id);

    abstract Map<String, Object> findByIdInternal(String processId, UUID id);

    abstract List<byte[]> findAllInternal(String processId);

    abstract Long countInternal(String processId);

    protected RuntimeException uncheckedException(Exception ex, String message, Object... param) {
        return new RuntimeException(String.format(message, param), ex);
    }
}
