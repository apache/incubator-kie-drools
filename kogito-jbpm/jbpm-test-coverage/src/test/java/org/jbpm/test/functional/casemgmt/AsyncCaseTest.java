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
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.assertj.core.api.Assertions;
import org.jbpm.casemgmt.CaseMgmtService;
import org.jbpm.casemgmt.CaseMgmtUtil;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.wih.AsyncWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.jobexec.CheckCallCommand;
import org.junit.Test;
import org.kie.api.executor.ExecutorService;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

public class AsyncCaseTest extends JbpmTestCase {
    
    protected static final String TERMINATE_CASE = "org/jbpm/test/functional/casemgmt/TerminateMilestone.bpmn2";

    @Test(timeout = 30000)
    public void testAsyncWorkItem() throws Exception {
        
        ExecutorService executorService = ExecutorServiceFactory.newExecutorService();
        executorService.setThreadPoolSize(1);
        executorService.setInterval(1);
        executorService.setRetries(2);
        executorService.init();

        addWorkItemHandler("async", new AsyncWorkItemHandler(executorService));
        
        addWorkItemHandler("Milestone", new SystemOutWorkItemHandler());
        KieSession ksession = createKSession(TERMINATE_CASE);
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        CaseMgmtService caseMgmtService = new CaseMgmtUtil(runtimeEngine);
        ProcessInstance pi = caseMgmtService.startNewCase("AsyncWorkItem");
        long pid = pi.getId();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("CommandClass", CheckCallCommand.class.getCanonicalName());
        caseMgmtService.createDynamicWorkTask(pid, "async", params);
       
        // This will time out if the barrier waits for longer than 5 seconds.
        // The .await(..) call will only timeout (throw an exception) if the 
        //   other party (that is calling .execute(CommandContext)) has *not* also 
        //   called await (in CheckCallCommand.execute(CommandContext)
        // In this way, it's also a check to see if the command has executed
        CheckCallCommand.getBarrier().await(5, TimeUnit.SECONDS);

        caseMgmtService.triggerAdHocFragment(pid, "Terminate");
        

    }

}
