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

package org.kie.kogito.index.jpa.springboot.storage;

import java.util.List;

import org.kie.kogito.index.api.DateTimeCoercing;
import org.kie.kogito.index.jpa.mapper.ProcessDefinitionEntityMapper;
import org.kie.kogito.index.jpa.mapper.ProcessInstanceEntityMapper;
import org.kie.kogito.index.jpa.storage.*;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManager;

@Configuration
public class DataIndexStorageProducer {

    @Bean
    public JobEntityStorage jobEntityStorage(EntityManager entityManager) {
        return new JobEntityStorage(entityManager);
    }

    @Bean
    public ProcessDefinitionEntityStorage processDefinitionEntityStorage(EntityManager entityManager, List<JsonPredicateBuilder> jsonPredicateBuilders) {
        return new ProcessDefinitionEntityStorage(entityManager, jsonPredicateBuilders, ProcessDefinitionEntityMapper.INSTANCE);
    }

    @Bean
    public ProcessInstanceEntityStorage processInstanceEntityStorage(EntityManager entityManager, List<JsonPredicateBuilder> jsonPredicateBuilders) {
        return new ProcessInstanceEntityStorage(entityManager, jsonPredicateBuilders, ProcessInstanceEntityMapper.INSTANCE);
    }

    @Bean
    public UserTaskInstanceEntityStorage userTaskInstanceEntityStorage(EntityManager entityManager) {
        return new UserTaskInstanceEntityStorage(entityManager);
    }

    @Bean
    public DataIndexStorageService jpaDataIndexStorageService(ProcessDefinitionEntityStorage processDefinitionEntityStorage, JobEntityStorage jobEntityStorage,
            ProcessInstanceEntityStorage processInstanceEntityStorage, UserTaskInstanceEntityStorage userTaskInstanceEntityStorage) {
        return new JPADataIndexStorageService(processDefinitionEntityStorage, jobEntityStorage, processInstanceEntityStorage, userTaskInstanceEntityStorage);
    }

    @Bean
    public DateTimeCoercing dateTimeCoercing() {
        return new JPADateTimeCoercing();
    }
}
