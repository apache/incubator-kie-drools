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

import org.assertj.core.api.Assertions;
import org.jbpm.casemgmt.CaseMgmtService;
import org.jbpm.casemgmt.CaseMgmtUtil;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.casemgmt.WorkCaseService;
import org.jbpm.test.casemgmt.WorkCaseService.WorkCaseMilestones;
import org.jbpm.test.wih.InformWorkItemHandler;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

public class WorkCaseTest extends JbpmTestCase {

    private KieSession kieSession;
    private RuntimeEngine runtimeEngine;
    private CaseMgmtService caseMgmtService;
    private TaskService taskService;
    private AuditService auditService;

    private InformWorkItemHandler iwih;
    private WorkCaseService wcs;

    @Before
    public void setup() {

        addWorkItemHandler("Milestone", new SystemOutWorkItemHandler());
        iwih = new InformWorkItemHandler();
        addWorkItemHandler("Inform", iwih);

        kieSession = createKSession("org/jbpm/test/functional/casemgmt/WorkCase.bpmn2");
        runtimeEngine = getRuntimeEngine();
        caseMgmtService = new CaseMgmtUtil(runtimeEngine);
        iwih.setCaseMgmtService(caseMgmtService);
        taskService = runtimeEngine.getTaskService();
        auditService = getLogService();
        wcs = new WorkCaseService(runtimeEngine);
    }

    @Test(timeout = 30000)
    public void testInformingOfInterestedPeople() {

        String responsible = "john";
        long caseId = wcs.createWork("Integrate module X into project Y", "Module X working in the project Y by ...", responsible);
        wcs.addInformedPerson(caseId, "mary");

        wcs.informAbout(caseId, "my message");
        Assertions.assertThat(iwih.getLastMessage()).isEqualTo("my message");
        Assertions.assertThat(iwih.getLastMessageToWhom()).contains("mary");

        wcs.milestone(caseId, WorkCaseMilestones.CLOSE);

    }

    @Test(timeout = 30000)
    public void testProlongDueDate() {

        String responsible = "john";
        long caseId = wcs.createWork("Integrate module X into project Y", "Module X working in the project Y by ...", responsible, 1);
        wcs.prolongTimeoutTo(caseId, 3);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ProcessInstanceLog pil = auditService.findProcessInstance(caseId);
        Assertions.assertThat(pil.getStatus()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pil = auditService.findProcessInstance(caseId);
        Assertions.assertThat(pil.getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);

    }

    @Test(timeout = 30000)
    public void testConsultations() {

        String responsible = "john";
        long caseId = wcs.createWork("Integrate module X into project Y", "Module X working in the project Y by ...", responsible, 20);
        String consultant = "mary";
        wcs.addConsultant(caseId, consultant);

        wcs.consultWith(caseId, consultant);

        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(responsible, "en-UK");
        Assertions.assertThat(tasks).hasSize(1);

        TaskSummary task = tasks.get(0);
        Assertions.assertThat(taskService.getTaskById(task.getId()).getName()).isEqualTo("Consult work with mary");

        taskService.start(task.getId(), responsible);
        taskService.complete(task.getId(), responsible, null);
        
        String secondConsultant = "admin";
        wcs.addConsultant(caseId, secondConsultant);

        wcs.consultWith(caseId, secondConsultant);

        tasks = taskService.getTasksAssignedAsPotentialOwner(responsible, "en-UK");
        Assertions.assertThat(tasks).hasSize(1);

        task = tasks.get(0);
        Assertions.assertThat(taskService.getTaskById(task.getId()).getName()).isEqualTo("Consult work with admin");

        taskService.start(task.getId(), responsible);
        taskService.complete(task.getId(), responsible, null);

        wcs.milestone(caseId, WorkCaseMilestones.CLOSE);

        String[] passedNodes = getPassedNodes(caseId);
        Assertions.assertThat(passedNodes).containsSequence("Consult work with mary", "Consult work with admin", "Milestone: Close");

    }

    @Test(timeout = 30000)
    public void testCheckResolution() {

        String responsiblePerson = "john";
        long caseId = wcs.createWork("Integrate module X into project Y", "Module X working in the project Y by ...", responsiblePerson);

        createAndCompleteTask(caseId, "Do it", responsiblePerson, null, responsiblePerson);

        String approver = "mary";
        wcs.addApprover(caseId, approver);

        String selectedApprover = wcs.getRandomUserInTheRole(caseId, "accountable");
        caseMgmtService.setCaseData(caseId, "accountable", selectedApprover);
        wcs.checkResolution(caseId);

        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(selectedApprover, "en-UK");
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);

        taskService.start(task.getId(), selectedApprover);
        taskService.complete(task.getId(), selectedApprover, null);

        wcs.milestone(caseId, WorkCaseMilestones.CLOSE);

        String[] passedNodes = getPassedNodes(caseId);
        Assertions.assertThat(passedNodes).containsSequence("[Dynamic] Do it", "Check resolution", "Milestone: Close");

    }

    @Test(timeout = 30000)
    public void testCompleteHistory() {
        String responsiblePerson = "john";
        long caseId = wcs.createWork("Integrate module X into project Y", "Module X working in the project Y by ...", responsiblePerson);

        createAndCompleteTask(caseId, "Plan it", responsiblePerson, null, responsiblePerson);

        wcs.milestone(caseId, WorkCaseMilestones.PLANNING_DONE);

        createAndCompleteTask(caseId, "Design and implement it", responsiblePerson, null, responsiblePerson);

        String approver = "john";
        wcs.addApprover(caseId, approver);

        String selectedApprover = wcs.getRandomUserInTheRole(caseId, "accountable");
        caseMgmtService.setCaseData(caseId, "accountable", selectedApprover);
        wcs.checkResolution(caseId);

        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(selectedApprover, "en-UK");
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);

        taskService.start(task.getId(), selectedApprover);
        taskService.complete(task.getId(), selectedApprover, null);

        wcs.milestone(caseId, WorkCaseMilestones.WORK_DONE);
        wcs.milestone(caseId, WorkCaseMilestones.CLOSE);

        String[] passedNodes = getPassedNodes(caseId);
        for (String n : passedNodes) {
            System.out.println(n);
        }
        Assertions.assertThat(passedNodes).containsSequence("[Dynamic] Plan it", "Milestone: Planning Done", "[Dynamic] Design and implement it",
                "Check resolution", "Milestone: Work Done", "Milestone: Close");
    }

    private void createAndCompleteTask(long caseId, String taskName, String actors, String groupIds, String userId) {
        caseMgmtService.createDynamicHumanTask(caseId, taskName, actors, groupIds, null, null);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(userId, "en-UK");
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);

        taskService.start(task.getId(), userId);
        taskService.complete(task.getId(), userId, null);
    }

    private String[] getPassedNodes(long caseId) {
        AuditService auditService = getLogService();
        List<? extends NodeInstanceLog> nodes = auditService.findNodeInstances(caseId);
        String[] passedNodes = new String[nodes.size() / 2];
        for (NodeInstanceLog nil : nodes) {
            if (nil.getType() == NodeInstanceLog.TYPE_EXIT) {
                passedNodes[Integer.valueOf(nil.getNodeInstanceId())] = nil.getNodeName();
            }
        }
        return passedNodes;
    }
}
