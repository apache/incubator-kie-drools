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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.jbpm.workflow.core.node.HumanTaskNode;
import org.kie.kogito.Application;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemManager;
import org.kie.kogito.internal.process.workitem.WorkItemTransition;
import org.kie.kogito.process.workitems.InternalKogitoWorkItem;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;
import org.kie.kogito.usertask.UserTask;
import org.kie.kogito.usertask.UserTasks;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.kie.kogito.usertask.impl.lifecycle.DefaultUserTaskLifeCycle;
import org.kie.kogito.usertask.impl.model.DeadlineHelper;

import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;
import static org.kie.kogito.usertask.impl.lifecycle.DefaultUserTaskLifeCycle.PARAMETER_NOTIFY;
import static org.kie.kogito.usertask.impl.lifecycle.DefaultUserTaskLifeCycle.WORKFLOW_ENGINE_USER;

/**
 * Default Work Item handler based on the standard life cycle
 */
public class UserTaskKogitoWorkItemHandler extends DefaultKogitoWorkItemHandler {

    private static String UT_SEPARATOR = System.getProperty("org.jbpm.ht.user.separator", ",");

    private static final String DESCRIPTION = "Description";
    private static final String PRIORITY = "Priority";
    private static final String TASK_NAME = "TaskName";
    private static final String NODE_NAME = "NodeName";
    private static final String ACTOR_ID = "ActorId";
    private static final String GROUP_ID = "GroupId";
    private static final String BUSINESSADMINISTRATOR_ID = "BusinessAdministratorId";
    private static final String BUSINESSADMINISTRATOR_GROUP_ID = "BusinessAdministratorGroupId";
    private static final String EXCLUDED_OWNER_ID = "ExcludedOwnerId";

    private static final String NOT_STARTED_NOTIFY = "NotStartedNotify";
    private static final String NOT_STARTED_REASSIGN = "NotStartedReassign";
    private static final String NOT_COMPLETED_NOTIFY = "NotCompletedNotify";
    private static final String NOT_COMPLETED_REASSIGN = "NotCompletedReassign";

    public UserTaskKogitoWorkItemHandler() {
        super();
    }

    public UserTaskKogitoWorkItemHandler(Application application) {
        super();
        this.setApplication(application);
    }

    public Optional<WorkItemTransition> activateWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
        UserTasks userTasks = handler.getApplication().get(UserTasks.class);

        Object priority = workItem.getParameter(PRIORITY);
        UserTask userTask = userTasks.userTaskById((String) workItem.getParameter(KogitoWorkItem.PARAMETER_UNIQUE_TASK_ID));

        DefaultUserTaskInstance instance = (DefaultUserTaskInstance) userTask.createInstance();

        instance.setExternalReferenceId(workItem.getStringId());

        userTask.instances().create(instance);

        instance.setTaskName(ofNullable((String) workItem.getParameter(TASK_NAME)).orElse((String) workItem.getParameter(NODE_NAME)));
        instance.setTaskDescription((String) workItem.getParameter(DESCRIPTION));
        instance.setTaskPriority(priority != null ? priority.toString() : null);
        instance.setSlaDueDate(workItem.getNodeInstance().getSlaDueDate());

        instance.setMetadata("ProcessId", workItem.getProcessInstance().getProcessId());
        instance.setMetadata("ProcessType", workItem.getProcessInstance().getProcess().getType());
        instance.setMetadata("ProcessVersion", workItem.getProcessInstance().getProcessVersion());
        instance.setMetadata("ProcessInstanceId", workItem.getProcessInstance().getId());
        instance.setMetadata("ProcessInstanceState", workItem.getProcessInstance().getState());
        instance.setMetadata("RootProcessId", workItem.getProcessInstance().getRootProcessId());
        instance.setMetadata("RootProcessInstanceId", workItem.getProcessInstance().getRootProcessInstanceId());
        instance.setMetadata("ParentProcessInstanceId", workItem.getProcessInstance().getParentProcessInstanceId());

        instance.fireInitialStateChange();
        workItem.getParameters().entrySet().stream().filter(e -> !HumanTaskNode.TASK_PARAMETERS.contains(e.getKey())).forEach(e -> instance.setInput(e.getKey(), e.getValue()));

