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
package org.kie.kogito.jbpm.usertask.handler;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.internal.process.workitem.InvalidTransitionException;
import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemManager;
import org.kie.kogito.internal.process.workitem.Policy;
import org.kie.kogito.internal.process.workitem.WorkItemLifeCycle;
import org.kie.kogito.internal.process.workitem.WorkItemLifeCyclePhase;
import org.kie.kogito.internal.process.workitem.WorkItemPhaseState;
import org.kie.kogito.internal.process.workitem.WorkItemTerminationType;
import org.kie.kogito.internal.process.workitem.WorkItemTransition;
import org.kie.kogito.process.workitems.InternalKogitoWorkItem;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;
import org.kie.kogito.process.workitems.impl.DefaultWorkItemLifeCycle;
import org.kie.kogito.process.workitems.impl.DefaultWorkItemLifeCyclePhase;
import org.kie.kogito.usertask.UserTask;
import org.kie.kogito.usertask.UserTasks;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;

import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;

/**
 * Default Work Item handler based on the standard life cycle
 */
public class UserTaskKogitoWorkItemHandler extends DefaultKogitoWorkItemHandler {

    private static String UT_SEPARATOR = System.getProperty("org.jbpm.ht.user.separator", ",");

    public static final WorkItemPhaseState INACTIVE = WorkItemPhaseState.initialized();
    public static final WorkItemPhaseState COMPLETED = WorkItemPhaseState.of("Completed", WorkItemTerminationType.COMPLETE);
    public static final WorkItemPhaseState ABORTED = WorkItemPhaseState.of("Aborted", WorkItemTerminationType.ABORT);
    public static final WorkItemPhaseState ACTIVATED = WorkItemPhaseState.of("Activated");
    public static final WorkItemPhaseState RESERVED = WorkItemPhaseState.of("Reserved");

    public static final WorkItemLifeCyclePhase TRANSITION_RESERVED_COMPLETE =
            new DefaultWorkItemLifeCyclePhase("complete", RESERVED, COMPLETED, UserTaskKogitoWorkItemHandler::userTaskCompleteWorkItemHandler);
    public static final WorkItemLifeCyclePhase TRANSITION_ACTIVATED_COMPLETE =
            new DefaultWorkItemLifeCyclePhase("complete", ACTIVATED, COMPLETED, UserTaskKogitoWorkItemHandler::userTaskCompleteFromActiveWorkItemHandler);
    public static final WorkItemLifeCyclePhase TRANSITION_RESERVED_ABORT =
            new DefaultWorkItemLifeCyclePhase("abort", RESERVED, ABORTED, UserTaskKogitoWorkItemHandler::userTaskAbortWorkItemHandler);
    public static final WorkItemLifeCyclePhase TRANSITION_ACTIVATED_ABORT =
            new DefaultWorkItemLifeCyclePhase("abort", ACTIVATED, ABORTED, UserTaskKogitoWorkItemHandler::userTaskAbortWorkItemHandler);
    public static final WorkItemLifeCyclePhase TRANSITION_ACTIVATED_CLAIM =
            new DefaultWorkItemLifeCyclePhase("claim", ACTIVATED, RESERVED, UserTaskKogitoWorkItemHandler::userTaskClaimWorkItemHandler);
    public static final WorkItemLifeCyclePhase TRANSITION_CREATED_ACTIVE =
            new DefaultWorkItemLifeCyclePhase("activate", INACTIVE, ACTIVATED, UserTaskKogitoWorkItemHandler::userTaskActivateWorkItemHandler);
    public static final WorkItemLifeCyclePhase TRANSITION_RESERVED_RELEASE =
            new DefaultWorkItemLifeCyclePhase("release", RESERVED, ACTIVATED, UserTaskKogitoWorkItemHandler::userTaskReleaseWorkItemHandler);
    public static final WorkItemLifeCyclePhase TRANSITION_ACTIVATED_COMPLETED =
            new DefaultWorkItemLifeCyclePhase("skip", ACTIVATED, COMPLETED, UserTaskKogitoWorkItemHandler::userTaskCompleteWorkItemHandler);

    private static final String DESCRIPTION = "Description";
    private static final String PRIORITY = "Priority";
    private static final String TASK_NAME = "TaskName";
    private static final String ACTOR_ID = "ActorId";
    private static final String GROUP_ID = "GroupId";
    private static final String BUSINESSADMINISTRATOR_ID = "BusinessAdministratorId";
    private static final String BUSINESSADMINISTRATOR_GROUP_ID = "BusinessAdministratorGroupId";
    private static final String EXCLUDED_OWNER_ID = "ExcludedOwnerId";

