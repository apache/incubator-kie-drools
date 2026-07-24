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
package org.kie.kogito.index.mongodb.storage;

import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessDefinitionKey;
import org.kie.kogito.index.mongodb.model.ProcessDefinitionEntity;
import org.kie.kogito.index.mongodb.model.ProcessDefinitionEntityMapper;
import org.kie.kogito.index.storage.ModelProcessDefinitionStorage;
import org.kie.kogito.index.test.TestUtils;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.mongodb.client.MongoClientManager;
import org.kie.kogito.persistence.mongodb.storage.MongoStorage;
import org.kie.kogito.testcontainers.quarkus.MongoDBQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;

import static org.kie.kogito.index.storage.Constants.PROCESS_DEFINITIONS_STORAGE;

@QuarkusTest
@QuarkusTestResource(MongoDBQuarkusTestResource.class)
class ProcessDefinitionStorageIT extends StorageTestBase<ProcessDefinitionKey, ProcessDefinition> {

    @Inject
    MongoClientManager mongoClientManager;

    Storage<ProcessDefinitionKey, ProcessDefinition> storage;

    @BeforeEach
    void setUp() {
        this.storage = new ModelProcessDefinitionStorage(new MongoStorage<>(mongoClientManager.getCollection(PROCESS_DEFINITIONS_STORAGE, ProcessDefinitionEntity.class),
                ProcessDefinition.class.getName(), new ProcessDefinitionEntityMapper()));
    }

    @AfterEach
    void tearDown() {
        storage.clear();
    }

    @Test
    void testCache() {
        String processId = RandomStringUtils.randomAlphabetic(10);
        String version = "1.0";
        ProcessDefinition pdv1 = TestUtils.createProcessDefinition(processId, version, Set.of("admin", "kogito"));
        ProcessDefinition pdv2 = TestUtils.createProcessDefinition(processId, version, Set.of("kogito"));
        testStorage(storage, new ProcessDefinitionKey(processId, version), pdv1, pdv2);
    }
}
