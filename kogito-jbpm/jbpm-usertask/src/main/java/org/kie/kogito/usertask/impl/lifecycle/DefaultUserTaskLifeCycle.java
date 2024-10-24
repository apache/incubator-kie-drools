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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.usertask.UserTaskAssignmentStrategy;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.UserTaskInstanceNotAuthorizedException;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.kie.kogito.usertask.lifecycle.UserTaskLifeCycle;
import org.kie.kogito.usertask.lifecycle.UserTaskState;
import org.kie.kogito.usertask.lifecycle.UserTaskState.TerminationType;
import org.kie.kogito.usertask.lifecycle.UserTaskTransition;
import org.kie.kogito.usertask.lifecycle.UserTaskTransitionException;
import org.kie.kogito.usertask.lifecycle.UserTaskTransitionToken;

public class DefaultUserTaskLifeCycle implements UserTaskLifeCycle {
    public static final String WORKFLOW_ENGINE_USER = "WORKFLOW_ENGINE_USER";

    public static final String PARAMETER_USER = "USER";
    public static final String PARAMETER_NOTIFY = "NOTIFY";

    public static final String ACTIVATE = "activate";
    public static final String CLAIM = "claim";
    public static final String RELEASE = "release";
    public static final String COMPLETE = "complete";
    public static final String SKIP = "skip";
    public static final String FAIL = "fail";

    public static final UserTaskState INACTIVE = UserTaskState.initalized();
    public static final UserTaskState ACTIVE = UserTaskState.of("Ready");
    public static final UserTaskState RESERVED = UserTaskState.of("Reserved");
    public static final UserTaskState COMPLETED = UserTaskState.of("Completed", TerminationType.COMPLETED);
    public static final UserTaskState ERROR = UserTaskState.of("Error", TerminationType.ERROR);
    public static final UserTaskState OBSOLETE = UserTaskState.of("Obsolete", TerminationType.OBSOLETE);

    private final UserTaskTransition T_NEW_ACTIVE = new DefaultUserTransition(ACTIVATE, INACTIVE, ACTIVE, this::activate);
    private final UserTaskTransition T_ACTIVE_RESERVED = new DefaultUserTransition(CLAIM, ACTIVE, RESERVED, this::claim);
    private final UserTaskTransition T_ACTIVE_SKIPPED = new DefaultUserTransition(SKIP, ACTIVE, OBSOLETE, this::skip);
    private final UserTaskTransition T_ACTIVE_ERROR = new DefaultUserTransition(FAIL, ACTIVE, ERROR, this::fail);
    private final UserTaskTransition T_RESERVED_ACTIVE = new DefaultUserTransition(RELEASE, RESERVED, ACTIVE, this::release);
    private final UserTaskTransition T_RESERVED_COMPLETED = new DefaultUserTransition(COMPLETE, RESERVED, COMPLETED, this::complete);
    private final UserTaskTransition T_RESERVED_SKIPPED = new DefaultUserTransition(SKIP, RESERVED, OBSOLETE, this::skip);
    private final UserTaskTransition T_RESERVED_ERROR = new DefaultUserTransition(FAIL, RESERVED, ERROR, this::fail);

    private List<UserTaskTransition> transitions;

    public DefaultUserTaskLifeCycle() {
        transitions = List.of(
                T_NEW_ACTIVE,
                T_ACTIVE_RESERVED,
                T_ACTIVE_SKIPPED,
                T_ACTIVE_ERROR,
                T_RESERVED_ACTIVE,
                T_RESERVED_COMPLETED,
                T_RESERVED_SKIPPED,
                T_RESERVED_ERROR);
    }

    @Override
    public List<UserTaskTransition> allowedTransitions(UserTaskInstance userTaskInstance) {
        return transitions.stream().filter(t -> t.source().equals(userTaskInstance.getStatus())).toList();
    }

    @Override
    public Optional<UserTaskTransitionToken> transition(UserTaskInstance userTaskInstance, UserTaskTransitionToken userTaskTransitionToken, IdentityProvider identityProvider) {
        checkPermission(userTaskInstance, identityProvider);
        UserTaskTransition transition = transitions.stream()
                .filter(t -> t.source().equals(userTaskInstance.getStatus()) && t.id().equals(userTaskTransitionToken.transitionId()))
                .findFirst()
                .orElseThrow(() -> new UserTaskTransitionException("Invalid transition from " + userTaskInstance.getStatus()));
        return transition.executor().execute(userTaskInstance, userTaskTransitionToken, identityProvider);
    }

    @Override
    public UserTaskTransitionToken newCompleteTransitionToken(UserTaskInstance userTaskInstance, Map<String, Object> data) {
        return newTransitionToken(COMPLETE, userTaskInstance.getStatus(), data);
    }

    @Override
    public UserTaskTransitionToken newAbortTransitionToken(UserTaskInstance userTaskInstance, Map<String, Object> data) {
        return newTransitionToken(FAIL, userTaskInstance.getStatus(), data);
    }

