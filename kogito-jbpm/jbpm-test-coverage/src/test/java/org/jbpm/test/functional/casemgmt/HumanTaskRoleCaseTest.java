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

import java.util.List;
import java.util.Random;

import org.assertj.core.api.Assertions;
import org.jbpm.casemgmt.CaseMgmtService;
import org.jbpm.casemgmt.CaseMgmtUtil;
import org.jbpm.test.JbpmTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

public class HumanTaskRoleCaseTest extends JbpmTestCase {

    private KieSession kieSession;
    private RuntimeEngine runtimeEngine;
    private CaseMgmtService caseMgmtService;
    private TaskService taskService;
    private ProcessInstance casePi;

    @Before
    public void setup() {
        kieSession = createKSession("org/jbpm/test/functional/casemgmt/HumanTaskRoleCase.bpmn2");
        runtimeEngine = getRuntimeEngine();
        caseMgmtService = new CaseMgmtUtil(runtimeEngine);
        taskService = runtimeEngine.getTaskService();
    }

    @Test(timeout = 30000)
    public void testCustomRole() {

        casePi = caseMgmtService.startNewCase("caseDataHT");
        long pid = casePi.getId();

        caseMgmtService.addUserToRole(pid, "customRole", "admin");
        caseMgmtService.addUserToRole(pid, "customRole", "john");
        
        String selectedUser = getRandomUserInTheRole(pid, "customRole");
        
        caseMgmtService.setCaseData(pid, "optUserId", selectedUser);
        
        
        caseMgmtService.triggerAdHocFragment(pid, "Optional Human Task");

        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(selectedUser, "en-UK");
        Assertions.assertThat(tasks).hasSize(1);

        TaskSummary task = tasks.get(0);
        taskService.start(task.getId(), selectedUser);
        taskService.complete(task.getId(), selectedUser, null);

    }

    @Test(timeout = 30000)
    public void testCustomRoleOnDynamicTask() {

        casePi = caseMgmtService.startNewCase("caseDataHT");
        long pid = casePi.getId();

        caseMgmtService.addUserToRole(pid, "customRole", "john");
        caseMgmtService.addUserToRole(pid, "customRole", "mary");
        
        String selectedUser = getRandomUserInTheRole(pid, "customRole");

        caseMgmtService.createDynamicHumanTask(pid, "Custom Role Task", selectedUser, null, null, null);

        List<Long> tasks = taskService.getTasksByProcessInstanceId(pid);
        Assertions.assertThat(tasks).hasSize(1);

        Task task = taskService.getTaskById(tasks.get(0));
        taskService.start(task.getId(), selectedUser);
        taskService.complete(task.getId(), selectedUser, null);

    }

    private String getRandomUserInTheRole(long pid, String role) {
        String[] users = caseMgmtService.getCaseRoleInstanceNames(pid).get(role);
        Random rand = new Random();
        int n = rand.nextInt(users.length-1);
        return users[n];
    }

}
