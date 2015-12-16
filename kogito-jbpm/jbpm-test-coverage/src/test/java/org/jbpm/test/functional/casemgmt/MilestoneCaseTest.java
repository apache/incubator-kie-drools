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

import org.assertj.core.api.Assertions;
import org.jbpm.casemgmt.CaseMgmtService;
import org.jbpm.casemgmt.CaseMgmtUtil;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;

import qa.tools.ikeeper.annotation.BZ;

public class MilestoneCaseTest extends JbpmTestCase {
    
    protected static final String TERMINATE_CASE = "org/jbpm/test/functional/casemgmt/TerminateMilestone.bpmn2";

    @Test(timeout = 30000)
    @BZ("1256700")
    public void testProcessCompleted() {
        addWorkItemHandler("Milestone", new SystemOutWorkItemHandler());
        KieSession ksession = createKSession(TERMINATE_CASE);
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        CaseMgmtService caseMgmtService = new CaseMgmtUtil(runtimeEngine);
        ProcessInstance pi = caseMgmtService.startNewCase("completeProcess");
        long pid = pi.getId();

        Assertions.assertThat(pi.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        String[] achievedMilestones = caseMgmtService.getAchievedMilestones(pid);
        Assertions.assertThat(achievedMilestones).isNullOrEmpty();

        // pi.signalEvent("Terminate", null);
        caseMgmtService.triggerAdHocFragment(pid, "Terminate");

        ProcessInstanceLog pil = getLogService().findProcessInstance(pid);
        Assertions.assertThat(pil.getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        achievedMilestones = caseMgmtService.getAchievedMilestones(pid);
        Assertions.assertThat(achievedMilestones).hasSize(1);
        Assertions.assertThat(achievedMilestones[0]).isEqualTo("Terminate");

    }

}
