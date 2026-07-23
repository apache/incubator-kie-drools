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

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.mongodb.model.DomainEntityMapper;
import org.kie.kogito.index.mongodb.model.JobEntityMapper;
import org.kie.kogito.index.mongodb.model.ProcessDefinitionEntityMapper;
import org.kie.kogito.index.mongodb.model.ProcessIdEntityMapper;
import org.kie.kogito.index.mongodb.model.ProcessInstanceEntityMapper;
import org.kie.kogito.index.mongodb.model.UserTaskInstanceEntityMapper;
import org.kie.kogito.persistence.mongodb.index.IndexCreateOrUpdateEvent;
import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;
import org.kie.kogito.persistence.mongodb.storage.MongoModelService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import static org.kie.kogito.index.mongodb.Constants.isDomainCollection;
import static org.kie.kogito.index.storage.Constants.JOBS_STORAGE;
import static org.kie.kogito.index.storage.Constants.PROCESS_DEFINITIONS_STORAGE;
import static org.kie.kogito.index.storage.Constants.PROCESS_ID_MODEL_STORAGE;
import static org.kie.kogito.index.storage.Constants.PROCESS_INSTANCES_STORAGE;
import static org.kie.kogito.index.storage.Constants.USER_TASK_INSTANCES_STORAGE;

@ApplicationScoped
public class MongoModelServiceImpl implements MongoModelService {

    @Inject
    Event<IndexCreateOrUpdateEvent> indexCreateOrUpdateEvent;

    static final Map<String, Supplier<MongoEntityMapper<?, ?>>> ENTITY_MAPPER_MAP = Map.of(
            JOBS_STORAGE, JobEntityMapper::new,
            PROCESS_DEFINITIONS_STORAGE, ProcessDefinitionEntityMapper::new,
            PROCESS_INSTANCES_STORAGE, ProcessInstanceEntityMapper::new,
            USER_TASK_INSTANCES_STORAGE, UserTaskInstanceEntityMapper::new,
            PROCESS_ID_MODEL_STORAGE, ProcessIdEntityMapper::new);

    @PostConstruct
    void init() {
        indexCreateOrUpdateEvent.fire(new IndexCreateOrUpdateEvent(PROCESS_DEFINITIONS_STORAGE, ProcessDefinition.class.getName()));
        indexCreateOrUpdateEvent.fire(new IndexCreateOrUpdateEvent(PROCESS_INSTANCES_STORAGE, ProcessInstance.class.getName()));
        indexCreateOrUpdateEvent.fire(new IndexCreateOrUpdateEvent(USER_TASK_INSTANCES_STORAGE, UserTaskInstance.class.getName()));
        indexCreateOrUpdateEvent.fire(new IndexCreateOrUpdateEvent(JOBS_STORAGE, Job.class.getName()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V, E> MongoEntityMapper<V, E> getEntityMapper(String name) {
        Supplier<MongoEntityMapper<?, ?>> supplier = ENTITY_MAPPER_MAP.get(name);
        if (Objects.nonNull(supplier)) {
            return (MongoEntityMapper<V, E>) supplier.get();
        } else if (isDomainCollection(name)) {
            return (MongoEntityMapper<V, E>) new DomainEntityMapper();
        }
        return null;
    }
}