    @Override
    public String getName() {
        return "Human Task";
    }

    @Override
    public WorkItemLifeCycle initialize() {
        return new DefaultWorkItemLifeCycle(
                TRANSITION_CREATED_ACTIVE,
                TRANSITION_ACTIVATED_CLAIM,
                TRANSITION_ACTIVATED_ABORT,
                TRANSITION_ACTIVATED_COMPLETE,
                TRANSITION_RESERVED_RELEASE,
                TRANSITION_RESERVED_ABORT,
                TRANSITION_RESERVED_COMPLETE,
                TRANSITION_ACTIVATED_COMPLETED);
    }

    @Override
    public WorkItemTransition startingTransition(Map<String, Object> data, Policy... policies) {
        return workItemLifeCycle.newTransition("activate", null, data, policies);
    }

    @Override
    public WorkItemTransition abortTransition(String phaseStatus, Policy... policies) {
        return workItemLifeCycle.newTransition("abort", phaseStatus, emptyMap(), policies);
    }

    @Override
    public WorkItemTransition completeTransition(String phaseStatus, Map<String, Object> data, Policy... policies) {
        return workItemLifeCycle.newTransition("complete", phaseStatus, data, policies);
    }

    static public Optional<WorkItemTransition> userTaskActivateWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
        UserTasks userTasks = handler.getApplication().get(UserTasks.class);

        Object priority = workItem.getParameter(PRIORITY);
        Integer priorityInteger = null;
        if (priority instanceof String priorityString) {
            priorityInteger = Integer.parseInt((String) priorityString);
        } else {
            priority = (Integer) priority;
        }

        UserTask userTask = userTasks.userTaskById((String) workItem.getParameter(KogitoWorkItem.PARAMETER_UNIQUE_TASK_ID));

        DefaultUserTaskInstance instance = (DefaultUserTaskInstance) userTask.createInstance();
        instance.setId(workItem.getStringId());
        instance.setTaskName((String) workItem.getParameter(TASK_NAME));
        instance.setTaskDescription((String) workItem.getParameter(DESCRIPTION));
        instance.setTaskPriority(priorityInteger);
        instance.setExternalReferenceId(workItem.getStringId());
        instance.setMetadata("ProcessId", workItem.getProcessInstance().getProcessId());
        instance.setMetadata("ProcessType", workItem.getProcessInstance().getProcess().getType());
        instance.setMetadata("ProcessVersion", workItem.getProcessInstance().getProcessVersion());
        instance.setMetadata("ProcessInstanceId", workItem.getProcessInstance().getId());
        instance.setMetadata("ProcessInstanceState", workItem.getProcessInstance().getState());
        instance.setMetadata("RootProcessId", workItem.getProcessInstance().getRootProcessId());
        instance.setMetadata("RootProcessInstanceId", workItem.getProcessInstance().getRootProcessInstanceId());
        instance.setMetadata("ParentProcessInstanceId", workItem.getProcessInstance().getParentProcessInstanceId());

        ofNullable(workItem.getParameters().get(ACTOR_ID)).map(String.class::cast).map(UserTaskKogitoWorkItemHandler::toSet).ifPresent(instance::setPotentialUsers);
        ofNullable(workItem.getParameters().get(GROUP_ID)).map(String.class::cast).map(UserTaskKogitoWorkItemHandler::toSet).ifPresent(instance::setPotentialGroups);
        ofNullable(workItem.getParameters().get(BUSINESSADMINISTRATOR_ID)).map(String.class::cast).map(UserTaskKogitoWorkItemHandler::toSet).ifPresent(instance::setAdminUsers);
        ofNullable(workItem.getParameters().get(BUSINESSADMINISTRATOR_GROUP_ID)).map(String.class::cast).map(UserTaskKogitoWorkItemHandler::toSet).ifPresent(instance::setAdminGroups);
        ofNullable(workItem.getParameters().get(EXCLUDED_OWNER_ID)).map(String.class::cast).map(UserTaskKogitoWorkItemHandler::toSet).ifPresent(instance::setExcludedUsers);

        instance.assign();
        instance.transition(instance.createTransitionToken("activate", emptyMap()));

