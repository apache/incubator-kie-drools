/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.workflow.instance.node;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.swimlane.SwimlaneContext;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.swimlane.SwimlaneContextInstance;
import org.jbpm.process.instance.impl.humantask.DeadlineHelper;
import org.jbpm.process.instance.impl.humantask.DeadlineInfo;
import org.jbpm.process.instance.impl.humantask.HumanTaskHelper;
import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemImpl;
import org.jbpm.process.instance.impl.humantask.Reassignment;
import org.jbpm.process.instance.impl.humantask.ScheduleInfo;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.kogito.internal.process.event.KogitoProcessEventSupport;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.TimerJobId;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;
import org.kie.kogito.process.workitems.InternalKogitoWorkItem;
import org.kie.kogito.timer.TimerInstance;

public class HumanTaskNodeInstance extends WorkItemNodeInstance {

    private static final long serialVersionUID = 510l;
    private static final String NODE_NAME = "NodeName";
    private static final String DESCRIPTION = "Description";
    private static final String PRIORITY = "Priority";
    private static final String TASK_NAME = "TaskName";
    private String separator = System.getProperty("org.jbpm.ht.user.separator", ",");

    private static final String ACTOR_ID = "ActorId";
    private static final String GROUP_ID = "GroupId";
    private static final String BUSINESSADMINISTRATOR_ID = "BusinessAdministratorId";
    private static final String BUSINESSADMINISTRATOR_GROUP_ID = "BusinessAdministratorGroupId";
    private static final String EXCLUDED_OWNER_ID = "ExcludedOwnerId";
    private static final String TIMER_TRIGGERED = "timerTriggered";
    private static final String WORK_ITEM_TRANSITION = "workItemTransition";

    private transient SwimlaneContextInstance swimlaneContextInstance;

    private Map<String, Map<String, Object>> notStartedDeadlines = new ConcurrentHashMap<>();
    private Map<String, Map<String, Object>> notCompletedDeadlines = new ConcurrentHashMap<>();
    private Map<String, Reassignment> notStartedReassignments = new ConcurrentHashMap<>();
    private Map<String, Reassignment> notCompletedReassignments = new ConcurrentHashMap<>();

    public HumanTaskNode getHumanTaskNode() {
        return (HumanTaskNode) getNode();
    }

    @Override
    protected InternalKogitoWorkItem newWorkItem() {
        return new HumanTaskWorkItemImpl();
    }

    /*
     * Since job service just communicate the timer id to listeners, we need to keep
     * a map between the timer id and the notification information for not started task deadlines
     * (which consist of a list of key value pairs)
     */
    public Map<String, Map<String, Object>> getNotStartedDeadlineTimers() {
        return notStartedDeadlines;
    }

    /*
     * Since job service just communicate the timer id to listeners, we need to keep
     * a map between the timer id and the notification information for not completed task deadlines
     * (which consist of a list of key value pairs)
     */
    public Map<String, Map<String, Object>> getNotCompletedDeadlineTimers() {
        return notCompletedDeadlines;
    }

    public Map<String, Reassignment> getNotStartedReassignments() {
        return notStartedReassignments;
    }

    public Map<String, Reassignment> getNotCompletedReassigments() {
        return notCompletedReassignments;
    }

    @Override
    protected InternalKogitoWorkItem createWorkItem(WorkItemNode workItemNode) {
        HumanTaskWorkItemImpl workItem = (HumanTaskWorkItemImpl) super.createWorkItem(workItemNode);
        String actorId = assignWorkItem(workItem);
        if (actorId != null) {
            workItem.setParameter(ACTOR_ID, actorId);
        }

        workItem.setTaskName((String) workItem.getParameter(TASK_NAME));
        workItem.setTaskDescription((String) workItem.getParameter(DESCRIPTION));
        workItem.setTaskPriority((String) workItem.getParameter(PRIORITY));
        workItem.setReferenceName((String) workItem.getParameter(NODE_NAME));
        Work work = workItemNode.getWork();
        scheduleDeadlines(work.getNotStartedDeadlines(), notStartedDeadlines);
        scheduleDeadlines(work.getNotCompletedDeadlines(), notCompletedDeadlines);
        scheduleDeadlines(work.getNotStartedReassignments(), notStartedReassignments);
        scheduleDeadlines(work.getNotCompletedReassigments(), notCompletedReassignments);
        return workItem;
    }

