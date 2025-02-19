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
package org.kie.kogito.index.postgresql.query;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.index.jpa.query.AbstractProcessInstanceEntityQueryIT;
import org.kie.kogito.index.jpa.storage.ProcessDefinitionEntityStorage;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessDefinitionKey;
import org.kie.kogito.index.storage.ProcessInstanceStorage;
import org.kie.kogito.index.test.TestUtils;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;

import static java.util.Collections.singletonList;
import static org.kie.kogito.index.json.JsonUtils.jsonFilter;
import static org.kie.kogito.index.test.QueryTestUtils.assertNotId;
import static org.kie.kogito.index.test.QueryTestUtils.assertWithId;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.*;

@QuarkusTest
@QuarkusTestResource(PostgreSqlQuarkusTestResource.class)
class ProcessInstanceEntityQueryIT extends AbstractProcessInstanceEntityQueryIT {

    @Inject
    ProcessDefinitionEntityStorage definitionStorage;

    @Test
    void testProcessInstanceVariables() {
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();
        ProcessInstanceStorage storage = getStorage();
        ProcessInstanceVariableDataEvent variableEvent = TestUtils.createProcessInstanceVariableEvent(processInstanceId, processId, "John", 28, false,
                List.of("Super", "Astonishing", "TheRealThing"));
        final String version = "1.0";
        ProcessDefinitionKey key = new ProcessDefinitionKey(processId, version);
        ProcessDefinition definitionEvent = TestUtils.createProcessDefinition(processId, version, Set.of());
        definitionEvent.setAnnotations(Set.of("Javierito", "Another"));
        definitionEvent.setMetadata(Map.of("name", "Javierito", "hobbies", List.of("community", "first")));
        variableEvent.setKogitoProcessInstanceVersion(version);
        definitionStorage.put(key, definitionEvent);
        storage.indexVariable(variableEvent);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(equalTo("variables.traveller.name", "John"))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(equalTo("variables.traveller.name", "Smith"))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(equalTo("variables.traveller.isMartian", false))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(equalTo("variables.traveller.isMartian", true))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(equalTo("variables.traveller.age", 28))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(equalTo("variables.traveller.age", 29))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(between("variables.traveller.age", 26, 30))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(between("variables.traveller.age", 1, 3))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(between("variables.traveller.age", 26, 30))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(between("variables.traveller.age", 1, 3))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(greaterThan("variables.traveller.age", 26))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(greaterThan("variables.traveller.age", 28))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(greaterThanEqual("variables.traveller.age", 28))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(greaterThanEqual("variables.traveller.age", 29))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(lessThan("variables.traveller.age", 29))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(lessThan("variables.traveller.age", 28))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(lessThanEqual("variables.traveller.age", 28))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(lessThanEqual("variables.traveller.age", 27))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(lessThanEqual("variables.traveller.age", 28))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(lessThanEqual("variables.traveller.age", 27))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(in("variables.traveller.name", List.of("John", "Smith")))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(in("variables.traveller.age", List.of("Jack", "Smith")))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(in("variables.traveller.age", List.of(28, 29)))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(in("variables.traveller.age", List.of(27, 29)))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(like("variables.traveller.name", "Joh*"))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(like("variables.traveller.name", "Joha*"))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(notNull("variables.traveller.aliases"))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(isNull("variables.traveller.aliases"))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(not(jsonFilter(isNull("variables.traveller.aliases")))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(and(List.of(jsonFilter(notNull("variables.traveller.aliases")), jsonFilter(lessThan("variables.traveller.age", 45))))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(or(List.of(jsonFilter(notNull("variables.traveller.aliases")), jsonFilter(lessThan("variables.traveller.age", 22))))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(contains("variables.traveller.aliases", "TheRealThing"))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(contains("variables.traveller.aliases", "TheDummyThing"))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(containsAny("variables.traveller.aliases", List.of("TheRealThing", "TheDummyThing")))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(containsAny("variables.traveller.aliases", List.of("TheRedPandaThing", "TheDummyThing")))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(containsAll("variables.traveller.aliases", List.of("Super", "Astonishing", "TheRealThing")))), null, null, null,
                processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(containsAll("variables.traveller.aliases", List.of("Super", "TheDummyThing")))), null, null, null,
                processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(equalTo("definition.metadata.name", "Javierito"))), null, null, null, processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(equalTo("definition.metadata.name", "Fulanito"))), null, null, null, processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(jsonFilter(contains("definition.metadata.hobbies", "community"))), null, null, null, processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(jsonFilter(contains("definition.metadata.hobbies", "commercial"))), null, null, null, processInstanceId);
        queryAndAssert(assertWithId(), storage, singletonList(contains("definition.annotations", "Javierito")), null, null, null, processInstanceId);
        queryAndAssert(assertNotId(), storage, singletonList(contains("definition.annotations", "Fulanito")), null, null, null, processInstanceId);
    }
}
