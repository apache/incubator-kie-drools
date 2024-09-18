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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.UserTaskInstances;

import com.fasterxml.jackson.databind.ObjectMapper;

public class InMemoryUserTaskInstances implements UserTaskInstances {

    private Map<String, byte[]> userTaskInstances;
    private Function<UserTaskInstance, UserTaskInstance> reconnectUserTaskInstance;
    private Function<UserTaskInstance, UserTaskInstance> disconnectUserTaskInstance;
    private ObjectMapper mapper;

    public InMemoryUserTaskInstances() {
        this.userTaskInstances = new HashMap<>();
        this.reconnectUserTaskInstance = Function.identity();
        this.disconnectUserTaskInstance = Function.identity();
        this.mapper = new ObjectMapper();
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
            UserTaskInstance userTaskInstance = mapper.readValue(userTaskInstances.get(userTaskInstanceId), DefaultUserTaskInstance.class);
            return Optional.ofNullable(reconnectUserTaskInstance.apply(userTaskInstance));
        } catch (Exception e) {
            return Optional.empty();
        }
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
            return null;
        }
    }

    @Override
    public UserTaskInstance remove(String userTaskInstanceId) {
        try {
            return disconnectUserTaskInstance.apply(mapper.readValue(userTaskInstances.remove(userTaskInstanceId), DefaultUserTaskInstance.class));
        } catch (Exception e) {
            return null;
        }
    }

}
