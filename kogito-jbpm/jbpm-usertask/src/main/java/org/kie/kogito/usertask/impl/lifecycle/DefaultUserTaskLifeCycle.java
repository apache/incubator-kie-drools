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
package org.kie.kogito.usertask.impl.lifecycle;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.lifecycle.UserTaskLifeCycle;
import org.kie.kogito.usertask.lifecycle.UserTaskState;
import org.kie.kogito.usertask.lifecycle.UserTaskState.TerminationType;
import org.kie.kogito.usertask.lifecycle.UserTaskTransition;
import org.kie.kogito.usertask.lifecycle.UserTaskTransitionToken;

public class DefaultUserTaskLifeCycle implements UserTaskLifeCycle {

    public static final UserTaskState INACTIVE = UserTaskState.of(null);
    public static final UserTaskState ACTIVE = UserTaskState.of("Ready");
    public static final UserTaskState RESERVED = UserTaskState.of("Reserved");
    public static final UserTaskState COMPLETED = UserTaskState.of("Completed", TerminationType.COMPLETED);
    public static final UserTaskState ERROR = UserTaskState.of("Error", TerminationType.ERROR);
    public static final UserTaskState OBSOLETE = UserTaskState.of("Obsolete", TerminationType.OBSOLETE);

    private static final UserTaskTransition T_NEW_ACTIVE = new DefaultUserTransition("activate", INACTIVE, ACTIVE, DefaultUserTaskLifeCycle::activate);
    private static final UserTaskTransition T_ACTIVE_RESERVED = new DefaultUserTransition("claim", ACTIVE, RESERVED, DefaultUserTaskLifeCycle::claim);
    private static final UserTaskTransition T_RESERVED_ACTIVE = new DefaultUserTransition("release", RESERVED, ACTIVE, DefaultUserTaskLifeCycle::release);
    private static final UserTaskTransition T_ACTIVE_COMPLETED = new DefaultUserTransition("complete", ACTIVE, COMPLETED, DefaultUserTaskLifeCycle::complete);
    private static final UserTaskTransition T_RESERVED_COMPLETED = new DefaultUserTransition("complete", RESERVED, COMPLETED, DefaultUserTaskLifeCycle::complete);
    private static final UserTaskTransition T_RESERVED_SKIPPED = new DefaultUserTransition("skip", RESERVED, OBSOLETE, DefaultUserTaskLifeCycle::skip);
    private static final UserTaskTransition T_ACTIVE_SKIPPED = new DefaultUserTransition("skip", ACTIVE, OBSOLETE, DefaultUserTaskLifeCycle::complete);
    private static final UserTaskTransition T_RESERVED_ERROR = new DefaultUserTransition("fail", RESERVED, ERROR, DefaultUserTaskLifeCycle::fail);

    private List<UserTaskTransition> transitions;

    public DefaultUserTaskLifeCycle() {
        transitions = List.of(
                T_NEW_ACTIVE,
                T_ACTIVE_RESERVED,
                T_RESERVED_ACTIVE,
                T_ACTIVE_COMPLETED,
                T_RESERVED_COMPLETED,
                T_ACTIVE_SKIPPED,
                T_RESERVED_SKIPPED,
                T_RESERVED_ERROR);
    }

    @Override
    public Optional<UserTaskTransitionToken> transition(UserTaskInstance userTaskInstance, UserTaskTransitionToken transition) {
        return Optional.empty();
    }

    @Override
    public UserTaskTransitionToken newTransitionToken(String transitionId, UserTaskInstance userTaskInstance, Map<String, Object> data) {
        UserTaskState state = userTaskInstance.getStatus();
        UserTaskTransition transition = transitions.stream().filter(e -> e.source().equals(state) && e.id().equals(transitionId)).findAny()
                .orElseThrow(() -> new RuntimeException("Invalid transition " + transitionId + " from " + state));
        return new DefaultUserTaskTransitionToken(transition, data);
    }

    @Override
    public UserTaskTransitionToken newCompleteTransitionToken(UserTaskInstance userTaskInstance, Map<String, Object> data) {
        return newTransitionToken("complete", userTaskInstance, data);
    }

    @Override
    public UserTaskTransitionToken newAbortTransitionToken(UserTaskInstance userTaskInstance, Map<String, Object> data) {
        return newTransitionToken("fail", userTaskInstance, data);
    }

    public static Optional<UserTaskTransitionToken> activate(UserTaskInstance userTaskInstance, UserTaskTransitionToken token) {
        return Optional.empty();
    }

    public static Optional<UserTaskTransitionToken> claim(UserTaskInstance userTaskInstance, UserTaskTransitionToken token) {
        return Optional.empty();
    }

    public static Optional<UserTaskTransitionToken> release(UserTaskInstance userTaskInstance, UserTaskTransitionToken token) {
        return Optional.empty();
    }

    public static Optional<UserTaskTransitionToken> complete(UserTaskInstance userTaskInstance, UserTaskTransitionToken token) {
        return Optional.empty();
    }

    public static Optional<UserTaskTransitionToken> skip(UserTaskInstance userTaskInstance, UserTaskTransitionToken token) {
        return Optional.empty();
    }

    public static Optional<UserTaskTransitionToken> fail(UserTaskInstance userTaskInstance, UserTaskTransitionToken token) {
        return Optional.empty();
    }
}