        ofNullable(workItem.getParameters().get(ACTOR_ID)).map(String.class::cast).map(this::toSet).ifPresent(instance::setPotentialUsers);
        ofNullable(workItem.getParameters().get(GROUP_ID)).map(String.class::cast).map(this::toSet).ifPresent(instance::setPotentialGroups);
        ofNullable(workItem.getParameters().get(BUSINESSADMINISTRATOR_ID)).map(String.class::cast).map(this::toSet).ifPresent(instance::setAdminUsers);
        ofNullable(workItem.getParameters().get(BUSINESSADMINISTRATOR_GROUP_ID)).map(String.class::cast).map(this::toSet).ifPresent(instance::setAdminGroups);
        ofNullable(workItem.getParameters().get(EXCLUDED_OWNER_ID)).map(String.class::cast).map(this::toSet).ifPresent(instance::setExcludedUsers);

        ofNullable(workItem.getParameters().get(NOT_STARTED_NOTIFY)).map(String.class::cast).map(DeadlineHelper::parseDeadlines).ifPresent(instance::setNotStartedDeadlines);
        ofNullable(workItem.getParameters().get(NOT_STARTED_REASSIGN)).map(String.class::cast).map(DeadlineHelper::parseReassignments).ifPresent(instance::setNotStartedReassignments);
        ofNullable(workItem.getParameters().get(NOT_COMPLETED_NOTIFY)).map(String.class::cast).map(DeadlineHelper::parseDeadlines).ifPresent(instance::setNotCompletedDeadlines);
        ofNullable(workItem.getParameters().get(NOT_COMPLETED_REASSIGN)).map(String.class::cast).map(DeadlineHelper::parseReassignments).ifPresent(instance::setNotCompletedReassignments);

        instance.initialize(emptyMap(), IdentityProviders.of(WORKFLOW_ENGINE_USER));

        if (workItem instanceof InternalKogitoWorkItem ikw) {
            ikw.setExternalReferenceId(instance.getId());
            ikw.setActualOwner(instance.getActualOwner());
        }

        return Optional.empty();
    }

    @Override
    public Optional<WorkItemTransition> completeWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
        if (transition.data().containsKey("Notify")) {
            return Optional.empty();
        }
        UserTasks userTasks = handler.getApplication().get(UserTasks.class);
        UserTask userTask = userTasks.userTaskById((String) workItem.getParameter(KogitoWorkItem.PARAMETER_UNIQUE_TASK_ID));
        userTask.instances().findById(workItem.getExternalReferenceId()).ifPresent(ut -> {
            if (workItem instanceof InternalKogitoWorkItem ikw) {
                ikw.setActualOwner(ut.getActualOwner());
            }
            ut.transition(DefaultUserTaskLifeCycle.SKIP, Collections.singletonMap(PARAMETER_NOTIFY, Boolean.FALSE), IdentityProviders.of(WORKFLOW_ENGINE_USER));
        });
        return Optional.empty();
    }

    @Override
    public Optional<WorkItemTransition> abortWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
        if (transition.data().containsKey("Notify")) {
            return Optional.empty();
        }
        UserTasks userTasks = handler.getApplication().get(UserTasks.class);
        UserTask userTask = userTasks.userTaskById((String) workItem.getParameter(KogitoWorkItem.PARAMETER_UNIQUE_TASK_ID));
        userTask.instances().findById(workItem.getExternalReferenceId()).ifPresent(ut -> {
            if (workItem instanceof InternalKogitoWorkItem ikw) {
                ikw.setActualOwner(ut.getActualOwner());
            }
            ut.transition(DefaultUserTaskLifeCycle.FAIL, Collections.singletonMap(PARAMETER_NOTIFY, Boolean.FALSE), IdentityProviders.of(WORKFLOW_ENGINE_USER));
        });
        return Optional.empty();
    }

    protected Set<String> toSet(String value) {
        if (value == null) {
            return null;
        }
        return Set.of(value.split(UT_SEPARATOR));
    }

    @Override
    public String getName() {
        return "Human Task";
    }
}
