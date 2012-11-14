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


import org.jbpm.task.Status;
import org.jbpm.task.event.entity.TaskClaimedEvent;
import org.jbpm.task.event.entity.TaskCompletedEvent;
import org.jbpm.task.event.entity.TaskFailedEvent;
import org.jbpm.task.event.entity.TaskSkippedEvent;
import org.jbpm.task.event.entity.TaskUserEvent;
import org.jbpm.task.service.TaskServiceSession;

public class InternalTaskEventListener extends DefaultTaskEventListener{
    
    private TaskServiceSession session;

    public InternalTaskEventListener(TaskServiceSession session){
        this.session = session;
    }

    @Override
    public void taskClaimed(TaskUserEvent event) {
    }

    @Override
    public void taskCompleted(TaskUserEvent event) {
        session.setTaskStatus(event.getTaskId(), Status.Completed);
    }

    @Override
	public void taskFailed(TaskUserEvent event) {
	}

    @Override
	public void taskSkipped(TaskUserEvent event) {
	}
}
