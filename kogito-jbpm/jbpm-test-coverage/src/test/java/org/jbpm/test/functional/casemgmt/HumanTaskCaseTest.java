/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.test.functional.casemgmt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.assertj.core.api.Assertions;
import org.jbpm.casemgmt.CaseMgmtService;
import org.jbpm.casemgmt.CaseMgmtUtil;
import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.jbpm.services.task.impl.TaskContentRegistry;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.domain.Person;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

public class HumanTaskCaseTest extends JbpmTestCase {
    
    private KieSession kieSession;
    private RuntimeEngine runtimeEngine;
    private CaseMgmtService caseMgmtService;
    private TaskService taskService;
    private ProcessInstance casePi;
    
    @Before
    public void setup() {
        
        addTaskEventListener(new DefaultTaskEventListener() {
            @Override
            public void afterTaskCompletedEvent(TaskEvent event) {
                Task task = event.getTask();
                if (task.getName().equals("Change age and return message")) {
                    Content output = taskService.getContentById(task.getTaskData().getOutputContentId());
                    Map<String, Object> results = (Map<String, Object>) ContentMarshallerHelper.unmarshall(output.getContent(), TaskContentRegistry.get().getMarshallerContext(task).getEnvironment());
                    
                    for (Entry<String, Object> e : results.entrySet()) {
                        caseMgmtService.setCaseData(casePi.getId(), e.getKey(), e.getValue());
                    }
                }
            }
        });
        
        kieSession = createKSession(EMPTY_CASE);
        runtimeEngine = getRuntimeEngine();
        caseMgmtService = new CaseMgmtUtil(runtimeEngine);
        taskService = runtimeEngine.getTaskService();
    }

    @Test(timeout = 30000)
    public void testCaseData() {
        
        casePi = caseMgmtService.startNewCase("caseDataHT");
        long pid = casePi.getId();

        Person john = new Person("John", 30);
        caseMgmtService.setCaseData(pid, "person", john);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person", john);

        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assertions.assertThat(tasks).hasSize(0);

        caseMgmtService.createDynamicHumanTask(pid, "Change age and return message", "john", null, null, params);

        tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assertions.assertThat(tasks).hasSize(1);

        TaskSummary task = tasks.get(0);
        Assertions.assertThat(task.getProcessInstanceId()).isEqualTo(pid);

        Task[] activeTasks = caseMgmtService.getActiveTasks(pid);
        Assertions.assertThat(activeTasks).hasSize(1);
        
        params = taskService.getTaskContent(task.getId());
        Person p = (Person) params.get("person");
        p.setAge(35);
        Assertions.assertThat(p).isEqualTo(john);
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("person", p);
        result.put("message", "age changed to 35");

        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", result);

        activeTasks = caseMgmtService.getActiveTasks(pid);
        Assertions.assertThat(activeTasks).hasSize(0);

        System.out.println(" ### CASE DATA ### ");
        Map<String, Object> caseData = caseMgmtService.getCaseData(pid);
        for (Entry<String, Object> e : caseData.entrySet()) {
            System.out.println(e.getKey() + ":" + e.getValue());
        }
        Assertions.assertThat(caseData).containsEntry("message", "age changed to 35");
        Assertions.assertThat(caseData).containsEntry("person", p);

    }
    
    @Test(timeout = 30000)
    public void testAbort() {
        ProcessInstance pi = caseMgmtService.startNewCase("caseAbort");
        long pid = pi.getId();
        
        Assertions.assertThat(pi.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        kieSession.abortProcessInstance(pid);
        ProcessInstanceLog pil = getLogService().findProcessInstance(pid);
        Assertions.assertThat(pil.getStatus()).isEqualTo(ProcessInstance.STATE_ABORTED);
    }

}
