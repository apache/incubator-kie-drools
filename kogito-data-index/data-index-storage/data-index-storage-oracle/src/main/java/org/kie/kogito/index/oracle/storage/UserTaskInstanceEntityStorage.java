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
package org.kie.kogito.index.oracle.storage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.oracle.mapper.UserTaskInstanceEntityMapper;
import org.kie.kogito.index.oracle.model.AbstractEntity;
import org.kie.kogito.index.oracle.model.UserTaskInstanceEntity;
import org.kie.kogito.index.oracle.model.UserTaskInstanceEntityRepository;

@ApplicationScoped
public class UserTaskInstanceEntityStorage extends AbstractStorage<UserTaskInstanceEntity, UserTaskInstance> {

    public UserTaskInstanceEntityStorage() {
    }

    @Inject
    public UserTaskInstanceEntityStorage(UserTaskInstanceEntityRepository repository, UserTaskInstanceEntityMapper mapper) {
        super(repository, UserTaskInstance.class, UserTaskInstanceEntity.class, mapper::mapToModel, mapper::mapToEntity, AbstractEntity::getId);
    }
}
