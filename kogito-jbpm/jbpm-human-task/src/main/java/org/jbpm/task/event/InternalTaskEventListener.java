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


import javax.persistence.EntityManager;

import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.service.Operation;
import org.jbpm.task.service.TaskServiceSession;

/**
 *
 * @author salaboy
 */
public class InternalTaskEventListener extends DefaultTaskEventListener{
    private TaskServiceSession session;
    private EntityManager em;
    public InternalTaskEventListener(TaskServiceSession session){
        this.session = session;
        this.em = session.getEntityManager();
    }

    @Override
    public void taskClaimed(TaskClaimedEvent event) {
    }

    @Override
    public void taskCompleted(TaskCompletedEvent event) {

        if(! em.getTransaction().isActive()){
            em.getTransaction().begin();
        }
        Task task = session.getTask(event.getTaskId());
        task.getTaskData().setStatus(Status.Completed);
        em.persist(task);
        em.getTransaction().commit();
        
    }

    @Override
	public void taskFailed(TaskFailedEvent event) {
	}

    @Override
	public void taskSkipped(TaskSkippedEvent event) {
	}
}
