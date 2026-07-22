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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.kogito.usertask.UserTaskAssignmentStrategy;
import org.kie.kogito.usertask.UserTaskAssignmentStrategyConfig;

public class DefaultUserTaskAssignmentStrategyConfig implements UserTaskAssignmentStrategyConfig {

    private Map<String, UserTaskAssignmentStrategy> userTaskAssignmentStrategies;

    public DefaultUserTaskAssignmentStrategyConfig() {
        this.userTaskAssignmentStrategies = new HashMap<>();
        BasicUserTaskAssignmentStrategy strategy = new BasicUserTaskAssignmentStrategy();
        this.userTaskAssignmentStrategies.put(strategy.getName(), strategy);
    }

    public DefaultUserTaskAssignmentStrategyConfig(Iterable<UserTaskAssignmentStrategy> userTaskAssignmentStrategies) {
        this();
        userTaskAssignmentStrategies.forEach(uts -> this.userTaskAssignmentStrategies.put(uts.getName(), uts));
    }

    @Override
    public List<UserTaskAssignmentStrategy> userTaskAssignmentStrategies() {
        return new ArrayList<>(userTaskAssignmentStrategies.values());
    }

    @Override
    public UserTaskAssignmentStrategy forName(String name) {
        return userTaskAssignmentStrategies.get(name);
    }

}
