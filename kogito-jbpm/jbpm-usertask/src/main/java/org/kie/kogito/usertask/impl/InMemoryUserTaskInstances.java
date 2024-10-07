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
package org.kie.kogito.usertask.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.UserTaskInstances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class InMemoryUserTaskInstances implements UserTaskInstances {

    private static Logger LOG = LoggerFactory.getLogger(InMemoryUserTaskInstances.class);

    private Map<String, byte[]> userTaskInstances;
    private Function<UserTaskInstance, UserTaskInstance> reconnectUserTaskInstance;
    private Function<UserTaskInstance, UserTaskInstance> disconnectUserTaskInstance;
    private ObjectMapper mapper;

    public InMemoryUserTaskInstances() {
        LOG.info("Initializing InMemoryUsertaskInstances");
        this.userTaskInstances = new HashMap<>();
        this.reconnectUserTaskInstance = null;
        this.disconnectUserTaskInstance = null;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void setReconnectUserTaskInstance(Function<UserTaskInstance, UserTaskInstance> reconnectUserTaskInstance) {
        this.reconnectUserTaskInstance = reconnectUserTaskInstance;
    }

    @Override
    public void setDisconnectUserTaskInstance(Function<UserTaskInstance, UserTaskInstance> disconnectUserTaskInstance) {
        this.disconnectUserTaskInstance = disconnectUserTaskInstance;
    }

    @Override
    public Optional<UserTaskInstance> findById(String userTaskInstanceId) {
        try {
            if (!userTaskInstances.containsKey(userTaskInstanceId)) {
                return Optional.empty();
            }
            UserTaskInstance userTaskInstance = mapper.readValue(userTaskInstances.get(userTaskInstanceId), DefaultUserTaskInstance.class);
            return Optional.ofNullable(reconnectUserTaskInstance.apply(userTaskInstance));
        } catch (Exception e) {
            LOG.error("during find by Id {}", userTaskInstanceId, e);
            return Optional.empty();
        }
    }

    @Override
    public List<UserTaskInstance> findByIdentity(IdentityProvider identity) {
        try {
            String user = identity.getName();
            Collection<String> roles = identity.getRoles();
            List<UserTaskInstance> users = new ArrayList<>();
            for (String id : userTaskInstances.keySet()) {
                UserTaskInstance userTaskInstance = mapper.readValue(userTaskInstances.get(id), DefaultUserTaskInstance.class);
                if (checkVisibility(userTaskInstance, user, roles)) {
                    users.add(reconnectUserTaskInstance.apply(userTaskInstance));
                }
            }
            return users;
        } catch (Exception e) {
            LOG.error("during find by Identity {}", identity.getName(), e);
            return Collections.emptyList();
        }
    }

    private boolean checkVisibility(UserTaskInstance userTaskInstance, String user, Collection<String> roles) {
        Set<String> adminUsers = userTaskInstance.getAdminUsers();
        if (adminUsers.contains(user)) {
            return true;
        }

        Set<String> userAdminGroups = new HashSet<>(userTaskInstance.getAdminGroups());
        userAdminGroups.retainAll(roles);
        if (!userAdminGroups.isEmpty()) {
            return true;
        }

        if (userTaskInstance.getActualOwner() != null && userTaskInstance.getActualOwner().equals(user)) {
            return true;
        }

        // there is no user
        Set<String> users = new HashSet<>(userTaskInstance.getPotentialUsers());
        users.removeAll(userTaskInstance.getExcludedUsers());
        if (users.contains(user)) {
            return true;
        }

        Set<String> userPotGroups = new HashSet<>(userTaskInstance.getPotentialGroups());
        userPotGroups.retainAll(roles);
        if (!userPotGroups.isEmpty()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean exists(String userTaskInstanceId) {
        return userTaskInstances.containsKey(userTaskInstanceId);
    }

    @Override
    public UserTaskInstance create(UserTaskInstance userTaskInstance) {
        try {
            byte[] data = mapper.writeValueAsBytes(userTaskInstance);
            userTaskInstances.put(userTaskInstance.getId(), data);
            return reconnectUserTaskInstance.apply(userTaskInstance);
        } catch (Exception e) {
            LOG.error("during create {}", userTaskInstance.getId(), e);
            return null;
        }
    }

    @Override
    public UserTaskInstance update(UserTaskInstance userTaskInstance) {
        try {
            byte[] data = mapper.writeValueAsBytes(userTaskInstance);
            userTaskInstances.put(userTaskInstance.getId(), data);
            return userTaskInstance;
        } catch (Exception e) {
            LOG.error("during udpate {}", userTaskInstance.getId(), e);
            return null;
        }
    }

    @Override
    public UserTaskInstance remove(String userTaskInstanceId) {
        try {
            if (!userTaskInstances.containsKey(userTaskInstanceId)) {
                return null;
            }
            return disconnectUserTaskInstance.apply(mapper.readValue(userTaskInstances.remove(userTaskInstanceId), DefaultUserTaskInstance.class));
        } catch (Exception e) {
            LOG.error("during remove {}", userTaskInstanceId, e);
            return null;
        }
    }

}
