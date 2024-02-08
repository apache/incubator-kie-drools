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
package org.kie.kogito.index.jdbc;

import org.kie.kogito.index.jpa.storage.JPAStorageService;
import org.kie.kogito.index.jpa.storage.JobEntityStorage;
import org.kie.kogito.index.jpa.storage.ProcessDefinitionEntityStorage;
import org.kie.kogito.index.jpa.storage.ProcessInstanceEntityStorage;
import org.kie.kogito.index.jpa.storage.UserTaskInstanceEntityStorage;
import org.kie.kogito.persistence.api.StorageService;

import io.quarkus.arc.properties.IfBuildProperty;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;

import static org.kie.kogito.persistence.api.factory.Constants.PERSISTENCE_TYPE_PROPERTY;

public class JdbcStorageServiceProducer {
    @Produces
    @Alternative
    @Priority(1)
    @ApplicationScoped
    @IfBuildProperty(name = PERSISTENCE_TYPE_PROPERTY, stringValue = "jdbc")
    StorageService PostgreSqlStorageService(final ProcessDefinitionEntityStorage definitionStorage,
            final ProcessInstanceEntityStorage processStorage,
            final JobEntityStorage jobStorage,
            final UserTaskInstanceEntityStorage taskStorage) {
        return new JPAStorageService(definitionStorage, processStorage, jobStorage, taskStorage);
    }
}
