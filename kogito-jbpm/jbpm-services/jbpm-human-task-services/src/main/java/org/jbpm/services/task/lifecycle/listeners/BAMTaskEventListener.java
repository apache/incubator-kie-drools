/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.services.task.lifecycle.listeners;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.events.AfterTaskActivatedEvent;
import org.jbpm.services.task.events.AfterTaskAddedEvent;
import org.jbpm.services.task.events.AfterTaskClaimedEvent;
import org.jbpm.services.task.events.AfterTaskCompletedEvent;
import org.jbpm.services.task.events.AfterTaskExitedEvent;
import org.jbpm.services.task.events.AfterTaskFailedEvent;
import org.jbpm.services.task.events.AfterTaskStartedEvent;
import org.jbpm.services.task.events.AfterTaskStoppedEvent;
import org.jbpm.services.task.impl.model.BAMTaskSummaryImpl;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.kie.internal.task.api.model.Task;

@ApplicationScoped
@Transactional
public class BAMTaskEventListener implements TaskLifeCycleEventListener {

    @Inject
    private JbpmServicesPersistenceManager pm;

    public BAMTaskEventListener() {
    }

    public void afterTaskStartedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskStartedEvent Task ti) {
        List<BAMTaskSummaryImpl> taskSummaries = (List<BAMTaskSummaryImpl>) pm.queryStringWithParametersInTransaction("select bts from BAMTaskSummaryImpl bts where bts.taskId=:taskId", 
                pm.addParametersToMap("taskId", ti.getId()));
        if (taskSummaries.isEmpty()) {
            String actualOwner = "";
            if (ti.getTaskData().getActualOwner() != null) {
                actualOwner = ti.getTaskData().getActualOwner().getId();
            }
            BAMTaskSummaryImpl bamTaskSummary = new BAMTaskSummaryImpl(ti.getId(), ti.getNames().get(0).getText(), "Started", new Date(), actualOwner, ti.getTaskData().getProcessInstanceId());
            bamTaskSummary.setStartDate(new Date());
            pm.persist(bamTaskSummary);
            
        } else if (taskSummaries.size() == 1) {
            
            BAMTaskSummaryImpl taskSummaryById = taskSummaries.get(0);
            taskSummaryById.setStatus("Started");
            taskSummaryById.setStartDate(new Date());
            if (ti.getTaskData().getActualOwner() != null) {
                taskSummaryById.setUserId(ti.getTaskData().getActualOwner().getId());
            }
            pm.merge(taskSummaryById);

        } else {
            throw new IllegalStateException("We cannot have more than one BAM Task Summary for the task id = " + ti.getId());
        }

    }

    public void afterTaskActivatedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskActivatedEvent Task ti) {
    }

    public void afterTaskClaimedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskClaimedEvent Task ti) {
        
        List<BAMTaskSummaryImpl> taskSummaries = (List<BAMTaskSummaryImpl>) pm.queryStringWithParametersInTransaction("select bts from BAMTaskSummaryImpl bts where bts.taskId=:taskId",
                pm.addParametersToMap("taskId", ti.getId()));
        if (taskSummaries.isEmpty()) {
            
            String actualOwner = "";
            if (ti.getTaskData().getActualOwner() != null) {
                actualOwner = ti.getTaskData().getActualOwner().getId();
            }

            pm.persist(new BAMTaskSummaryImpl(ti.getId(), ti.getNames().get(0).getText(), "Claimed", new Date(), actualOwner, ti.getTaskData().getProcessInstanceId()));
        } else if (taskSummaries.size() == 1) {
            
            BAMTaskSummaryImpl taskSummaryById = taskSummaries.get(0);
            taskSummaryById.setStatus("Claimed");
            if (ti.getTaskData().getActualOwner() != null) {
                taskSummaryById.setUserId(ti.getTaskData().getActualOwner().getId());
            }
            pm.merge(taskSummaryById);

        } else {
            throw new IllegalStateException("We cannot have more than one BAM Task Summary for the task id = " + ti.getId());
        }

    }

    public void afterTaskSkippedEvent(Task ti) {
    }

    public void afterTaskStoppedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskStoppedEvent Task ti) {
    }

    public void afterTaskCompletedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskCompletedEvent Task ti) {

        List<BAMTaskSummaryImpl> summaries = (List<BAMTaskSummaryImpl>) pm.queryStringWithParametersInTransaction("select bts from BAMTaskSummaryImpl bts where bts.taskId=:taskId",
                pm.addParametersToMap("taskId", ti.getId()));
        
        if(summaries.size() == 1){
        
          BAMTaskSummaryImpl taskSummaryById = (BAMTaskSummaryImpl)summaries.get(0);

          taskSummaryById.setStatus("Completed");
          Date completedDate = new Date();
          taskSummaryById.setEndDate(completedDate);
          taskSummaryById.setDuration(completedDate.getTime() - taskSummaryById.getStartDate().getTime());
          pm.merge(taskSummaryById);
        }else{
          // Log
          System.out.print("EEEE: Something went wrong with the Task BAM Listener");
        }
    }

    public void afterTaskFailedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskFailedEvent Task ti) {
    }

    public void afterTaskAddedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskAddedEvent Task ti) {
    }

    public void afterTaskExitedEvent(@Observes(notifyObserver = Reception.ALWAYS) @AfterTaskExitedEvent Task ti) {
    }
}
