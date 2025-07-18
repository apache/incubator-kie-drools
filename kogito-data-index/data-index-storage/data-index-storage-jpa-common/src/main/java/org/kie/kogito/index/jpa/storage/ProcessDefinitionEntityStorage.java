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
package org.kie.kogito.index.jpa.storage;

import java.util.Optional;

import org.kie.kogito.index.jpa.mapper.ProcessDefinitionEntityMapper;
import org.kie.kogito.index.jpa.model.ProcessDefinitionEntity;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessDefinitionKey;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import static org.kie.kogito.index.DependencyInjectionUtils.getInstance;

@ApplicationScoped
public class ProcessDefinitionEntityStorage extends AbstractStorage<ProcessDefinitionKey, ProcessDefinitionEntity, ProcessDefinition> {

    protected ProcessDefinitionEntityStorage() {
    }

    @Inject
    public ProcessDefinitionEntityStorage(EntityManager em, Instance<JsonPredicateBuilder> predicateBuilder) {
        this(em, predicateBuilder, ProcessDefinitionEntityMapper.INSTANCE);
    }

    public ProcessDefinitionEntityStorage(EntityManager em, Iterable<JsonPredicateBuilder> predicateBuilder, ProcessDefinitionEntityMapper mapper) {
        super(em, ProcessDefinition.class, ProcessDefinitionEntity.class, mapper::mapToModel, mapper.INSTANCE::mapToEntity, e -> new ProcessDefinitionKey(e.getId(),
                e.getVersion()), Optional.ofNullable(getInstance(predicateBuilder)));
    }

}