        if (workItem instanceof InternalKogitoWorkItem ikw) {
            ikw.setExternalReferenceId(instance.getId());
            ikw.setActualOwner(instance.getActualOwner());
        }
        userTask.instances().create(instance);
        return Optional.empty();
    }

    static protected Set<String> toSet(String value) {
        if (value == null) {
            return null;
        }
        return Set.of(value.split(UT_SEPARATOR));
    }

    static public Optional<WorkItemTransition> userTaskClaimWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
        workItem.removeOutput("ACTUAL_OWNER");

        UserTasks userTasks = handler.getApplication().get(UserTasks.class);
        UserTask userTask = userTasks.userTaskById((String) workItem.getParameter(KogitoWorkItem.PARAMETER_UNIQUE_TASK_ID));
        userTask.instances().findById(workItem.getExternalReferenceId()).ifPresent(ut -> {
            Map<String, Object> data = transition.data();
            if (workItem instanceof InternalKogitoWorkItem ikw) {
                getUserFromTransition(transition).ifPresent(ikw::setActualOwner);
                if (data.containsKey("ACTUAL_OWNER") && ikw.getActualOwner() == null) {
                    ut.setActuaOwner((String) data.get("ACTUAL_OWNER"));
                }
                if (ikw.getActualOwner() == null) {
                    throw new InvalidTransitionException("transition claim does not contain user id");
                }
            }
            ut.setActuaOwner(workItem.getActualOwner());
            ut.transition(ut.createTransitionToken("claim", emptyMap()));
            userTask.instances().update(ut);
        });
        return Optional.empty();
    }

    static public Optional<WorkItemTransition> userTaskReleaseWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
        if (workItem instanceof InternalKogitoWorkItem ikw) {
            ikw.setActualOwner(null);
        }
        UserTasks userTasks = handler.getApplication().get(UserTasks.class);
        UserTask userTask = userTasks.userTaskById((String) workItem.getParameter(KogitoWorkItem.PARAMETER_UNIQUE_TASK_ID));
        userTask.instances().findById(workItem.getExternalReferenceId()).ifPresent(ut -> {
            ut.setActuaOwner(null);
            ut.transition(ut.createTransitionToken("release", emptyMap()));
            userTask.instances().update(ut);
        });

        return Optional.empty();
    }

    static public Optional<WorkItemTransition> userTaskCompleteWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
        UserTasks userTasks = handler.getApplication().get(UserTasks.class);
        UserTask userTask = userTasks.userTaskById((String) workItem.getParameter(KogitoWorkItem.PARAMETER_UNIQUE_TASK_ID));
        userTask.instances().findById(workItem.getExternalReferenceId()).ifPresent(ut -> {
            ut.transition(ut.createTransitionToken("complete", emptyMap()));
            userTask.instances().update(ut);
        });
        if (workItem instanceof InternalKogitoWorkItem ikw && ikw.getActualOwner() == null) {
            getUserFromTransition(transition).ifPresent(user -> {
                ikw.setActualOwner(user);
            });
        }
        return Optional.empty();
    }

    static public Optional<WorkItemTransition> userTaskCompleteFromActiveWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem,
            WorkItemTransition transition) {
        UserTasks userTasks = handler.getApplication().get(UserTasks.class);
        UserTask userTask = userTasks.userTaskById((String) workItem.getParameter(KogitoWorkItem.PARAMETER_UNIQUE_TASK_ID));
        userTask.instances().findById(workItem.getExternalReferenceId()).ifPresent(ut -> {
            ut.transition(ut.createTransitionToken("complete", emptyMap()));
            userTask.instances().update(ut);
        });
        if (workItem instanceof InternalKogitoWorkItem ikw) {
            getUserFromTransition(transition).ifPresent(user -> {
                ikw.setActualOwner(user);
            });
        }
        return Optional.empty();
    }

    static public Optional<WorkItemTransition> userTaskAbortWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
        UserTasks userTasks = handler.getApplication().get(UserTasks.class);
        UserTask userTask = userTasks.userTaskById((String) workItem.getParameter(KogitoWorkItem.PARAMETER_UNIQUE_TASK_ID));
        userTask.instances().findById(workItem.getExternalReferenceId()).ifPresent(ut -> {
            ut.transition(ut.createTransitionToken("skip", emptyMap()));
            userTask.instances().update(ut);
        });
        return Optional.empty();
    }

    private static Optional<String> getUserFromTransition(WorkItemTransition transition) {
        Optional<SecurityPolicy> securityPolicy = transition.policies().stream().filter(SecurityPolicy.class::isInstance).map(SecurityPolicy.class::cast).findAny();
        if (securityPolicy.isPresent()) {
            return Optional.ofNullable(securityPolicy.get().getUser());
        }
        return Optional.empty();
    }
}
