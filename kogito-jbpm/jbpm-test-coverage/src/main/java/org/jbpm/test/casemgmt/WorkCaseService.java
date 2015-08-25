/*
 * Copyright 2015 JBoss Inc
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

package org.jbpm.test.casemgmt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.casemgmt.CaseMgmtService;
import org.jbpm.casemgmt.CaseMgmtUtil;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.model.InternalTask;

public class WorkCaseService {

    private RuntimeEngine runtimeEngine;
    private KieSession kieSession;
    private CaseMgmtService caseMgmtService;
    private TaskService taskService;

    public WorkCaseService(RuntimeEngine runtimeEngine) {
        this.runtimeEngine = runtimeEngine;
        kieSession = runtimeEngine.getKieSession();
        caseMgmtService = new CaseMgmtUtil(runtimeEngine);
        taskService = runtimeEngine.getTaskService();
    }

    public long createWork(String name, String goal, String responsiblePerson) {
        return createWork(name, goal, responsiblePerson, 10);
    }

    public long createWork(String name, String goal, String responsiblePerson, int timeout) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("timeout", timeout);
        ProcessInstance pi = kieSession.startProcess("WorkCase", params);
        long caseId = pi.getId();

        caseMgmtService.setCaseData(caseId, "goal", goal);
        caseMgmtService.addUserToRole(caseId, "responsible", responsiblePerson);
        return caseId;
    }

    public void addApprover(long caseId, String userId) {
        caseMgmtService.addUserToRole(caseId, "accountable", userId);
    }

    public void addConsultant(long caseId, String userId) {
        caseMgmtService.addUserToRole(caseId, "consulted", userId);
    }

    public void addInformedPerson(long caseId, String userId) {
        caseMgmtService.addUserToRole(caseId, "informed", userId);
    }

    public void informAbout(long caseId, String message) {
        caseMgmtService.setCaseData(caseId, "informAbout", message);
        caseMgmtService.triggerAdHocFragment(caseId, "Inform interested people");
    }

    /**
     * To plan the meeting.
     */
    public void consultWith(long caseId, String userId) {
        List<String> consultants = Arrays.asList(caseMgmtService.getCaseRoleInstanceNames(caseId).get("consulted"));
        if (!consultants.contains(userId)) {
            addConsultant(caseId, userId);
        }

        caseMgmtService.setCaseData(caseId, "consultant", userId);
        caseMgmtService.triggerAdHocFragment(caseId, "Consult work with");

        List<Long> taskIds = taskService.getTasksByProcessInstanceId(caseId);
        for (Long taskId : taskIds) {
            Task t = taskService.getTaskById(taskId);
            if (t.getName().startsWith("Consult work with")) {
                ((InternalTask) t).setName("Consult work with " + userId);
            }
        }
    }

    /**
     * One of the accountable people will check the work solution.
     */
    public void checkResolution(long caseId) {
        caseMgmtService.triggerAdHocFragment(caseId, "Check resolution");
    }

    public void milestone(long caseId, WorkCaseMilestones milestone) {
        caseMgmtService.triggerAdHocFragment(caseId, milestone.getTaskName());
    }

    public void prolongTimeoutTo(long caseId, int timeout) {
        kieSession.execute(new UpdateTimerCommand(caseId, "Timeout", timeout));
        caseMgmtService.setCaseData(caseId, "timeout", 15);
    }

    public static enum WorkCaseMilestones {
        PLANNING_DONE("Milestone: Planning Done"), WORK_DONE("Milestone: Work Done"), CLOSE("Milestone: Close");

        private String taskName;

        private WorkCaseMilestones(String taskName) {
            this.taskName = taskName;
        }

        public String getTaskName() {
            return taskName;
        }
    }

}
