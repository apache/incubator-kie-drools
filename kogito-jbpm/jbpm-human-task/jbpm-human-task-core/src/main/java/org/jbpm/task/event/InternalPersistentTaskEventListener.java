/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.task.event;

import org.jbpm.task.event.entity.TaskUserEvent;

public class InternalPersistentTaskEventListener implements TaskEventListener {

    private TaskEventsAdmin eventsAdmin;

    public InternalPersistentTaskEventListener(TaskEventsAdmin eventsAdmin) {
        this.eventsAdmin = eventsAdmin;
    }

    public void taskClaimed(TaskUserEvent event) {
        eventsAdmin.storeEvent(event);
    }

    public void taskCompleted(TaskUserEvent event) {
        eventsAdmin.storeEvent(event);
    }

    public void taskFailed(TaskUserEvent event) {
        eventsAdmin.storeEvent(event);
    }

    public void taskSkipped(TaskUserEvent event) {
        eventsAdmin.storeEvent(event);
    }

    public void taskCreated(TaskUserEvent event) {
        eventsAdmin.storeEvent(event);
    }

    public void taskStarted(TaskUserEvent event) {
        eventsAdmin.storeEvent(event);
    }

    public void taskStopped(TaskUserEvent event) {
        eventsAdmin.storeEvent(event);
    }

    public void taskReleased(TaskUserEvent event) {
        eventsAdmin.storeEvent(event);
    }

    public void taskForwarded(TaskUserEvent event) {
        eventsAdmin.storeEvent(event);
    }
}
