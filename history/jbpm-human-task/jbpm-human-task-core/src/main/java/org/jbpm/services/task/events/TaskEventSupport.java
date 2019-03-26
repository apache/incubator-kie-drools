/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.events;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.core.event.AbstractEventSupport;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.TaskLifeCycleEventListener.AssignmentType;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskContext;

public class TaskEventSupport extends AbstractEventSupport<TaskLifeCycleEventListener> {

    public void fireBeforeTaskActivated(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskActivatedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskClaimed(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskClaimedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskSkipped(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskSkippedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskStarted(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskStartedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskStopped(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskStoppedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskCompleted(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskCompletedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskFailed(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskFailedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskAdded(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskAddedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskExited(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskExitedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskReleased(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskReleasedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskResumed(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskResumedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskSuspended(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskSuspendedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskForwarded(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskForwardedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskDelegated(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskDelegatedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskNominated(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().beforeTaskNominatedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskUpdated(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
            	TaskLifeCycleEventListener listener = iter.next();
        		listener.beforeTaskUpdatedEvent(event);
            	
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskReassigned(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                TaskLifeCycleEventListener listener = iter.next();                
                listener.beforeTaskReassignedEvent(event);                
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskNotified(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                TaskLifeCycleEventListener listener = iter.next();
                listener.beforeTaskNominatedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskInputVariablesChanged(final Task task, TaskContext context, Map<String, Object> variables) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                TaskLifeCycleEventListener listener = iter.next();
                listener.beforeTaskInputVariableChangedEvent(event, variables);                
            } while (iter.hasNext());
        }
    }
    
    
    public void fireBeforeTaskOutputVariablesChanged(final Task task, TaskContext context, Map<String, Object> variables) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                TaskLifeCycleEventListener listener = iter.next();
                listener.beforeTaskOutputVariableChangedEvent(event, variables);                
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskAssignmentsAddedEvent(final Task task, TaskContext context, AssignmentType type, List<OrganizationalEntity> entities) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                TaskLifeCycleEventListener listener = iter.next();
                listener.beforeTaskAssignmentsAddedEvent(event, type, entities);                
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeTaskAssignmentsRemovedEvent(final Task task, TaskContext context, AssignmentType type, List<OrganizationalEntity> entities) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                TaskLifeCycleEventListener listener = iter.next();
                listener.beforeTaskAssignmentsRemovedEvent(event, type, entities);                
            } while (iter.hasNext());
        }
    }

    
    // after methods
    
    public void fireAfterTaskActivated(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskActivatedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskClaimed(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskClaimedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskSkipped(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskSkippedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskStarted(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskStartedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskStopped(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskStoppedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskCompleted(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskCompletedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskFailed(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskFailedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskAdded(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskAddedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskExited(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskExitedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskReleased(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskReleasedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskResumed(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskResumedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskSuspended(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskSuspendedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskForwarded(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskForwardedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskDelegated(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskDelegatedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskNominated(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                iter.next().afterTaskNominatedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskUpdated(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
            	TaskLifeCycleEventListener listener = iter.next();
            	listener.afterTaskUpdatedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskReassigned(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                TaskLifeCycleEventListener listener = iter.next();
                listener.afterTaskReassignedEvent(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskNotified(final Task task, TaskContext context) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                TaskLifeCycleEventListener listener = iter.next();
                listener.afterTaskNotificationEvent(event);                
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskInputVariablesChanged(final Task task, TaskContext context, Map<String, Object> variables) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                TaskLifeCycleEventListener listener = iter.next();
                listener.afterTaskInputVariableChangedEvent(event, variables);                
            } while (iter.hasNext());
        }
    }
    
    
    public void fireAfterTaskOutputVariablesChanged(final Task task, TaskContext context, Map<String, Object> variables) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                TaskLifeCycleEventListener listener = iter.next();
                listener.afterTaskOutputVariableChangedEvent(event, variables);                
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskAssignmentsAddedEvent(final Task task, TaskContext context, AssignmentType type, List<OrganizationalEntity> entities) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                TaskLifeCycleEventListener listener = iter.next();
                listener.afterTaskAssignmentsAddedEvent(event, type, entities);                
            } while (iter.hasNext());
        }
    }
    
    public void fireAfterTaskAssignmentsRemovedEvent(final Task task, TaskContext context, AssignmentType type, List<OrganizationalEntity> entities) {
        final Iterator<TaskLifeCycleEventListener> iter = getEventListenersIterator();
        if (iter.hasNext()) {
            final TaskEventImpl event = new TaskEventImpl(task, context);
            
            do{
                TaskLifeCycleEventListener listener = iter.next();
                listener.afterTaskAssignmentsRemovedEvent(event, type, entities);                
            } while (iter.hasNext());
        }
    }
}
