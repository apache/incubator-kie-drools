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
import org.kie.kogito.index.jpa.storage.ProcessDefinitionEntityStorage;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessDefinitionKey;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.storage.ProcessInstanceStorage;
import org.kie.kogito.index.test.TestUtils;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import com.fasterxml.jackson.databind.JsonNode;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(PostgreSqlQuarkusTestResource.class)
class VariablesAndMetadataJsonIT {

    @Inject
    ProcessInstanceStorage storage;

    @Inject
    ProcessDefinitionEntityStorage definitionStorage;

    @Test
    void shouldStoreAndRetrieveVariablesAndMetadataWithPostgresJson() {
        String processId = "json.process";
        String processInstanceId = UUID.randomUUID().toString();

        final String version = "1.0";
        ProcessDefinitionKey key = new ProcessDefinitionKey(processId, version);
        ProcessDefinition definitionEvent = TestUtils.createProcessDefinition(processId, version, Set.of());
        definitionEvent.setAnnotations(Set.of("TestAnnotation"));
        definitionEvent.setMetadata(Map.of(
                "name", "TestProcess",
                "type", "demo",
                "owner", "pepe"));
        definitionStorage.put(key, definitionEvent);

        ProcessInstanceVariableDataEvent variableEvent =
                TestUtils.createProcessInstanceVariableEvent(
                        processInstanceId,
                        processId,
                        "John",
                        28,
                        false,
                        List.of("A", "B"));
        variableEvent.setKogitoProcessInstanceVersion(version);

        storage.indexVariable(variableEvent);

        ProcessInstance result = storage.get(processInstanceId);
        assertThat(result).isNotNull();

        JsonNode variablesNode = result.getVariables();
        assertThat(variablesNode).isNotNull();
        assertThat(variablesNode.has("traveller")).isTrue();

        JsonNode travellerNode = variablesNode.get("traveller");
        assertThat(travellerNode.get("name").asText()).isEqualTo("John");
        assertThat(travellerNode.get("age").asInt()).isEqualTo(28);
        assertThat(travellerNode.get("isMartian").asBoolean()).isFalse();

        JsonNode aliasesNode = travellerNode.get("aliases");
        assertThat(aliasesNode.isArray()).isTrue();
        assertThat(aliasesNode).hasSize(2);
        assertThat(aliasesNode.get(0).asText()).isEqualTo("A");
        assertThat(aliasesNode.get(1).asText()).isEqualTo("B");

        ProcessDefinition storedDefinition = definitionStorage.get(key);
        assertThat(storedDefinition).isNotNull();

        Map<String, Object> metadata = storedDefinition.getMetadata();
        assertThat(metadata).isNotNull();
        assertThat(metadata).containsEntry("name", "TestProcess");
        assertThat(metadata).containsEntry("type", "demo");
        assertThat(metadata).containsEntry("owner", "pepe");

        Set<String> annotations = storedDefinition.getAnnotations();
        assertThat(annotations).contains("TestAnnotation");
    }
}
