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

package org.kie.kogito.usertask.lifecycle;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.usertask.UserTaskInstance;

public interface UserTaskLifeCycle {

    final String ACTIVATE = "activate";

    default String startTransition() {
        return ACTIVATE;
    }

    Optional<UserTaskTransitionToken> transition(UserTaskInstance userTaskInstance, UserTaskTransitionToken transition, IdentityProvider identity);

    UserTaskTransitionToken newTransitionToken(String transitionId, UserTaskInstance userTaskInstance, Map<String, Object> data);

    default Optional<UserTaskTransitionToken> newReassignmentTransitionToken(UserTaskInstance defaultUserTaskInstance, Map<String, Object> data) {
        return Optional.empty();
    }

    UserTaskTransitionToken newCompleteTransitionToken(UserTaskInstance userTaskInstance, Map<String, Object> data);

    UserTaskTransitionToken newAbortTransitionToken(UserTaskInstance userTaskInstance, Map<String, Object> data);

    List<UserTaskTransition> allowedTransitions(UserTaskInstance ut);

}
