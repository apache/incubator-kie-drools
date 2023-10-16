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
package org.kie.kogito.index.mongodb.model;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.persistence.mongodb.model.ModelUtils;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;

class ProcessDefinitionEntityMapperTest {

    ProcessDefinitionEntityMapper mapper = new ProcessDefinitionEntityMapper();

    ProcessDefinition pd = new ProcessDefinition();

    ProcessDefinitionEntity entity = new ProcessDefinitionEntity();

    @BeforeEach
    void setup() {
        String version = "1.0";
        String processId = "testProcessId";
        Set<String> roles = singleton("testRoles");
        String type = "testType";
        Set<String> addons = singleton("testAddons");

        pd.setId(processId);
        pd.setVersion(version);
        pd.setRoles(roles);
        pd.setAddons(addons);
        pd.setType(type);

        entity.setId(processId);
        entity.setVersion(version);
        entity.setRoles(roles);
        entity.setAddons(addons);
        entity.setType(type);
    }

    @Test
    void testGetEntityClass() {
        assertEquals(ProcessDefinitionEntity.class, mapper.getEntityClass());
    }

    @Test
    void testMapToEntity() {
        ProcessDefinitionEntity result = mapper.mapToEntity(pd.getId(), pd);
        assertEquals(entity, result);
    }

    @Test
    void testMapToModel() {
        ProcessDefinition result = mapper.mapToModel(entity);
        assertEquals(pd, result);
    }

    @Test
    void testConvertToMongoAttribute() {
        assertEquals(MONGO_ID, mapper.convertToMongoAttribute(MONGO_ID));
        String testAttribute = "testAttribute";
        assertEquals(testAttribute, mapper.convertToMongoAttribute(testAttribute));
    }

    @Test
    void testConvertToModelAttribute() {
        assertEquals(ModelUtils.ID, mapper.convertToModelAttribute(ModelUtils.ID));
        String testAttribute = "test.attribute.go";
        assertEquals(testAttribute, mapper.convertToModelAttribute(testAttribute));
    }
}