    @Override
    public UserTaskTransitionToken newTransitionToken(String transitionId, UserTaskInstance userTaskInstance, Map<String, Object> data) {
        return newTransitionToken(transitionId, userTaskInstance.getStatus(), data);
    }

    public UserTaskTransitionToken newTransitionToken(String transitionId, UserTaskState state, Map<String, Object> data) {
        UserTaskTransition transition = transitions.stream().filter(e -> e.source().equals(state) && e.id().equals(transitionId)).findAny()
                .orElseThrow(() -> new RuntimeException("Invalid transition " + transitionId + " from " + state));
        return new DefaultUserTaskTransitionToken(transition.id(), transition.source(), transition.target(), data);
    }

    public Optional<UserTaskTransitionToken> activate(UserTaskInstance userTaskInstance, UserTaskTransitionToken token, IdentityProvider identityProvider) {
        String user = assignStrategy(userTaskInstance, identityProvider);
        if (user != null) {
            return Optional.of(newTransitionToken(CLAIM, ACTIVE, Map.of(PARAMETER_USER, user)));
        }
        return Optional.empty();
    }

    public Optional<UserTaskTransitionToken> claim(UserTaskInstance userTaskInstance, UserTaskTransitionToken token, IdentityProvider identityProvider) {
        if (userTaskInstance instanceof DefaultUserTaskInstance defaultUserTaskInstance) {
            if (token.data().containsKey(PARAMETER_USER)) {
                defaultUserTaskInstance.setActualOwner((String) token.data().get(PARAMETER_USER));
            } else {
                defaultUserTaskInstance.setActualOwner(identityProvider.getName());
            }
        }
        return Optional.empty();
    }

    public Optional<UserTaskTransitionToken> release(UserTaskInstance userTaskInstance, UserTaskTransitionToken token, IdentityProvider identityProvider) {
        if (userTaskInstance instanceof DefaultUserTaskInstance defaultUserTaskInstance) {
            defaultUserTaskInstance.setActualOwner(null);
        }
        return Optional.empty();
    }

    public Optional<UserTaskTransitionToken> complete(UserTaskInstance userTaskInstance, UserTaskTransitionToken token, IdentityProvider identityProvider) {
        token.data().forEach(userTaskInstance::setOutput);
        return Optional.empty();
    }

    public Optional<UserTaskTransitionToken> skip(UserTaskInstance userTaskInstance, UserTaskTransitionToken token, IdentityProvider identityProvider) {
        if (token.data().containsKey(PARAMETER_NOTIFY)) {
            userTaskInstance.getMetadata().put(PARAMETER_NOTIFY, token.data().get(PARAMETER_NOTIFY));
        }
        return Optional.empty();
    }

    public Optional<UserTaskTransitionToken> fail(UserTaskInstance userTaskInstance, UserTaskTransitionToken token, IdentityProvider identityProvider) {
        if (token.data().containsKey(PARAMETER_NOTIFY)) {
            userTaskInstance.getMetadata().put(PARAMETER_NOTIFY, token.data().get(PARAMETER_NOTIFY));
        }
        return Optional.empty();
    }

    private String assignStrategy(UserTaskInstance userTaskInstance, IdentityProvider identityProvider) {
        UserTaskAssignmentStrategy assignmentStrategy = userTaskInstance.getUserTask().getAssignmentStrategy();
        return assignmentStrategy.computeAssigment(userTaskInstance, identityProvider).orElse(null);
    }

    private void checkPermission(UserTaskInstance userTaskInstance, IdentityProvider identityProvider) {
        String user = identityProvider.getName();
        Collection<String> roles = identityProvider.getRoles();

        if (WORKFLOW_ENGINE_USER.equals(user)) {
            return;
        }

        // first we check admins
        Set<String> adminUsers = userTaskInstance.getAdminUsers();
        if (adminUsers.contains(user)) {
            return;
        }

        Set<String> userAdminGroups = new HashSet<>(userTaskInstance.getAdminGroups());
        userAdminGroups.retainAll(roles);
        if (!userAdminGroups.isEmpty()) {
            return;
        }

        if (userTaskInstance.getActualOwner() != null && userTaskInstance.getActualOwner().equals(user)) {
            return;
        }

        if (List.of(INACTIVE, ACTIVE).contains(userTaskInstance.getStatus())) {
            // there is no user
            Set<String> users = new HashSet<>(userTaskInstance.getPotentialUsers());
            users.removeAll(userTaskInstance.getExcludedUsers());
            if (users.contains(identityProvider.getName())) {
                return;
            }

            Set<String> userPotGroups = new HashSet<>(userTaskInstance.getPotentialGroups());
            userPotGroups.retainAll(roles);
            if (!userPotGroups.isEmpty()) {
                return;
            }
        }

        throw new UserTaskInstanceNotAuthorizedException("user " + user + " with roles " + roles + " not autorized to perform an operation on user task " + userTaskInstance.getId());
    }

}
