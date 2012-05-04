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

import org.drools.event.AbstractEventSupport;

import java.util.Iterator;

/**
 */
public class TaskEventSupport extends AbstractEventSupport<TaskEventListener> {

    public void fireTaskClaimed(final long taskId, final String userId) {
        final Iterator<TaskEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final TaskClaimedEvent event = new TaskClaimedEvent(taskId, userId);

            do {
                iter.next().taskClaimed(event);
            } while (iter.hasNext());
        }
    }

    public void fireTaskCompleted(final long taskId, final String userId) {
        final Iterator<TaskEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final TaskCompletedEvent event = new TaskCompletedEvent(taskId, userId);

            do {
                iter.next().taskCompleted(event);
            } while (iter.hasNext());
        }
    }

    public void fireTaskFailed(final long taskId, final String userId) {
        final Iterator<TaskEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final TaskFailedEvent event = new TaskFailedEvent(taskId, userId);

            do {
                iter.next().taskFailed(event);
            } while (iter.hasNext());
        }
    }

    public void fireTaskSkipped(final long taskId, final String userId) {
        final Iterator<TaskEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final TaskSkippedEvent event = new TaskSkippedEvent(taskId, userId);

            do {
                iter.next().taskSkipped(event);
            } while (iter.hasNext());
        }
    }

    public void reset() {
        this.clear();
    }
}
