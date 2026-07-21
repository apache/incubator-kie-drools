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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.usertask.UserTaskAssignmentStrategy;
import org.kie.kogito.usertask.UserTaskInstance;

public class BasicUserTaskAssignmentStrategy implements UserTaskAssignmentStrategy {

    @Override
    public String getName() {
        return DEFAULT_NAME;
    }

    @Override
    public Optional<String> computeAssignment(UserTaskInstance userTaskInstance, IdentityProvider identityProvider) {
        Set<String> users = new HashSet<>(userTaskInstance.getPotentialUsers());
        users.removeAll(userTaskInstance.getExcludedUsers());
        if (users.size() == 1) {
            return Optional.of(users.iterator().next());
        }
        return Optional.empty();
    }

}
