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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.kogito.index.mongodb.model.ProcessIdEntityMapper.PROCESS_ID_ATTRIBUTE;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;

class ProcessIdEntityMapperTest {

    ProcessIdEntityMapper processIdEntityMapper = new ProcessIdEntityMapper();

    @Test
    void testGetEntityClass() {
        assertEquals(ProcessIdEntity.class, processIdEntityMapper.getEntityClass());
    }

    @Test
    void testMapToEntity() {
        String testId = "testProcessId";
        String testValue = "testProcessType";
        ProcessIdEntity result = processIdEntityMapper.mapToEntity(testId, testValue);

        ProcessIdEntity processIdEntity = new ProcessIdEntity();
        processIdEntity.setProcessId(testId);
        processIdEntity.setFullTypeName(testValue);

        assertEquals(processIdEntity, result);
    }

    @Test
    void testMapToModel() {
        String testId = "testProcessId";
        String testValue = "testProcessType";

        ProcessIdEntity processIdEntity = new ProcessIdEntity();
        processIdEntity.setProcessId(testId);
        processIdEntity.setFullTypeName(testValue);

        String result = processIdEntityMapper.mapToModel(processIdEntity);

        assertEquals(testValue, result);
    }

    @Test
    void testConvertToMongoAttribute() {
        assertEquals(MONGO_ID, processIdEntityMapper.convertToMongoAttribute(PROCESS_ID_ATTRIBUTE));

        String testAttribute = "testAttribute";
        assertEquals(testAttribute, processIdEntityMapper.convertToMongoAttribute(testAttribute));
    }

    @Test
    void testConvertToModelAttribute() {
        assertEquals(PROCESS_ID_ATTRIBUTE, processIdEntityMapper.convertToModelAttribute(MONGO_ID));

        String testAttribute = "test.attribute.name";
        assertEquals("name", processIdEntityMapper.convertToModelAttribute(testAttribute));
    }
}