    private <T> void scheduleDeadlines(Collection<DeadlineInfo<T>> deadlines,
            Map<String, T> timers) {
        if (!deadlines.isEmpty()) {
            ProcessInstance pi = getProcessInstance();
            for (DeadlineInfo<T> deadline : deadlines) {
                for (ScheduleInfo info : deadline.getScheduleInfo()) {
                    timers.put(getJobsService().scheduleProcessInstanceJob(ProcessInstanceJobDescription.of(
                            new TimerJobId(-1L),
                            DeadlineHelper.getExpirationTime(info),
                            pi.getStringId(),
                            pi.getRootProcessInstanceId(),
                            pi.getProcessId(),
                            pi.getRootProcessId(),
                            getStringId())), deadline.getNotification());
                }
            }
        }
    }

    @Override
    public void signalEvent(String type, Object event) {
        switch (type) {
            case WORK_ITEM_TRANSITION:
                cancelTimers(notStartedDeadlines);
                cancelTimers(notStartedReassignments);
                break;
            case TIMER_TRIGGERED:
                if (!sendNotification((TimerInstance) event)) {
                    super.signalEvent(type, event);
                }
                break;
            default:
                super.signalEvent(type, event);
        }
    }

    private boolean sendNotification(TimerInstance timerInstance) {
        boolean processed = checkAndSendNotitication(notStartedDeadlines, timerInstance, this::startNotification);
        if (!processed) {
            processed = checkAndSendNotitication(notCompletedDeadlines, timerInstance, this::endNotification);
        }
        if (!processed) {
            processed = checkAndReassign(notStartedReassignments, timerInstance);
        }
        if (!processed) {
            processed = checkAndReassign(notCompletedReassignments, timerInstance);
        }
        return processed;
    }

    private boolean checkAndSendNotitication(Map<String, Map<String, Object>> timers,
            TimerInstance timerInstance,
            Consumer<Map<String, Object>> publisher) {
        Map<String, Object> notification = timers.get(timerInstance.getId());
        boolean result = notification != null;
        if (result) {
            if (timerInstance.getRepeatLimit() == 0) {
                timers.remove(timerInstance.getId());
            }
            publisher.accept(notification);
        }
        return result;
    }

    private boolean checkAndReassign(Map<String, Reassignment> timers,
            TimerInstance timerInstance) {
        Reassignment reassignment = timers.remove(timerInstance.getId());
        boolean result = reassignment != null;
        if (result) {
            reassign(reassignment);
        }
        return result;
    }

    @Override
    protected void addWorkItemListener() {
        super.addWorkItemListener();
        getProcessInstance().addEventListener(TIMER_TRIGGERED, this, false);
        getProcessInstance().addEventListener(WORK_ITEM_TRANSITION, this, false);
    }

    @Override
    protected void removeWorkItemListener() {
        super.removeWorkItemListener();
        getProcessInstance().removeEventListener(TIMER_TRIGGERED, this, false);
        getProcessInstance().removeEventListener(WORK_ITEM_TRANSITION, this, false);
    }

    private KogitoProcessEventSupport getEventSupport() {
        return KogitoProcessRuntime.asKogitoProcessRuntime(getProcessInstance().getKnowledgeRuntime()
                .getProcessRuntime()).getProcessEventSupport();
    }

    private JobsService getJobsService() {
        return KogitoProcessRuntime.asKogitoProcessRuntime(getProcessInstance().getKnowledgeRuntime()
                .getProcessRuntime()).getJobsService();
    }

    private void startNotification(Map<String, Object> notification) {
        getEventSupport().fireOnTaskNotStartedDeadline(getProcessInstance(), (HumanTaskWorkItem) getWorkItem(),
                notification, getProcessInstance().getKnowledgeRuntime());
    }

    private void endNotification(Map<String, Object> notification) {
        getEventSupport().fireOnTaskNotCompletedDeadline(getProcessInstance(), (HumanTaskWorkItem) getWorkItem(),
                notification,
                getProcessInstance().getKnowledgeRuntime());
    }

