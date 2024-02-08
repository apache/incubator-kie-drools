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
import org.kie.kogito.index.jpa.model.ProcessDefinitionEntityId;
import org.kie.kogito.index.jpa.model.ProcessDefinitionEntityRepository;
import org.kie.kogito.index.model.ProcessDefinition;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProcessDefinitionEntityStorage extends AbstractStorage<ProcessDefinitionEntity, ProcessDefinition> {

    protected ProcessDefinitionEntityStorage() {
    }

    @Inject
    public ProcessDefinitionEntityStorage(ProcessDefinitionEntityRepository repository, ProcessDefinitionEntityMapper mapper) {
        super(new RepositoryAdapter(repository), ProcessDefinition.class, ProcessDefinitionEntity.class, mapper::mapToModel, mapper::mapToEntity, e -> new ProcessDefinitionEntityId(e.getId(),
                e.getVersion()).getKey());
    }

    @Transactional
    @Override
    public boolean containsKey(String key) {
        ProcessDefinitionEntityId id = new ProcessDefinitionEntityId(key);
        return getRepository().count("id = ?1 and version = ?2", id.getId(), id.getVersion()) == 1;
    }

    public static class RepositoryAdapter implements PanacheRepositoryBase<ProcessDefinitionEntity, String> {

        ProcessDefinitionEntityRepository repository;

        public RepositoryAdapter(ProcessDefinitionEntityRepository repository) {
            this.repository = repository;
        }

        @Override
        public boolean deleteById(String key) {
            return repository.deleteById(new ProcessDefinitionEntityId(key));
        }

        @Override
        public Optional<ProcessDefinitionEntity> findByIdOptional(String key) {
            return repository.findByIdOptional(new ProcessDefinitionEntityId(key));
        }

        @Override
        public ProcessDefinitionEntity findById(String s, LockModeType lockModeType) {
            return repository.findById(new ProcessDefinitionEntityId(s), lockModeType);
        }

        @Override
        public void persist(ProcessDefinitionEntity entity) {
            repository.persist(entity);
        }

        @Override
        public EntityManager getEntityManager() {
            return repository.getEntityManager();
        }
    }
}
