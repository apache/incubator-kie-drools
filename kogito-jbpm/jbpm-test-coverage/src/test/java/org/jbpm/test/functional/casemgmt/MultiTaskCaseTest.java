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

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jbpm.casemgmt.CaseMgmtService;
import org.jbpm.casemgmt.CaseMgmtUtil;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;

public class MultiTaskCaseTest extends JbpmTestCase {
    
    protected static final String MULTI_TASK_CASE = "org/jbpm/test/functional/casemgmt/MultiTaskCase.bpmn2";

    @Test(timeout = 30000)
    public void testTriggerTaskTwice() {
        addWorkItemHandler("Milestone", new SystemOutWorkItemHandler());
        KieSession ksession = createKSession(MULTI_TASK_CASE);
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        CaseMgmtService caseMgmtService = new CaseMgmtUtil(runtimeEngine);
        ProcessInstance pi = caseMgmtService.startNewCase("triggerTaskTwice");
        long pid = pi.getId();

        Assertions.assertThat(pi.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        String[] achievedMilestones = caseMgmtService.getAchievedMilestones(pid);
        Assertions.assertThat(achievedMilestones).isNullOrEmpty();

        caseMgmtService.triggerAdHocFragment(pid, "T1");
        caseMgmtService.triggerAdHocFragment(pid, "T1");
        caseMgmtService.triggerAdHocFragment(pid, "T1");
        
        AuditService auditService = getLogService();
        List<? extends NodeInstanceLog> nodes = auditService.findNodeInstances(pid, "_4");
        List<String> passedNodes = new ArrayList<String>();
        for (NodeInstanceLog nil : nodes) {
            if (nil.getType() == NodeInstanceLog.TYPE_EXIT) {
                System.out.println(nil);
                passedNodes.add(nil.getNodeName());
            }
        }
        
        Assertions.assertThat(passedNodes).hasSize(3);
        Assertions.assertThat(passedNodes).containsOnly("T1");
        
        
        caseMgmtService.triggerAdHocFragment(pid, "Terminate");

    }

}