    private void reassign(Reassignment reassignment) {
        HumanTaskWorkItemImpl humanTask = HumanTaskHelper.asHumanTask(getWorkItem());
        boolean modified = false;
        if (!reassignment.getPotentialUsers().isEmpty()) {
            humanTask.setPotentialUsers(reassignment.getPotentialUsers());
            modified = true;
        }
        if (!reassignment.getPotentialGroups().isEmpty()) {
            humanTask.setPotentialGroups(reassignment.getPotentialGroups());
            modified = true;
        }
        if (modified) {
            getEventSupport().fireAfterWorkItemTransition(getProcessInstance(), humanTask, null, null);
        }
    }

    protected String assignWorkItem(InternalKogitoWorkItem workItem) {
        String actorId = null;
        // if this human task node is part of a swimlane, check whether an actor
        // has already been assigned to this swimlane
        String swimlaneName = getHumanTaskNode().getSwimlane();
        SwimlaneContextInstance swimlaneContextInstance = getSwimlaneContextInstance(swimlaneName);
        if (swimlaneContextInstance != null) {
            actorId = swimlaneContextInstance.getActorId(swimlaneName);
            workItem.setParameter("SwimlaneActorId", actorId);
        }
        // if no actor can be assigned based on the swimlane, check whether an
        // actor is specified for this human task
        if (actorId == null) {
            actorId = (String) workItem.getParameter(ACTOR_ID);
            if (actorId != null && swimlaneContextInstance != null && actorId.split(separator).length == 1) {
                swimlaneContextInstance.setActorId(swimlaneName, actorId);
                workItem.setParameter("SwimlaneActorId", actorId);
            }
        }

        processAssigment(ACTOR_ID, workItem, ((HumanTaskWorkItemImpl) workItem).getPotentialUsers());
        processAssigment(GROUP_ID, workItem, ((HumanTaskWorkItemImpl) workItem).getPotentialGroups());
        processAssigment(EXCLUDED_OWNER_ID, workItem, ((HumanTaskWorkItemImpl) workItem).getExcludedUsers());
        processAssigment(BUSINESSADMINISTRATOR_ID, workItem, ((HumanTaskWorkItemImpl) workItem).getAdminUsers());
        processAssigment(BUSINESSADMINISTRATOR_GROUP_ID, workItem, ((HumanTaskWorkItemImpl) workItem).getAdminGroups());

        // always return ActorId from workitem as SwimlaneActorId is kept as separate parameter
        return (String) workItem.getParameter(ACTOR_ID);
    }

    private SwimlaneContextInstance getSwimlaneContextInstance(String swimlaneName) {
        if (this.swimlaneContextInstance == null) {
            if (swimlaneName == null) {
                return null;
            }
            SwimlaneContextInstance swimlaneContextInstance =
                    (SwimlaneContextInstance) resolveContextInstance(
                            SwimlaneContext.SWIMLANE_SCOPE, swimlaneName);
            if (swimlaneContextInstance == null) {
                throw new IllegalArgumentException(
                        "Could not find swimlane context instance");
            }
            this.swimlaneContextInstance = swimlaneContextInstance;
        }
        return this.swimlaneContextInstance;
    }

    @Override
    public void triggerCompleted(InternalKogitoWorkItem workItem) {
        cancelTimers(notStartedDeadlines);
        cancelTimers(notCompletedDeadlines);
        cancelTimers(notStartedReassignments);
        cancelTimers(notCompletedReassignments);
        String swimlaneName = getHumanTaskNode().getSwimlane();
        SwimlaneContextInstance swimlaneContextInstance = getSwimlaneContextInstance(swimlaneName);
        if (swimlaneContextInstance != null) {
            String newActorId = (workItem instanceof HumanTaskWorkItem) ? ((HumanTaskWorkItem) workItem).getActualOwner() : (String) workItem.getParameter(ACTOR_ID);
            if (newActorId != null) {
                swimlaneContextInstance.setActorId(swimlaneName, newActorId);
            }
        }
        super.triggerCompleted(workItem);
    }

    private <T> void cancelTimers(Map<String, T> timers) {
        Iterator<String> iter = timers.keySet().iterator();
        while (iter.hasNext()) {
            getJobsService().cancelJob(iter.next());
            iter.remove();
        }
    }

    protected void processAssigment(String type, InternalKogitoWorkItem workItem, Set<String> store) {
        String value = (String) workItem.getParameter(type);

        if (value != null) {
            for (String item : value.split(separator)) {
                store.add(item);
            }
        }
    }
}
