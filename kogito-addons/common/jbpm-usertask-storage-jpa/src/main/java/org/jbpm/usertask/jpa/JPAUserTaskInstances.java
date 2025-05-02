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

package org.jbpm.usertask.jpa;

import java.util.*;
import java.util.function.Function;

import org.jbpm.usertask.jpa.mapper.UserTaskInstanceEntityMapper;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.UserTaskInstanceRepository;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.UserTaskInstances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPAUserTaskInstances implements UserTaskInstances {
    public static final Logger LOGGER = LoggerFactory.getLogger(JPAUserTaskInstances.class);

    private final UserTaskInstanceRepository userTaskInstanceRepository;
    private final UserTaskInstanceEntityMapper userTaskInstanceEntityMapper;

    private Function<UserTaskInstance, UserTaskInstance> reconnectUserTaskInstance;
    private Function<UserTaskInstance, UserTaskInstance> disconnectUserTaskInstance;

    public JPAUserTaskInstances(UserTaskInstanceRepository userTaskInstanceRepository, UserTaskInstanceEntityMapper userTaskInstanceEntityMapper) {
        this.userTaskInstanceRepository = userTaskInstanceRepository;
        this.userTaskInstanceEntityMapper = userTaskInstanceEntityMapper;
    }

    @Override
    public Optional<UserTaskInstance> findById(String userTaskInstanceId) {
        return this.userTaskInstanceRepository.findById(userTaskInstanceId)
                .map(userTaskInstanceEntityMapper::mapTaskEntityToInstance)
                .map(reconnectUserTaskInstance);
    }

    @Override
    public List<UserTaskInstance> findByIdentity(IdentityProvider identityProvider) {
        return userTaskInstanceRepository.findByIdentity(identityProvider)
                .stream()
                .map(userTaskInstanceEntityMapper::mapTaskEntityToInstance)
                .map(reconnectUserTaskInstance)
                .toList();
    }

    @Override
    public boolean exists(String userTaskInstanceId) {
        return userTaskInstanceRepository.findById(userTaskInstanceId).isPresent();
    }

    @Override
    public UserTaskInstance create(UserTaskInstance userTaskInstance) {

        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();
        entity.setId(userTaskInstance.getId());

        userTaskInstanceEntityMapper.mapTaskInstanceToEntity(userTaskInstance, entity);

        this.userTaskInstanceRepository.persist(entity);

        return this.reconnectUserTaskInstance.apply(userTaskInstance);
    }

    @Override
    public UserTaskInstance update(UserTaskInstance userTaskInstance) {

        Optional<UserTaskInstanceEntity> optional = userTaskInstanceRepository.findById(userTaskInstance.getId());

        if (optional.isEmpty()) {
            LOGGER.error("Could not find userTaskInstance with id {}", userTaskInstance.getId());
            throw new RuntimeException("Could not find userTaskInstance with id " + userTaskInstance.getId());
        }

        UserTaskInstanceEntity userTaskInstanceEntity = optional.get();

        userTaskInstanceEntityMapper.mapTaskInstanceToEntity(userTaskInstance, userTaskInstanceEntity);

        userTaskInstanceRepository.update(userTaskInstanceEntity);

        return userTaskInstance;
    }

    @Override
    public UserTaskInstance remove(UserTaskInstance userTaskInstance) {
        Optional<UserTaskInstanceEntity> optional = userTaskInstanceRepository.findById(userTaskInstance.getId());

        if (optional.isEmpty()) {
            LOGGER.warn("Could not remove userTaskInstance with id {}, task cannot be found", userTaskInstance.getId());
            throw new RuntimeException("Could not remove userTaskInstance with id " + userTaskInstance.getId() + ", userTaskInstance cannot be found");
        }

        this.userTaskInstanceRepository.delete(optional.get());
        return this.disconnectUserTaskInstance.apply(userTaskInstance);
    }

    @Override
    public void setReconnectUserTaskInstance(Function<UserTaskInstance, UserTaskInstance> reconnectUserTaskInstance) {
        this.reconnectUserTaskInstance = reconnectUserTaskInstance;
    }

    @Override
    public void setDisconnectUserTaskInstance(Function<UserTaskInstance, UserTaskInstance> disconnectUserTaskInstance) {
        this.disconnectUserTaskInstance = disconnectUserTaskInstance;
    }
}
