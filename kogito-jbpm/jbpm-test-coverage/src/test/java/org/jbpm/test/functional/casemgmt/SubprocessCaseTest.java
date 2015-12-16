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
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.domain.Person;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

public class SubprocessCaseTest extends JbpmTestCase {

    private static final String HUMAN_TASK = "org/jbpm/test/functional/common/HumanTaskWithMultipleActors.bpmn2";
    private static final String HUMAN_TASK_ID = "org.jbpm.test.functional.common.HumanTaskWithMultipleActors";

    private KieSession kieSession;
    private RuntimeEngine runtimeEngine;
    private CaseMgmtService caseMgmtService;
    private TaskService taskService;
    private ProcessInstance casePi;
    
    @Before
    public void setup() {
        
        addProcessEventListener(new DefaultProcessEventListener() {
            @Override
            public void afterProcessCompleted(ProcessCompletedEvent event) {
                ProcessInstance pi = event.getProcessInstance();
                if (pi.getProcessId().equals(HUMAN_TASK_ID)) {
                    List<? extends VariableInstanceLog> vars = getLogService().findVariableInstances(pi.getId());
                    for (VariableInstanceLog v : vars) {
                        caseMgmtService.setCaseData(casePi.getId(), HUMAN_TASK_ID + "." + v.getVariableId(), v.getValue());
                    }
                }
            }
        });
        
        kieSession = createKSession(EMPTY_CASE, HUMAN_TASK);
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
        params.put("description", "subprocess");

        caseMgmtService.createDynamicProcess(pid, HUMAN_TASK_ID, params);
        
        ProcessInstance[] activeSubProcesses = caseMgmtService.getActiveSubProcesses(pid);
        Assertions.assertThat(activeSubProcesses).hasSize(1);

        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        Assertions.assertThat(list).hasSize(1);
        TaskSummary task = list.get(0);
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);
        
        activeSubProcesses = caseMgmtService.getActiveSubProcesses(pid);
        Assertions.assertThat(activeSubProcesses).hasSize(0);
        
        Map<String, Object> caseData = caseMgmtService.getCaseData(pid);
        System.out.println(" ### CASE DATA ### ");
        for (Entry<String, Object> e : caseData.entrySet()) {
            System.out.println(e.getKey() + ":" + e.getValue());
        }
        Assertions.assertThat(caseData).containsEntry(HUMAN_TASK_ID + ".description", "subprocess");
        Assertions.assertThat(caseData).containsEntry("person", john);
    }

}
