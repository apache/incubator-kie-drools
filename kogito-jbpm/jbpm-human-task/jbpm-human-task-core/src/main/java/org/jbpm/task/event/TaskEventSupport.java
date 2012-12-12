/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.task.event;

import java.util.Iterator;

import org.drools.event.AbstractEventSupport;
import org.jbpm.task.event.entity.TaskEventFactory;
import org.jbpm.task.event.entity.TaskUserEvent;

/**
 */
public class TaskEventSupport extends AbstractEventSupport<TaskEventListener> {

    public void fireTaskClaimed(final long taskId, final String userId, final int sessionId) {
        final Iterator<TaskEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final TaskUserEvent event = TaskEventFactory.createClaimedEvent(taskId, userId, sessionId);

            do {
                iter.next().taskClaimed(event);
            } while (iter.hasNext());
        }
    }

    public void fireTaskCompleted(final long taskId, final String userId, final int sessionId) {
        final Iterator<TaskEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final TaskUserEvent event = TaskEventFactory.createCompletedEvent(taskId, userId, sessionId);

            do {
                iter.next().taskCompleted(event);
            } while (iter.hasNext());
        }
    }

    public void fireTaskFailed(final long taskId, final String userId, final int sessionId) {
        final Iterator<TaskEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final TaskUserEvent event = TaskEventFactory.createFailedEvent(taskId, userId, sessionId);

            do {
                iter.next().taskFailed(event);
            } while (iter.hasNext());
        }
    }

    public void fireTaskSkipped(final long taskId, final String userId, final int sessionId) {
        final Iterator<TaskEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final TaskUserEvent event = TaskEventFactory.createSkippedEvent(taskId, userId, sessionId);

            do {
                iter.next().taskSkipped(event);
            } while (iter.hasNext());
        }
    }

    public void fireTaskCreated(final long taskId, final String userId, final int sessionId) {
        final Iterator<TaskEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final TaskUserEvent event = TaskEventFactory.createCreatedEvent(taskId, userId, sessionId);

            do {
                iter.next().taskCreated(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireTaskForwarded(final long taskId, final String userId, final int sessionId) {
        final Iterator<TaskEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final TaskUserEvent event = TaskEventFactory.createForwardedEvent(taskId, userId, sessionId);

            do {
                iter.next().taskForwarded(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireTaskStarted(final long taskId, final String userId, final int sessionId) {
        final Iterator<TaskEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final TaskUserEvent event = TaskEventFactory.createStartedEvent(taskId, userId, sessionId);

            do {
                iter.next().taskStarted(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireTaskReleased(final long taskId, final String userId, final int sessionId) {
        final Iterator<TaskEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final TaskUserEvent event = TaskEventFactory.createReleasedEvent(taskId, userId, sessionId);

            do {
                iter.next().taskReleased(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireTaskStopped(final long taskId, final String userId, final int sessionId) {
        final Iterator<TaskEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final TaskUserEvent event = TaskEventFactory.createStoppedEvent(taskId, userId, sessionId);

            do {
                iter.next().taskStopped(event);
            } while (iter.hasNext());
        }
    }
    
    public void reset() {
        this.clear();
    }
}
