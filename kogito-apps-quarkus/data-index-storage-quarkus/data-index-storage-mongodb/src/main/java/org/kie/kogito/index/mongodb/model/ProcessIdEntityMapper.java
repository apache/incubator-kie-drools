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

import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;

import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;

public class ProcessIdEntityMapper implements MongoEntityMapper<String, ProcessIdEntity> {

    static final String PROCESS_ID_ATTRIBUTE = "processId";

    @Override
    public Class<ProcessIdEntity> getEntityClass() {
        return ProcessIdEntity.class;
    }

    @Override
    public ProcessIdEntity mapToEntity(String key, String value) {
        ProcessIdEntity processIdEntity = new ProcessIdEntity();
        processIdEntity.setProcessId(key);
        processIdEntity.setFullTypeName(value);
        return processIdEntity;
    }

    @Override
    public String mapToModel(ProcessIdEntity entity) {
        return entity.getFullTypeName();
    }

    @Override
    public String convertToMongoAttribute(String attribute) {
        return PROCESS_ID_ATTRIBUTE.equals(attribute) ? MONGO_ID : MongoEntityMapper.super.convertToMongoAttribute(attribute);
    }

    @Override
    public String convertToModelAttribute(String attribute) {
        return MONGO_ID.equals(attribute) ? PROCESS_ID_ATTRIBUTE : MongoEntityMapper.super.convertToModelAttribute(attribute);
    }
}
