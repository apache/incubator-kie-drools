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
package org.kie.kogito.persistence.postgresql.reporting.api;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresJsonField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinition;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinitions;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
import org.kie.kogito.persistence.reporting.model.MappingDefinition;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostgresMappingsApiV1IT {

    @Test
    @Order(1)
    void getAllMappingDefinitions() {
        PostgresMappingDefinitions response = given().contentType(ContentType.TEXT).when().get("/mappings").prettyPeek().as(PostgresMappingDefinitions.class);
        assertNotNull(response);
        assertMappingDefinitions(List.of("BasicTypeMappingId",
                "HierarchicalTypeMappingId",
                "ComplexHierarchicalTypeMappingId"));
    }

    @Test
    @Order(2)
    void getMappingDefinition() {
        PostgresMappingDefinition response = given().contentType(ContentType.TEXT).when().get("/mappings/BasicTypeMappingId").prettyPeek().as(PostgresMappingDefinition.class);
        assertNotNull(response);
        assertEquals("BasicTypeMappingId", response.getMappingId());
        assertEquals(2, response.getFieldMappings().size());
    }

    @Test
    @Order(3)
    void dynamicMappingDefinitionCreation() {
        final PostgresMappingDefinition definition = new PostgresMappingDefinition("ExampleMappingId",
                "kogito_data_cache",
                "json_value",
                List.of(new PostgresField("var_name")),
                List.of(new PostgresPartitionField("cache_name", "Example")),
                "Example",
                List.of(new PostgresMapping("field1",
                        new PostgresJsonField("field1", JsonType.STRING))));

        Response response = given().contentType(ContentType.JSON).body(definition).when().post("/mappings").prettyPeek();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertMappingDefinitions(List.of("BasicTypeMappingId",
                "HierarchicalTypeMappingId",
                "ComplexHierarchicalTypeMappingId",
                "ExampleMappingId"));

        PostgresMappingDefinition newMappingDefinition = given().contentType(ContentType.TEXT).when().get("/mappings/ExampleMappingId").prettyPeek().as(PostgresMappingDefinition.class);
        assertNotNull(newMappingDefinition);
        assertEquals("ExampleMappingId", newMappingDefinition.getMappingId());
        assertEquals(1, newMappingDefinition.getFieldMappings().size());
    }

    @Test
    @Order(4)
    void dynamicMappingDefinitionDestruction() {
        assertMappingDefinitions(List.of("BasicTypeMappingId",
                "HierarchicalTypeMappingId",
                "ComplexHierarchicalTypeMappingId",
                "ExampleMappingId"));

        Response response = given().when().delete("/mappings/ExampleMappingId").prettyPeek();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertMappingDefinitions(List.of("BasicTypeMappingId",
                "HierarchicalTypeMappingId",
                "ComplexHierarchicalTypeMappingId"));
    }

    private void assertMappingDefinitions(final List<String> expectedMappingIds) {
        PostgresMappingDefinitions response = given().contentType(ContentType.TEXT).when().get("/mappings").prettyPeek().as(PostgresMappingDefinitions.class);
        assertNotNull(response);
        assertEquals(expectedMappingIds.size(), response.getMappingDefinitions().size());
        assertTrue(response.getMappingDefinitions().stream().map(MappingDefinition::getMappingId).collect(Collectors.toList()).containsAll(expectedMappingIds));
    }

}
