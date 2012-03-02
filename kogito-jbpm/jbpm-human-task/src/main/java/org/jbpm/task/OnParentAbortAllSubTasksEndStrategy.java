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

package org.jbpm.task;

import java.util.List;

import javax.persistence.Entity;

import org.jbpm.task.event.InternalTaskEventListener;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.persistence.TaskServiceSession;

/**
 *
 */
@Entity
public class OnParentAbortAllSubTasksEndStrategy extends SubTasksStrategy {

    public OnParentAbortAllSubTasksEndStrategy(){}

    public OnParentAbortAllSubTasksEndStrategy(String name) {
        this.setName(name);
    }



    public void execute(TaskServiceSession taskServiceSession, TaskService service, Task task) {
        
        List<TaskSummary> subTasks = taskServiceSession.getSubTasksByParent(task.getId(), "en-UK");
            for(TaskSummary taskSummary : subTasks){
                Task subTask = taskServiceSession.getTask(taskSummary.getId());
                service.addEventListener(new InternalTaskEventListener(taskServiceSession));

                service.getEventSupport().fireTaskCompleted( subTask.getId(),
                                                                subTask.getTaskData().getActualOwner().getId() );
            }
    }

   

}
