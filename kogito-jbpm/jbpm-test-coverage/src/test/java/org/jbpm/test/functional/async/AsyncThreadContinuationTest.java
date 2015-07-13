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

package org.jbpm.test.functional.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.wih.FirstErrorWorkItemHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.executor.api.ExecutorService;

public class AsyncThreadContinuationTest extends JbpmTestCase {

    private static final String PROCESS_ATC = "org.jbpm.test.functional.async.AsyncThreadContinuation";
    private static final String PROCESS_IT = "org.jbpm.test.functional.event.IntermediateTimerErrorRetry";
    private static final String BPMN_ATC = "org/jbpm/test/functional/async/AsyncThreadContinuation.bpmn2";
    private static final String BPMN_IT = "org/jbpm/test/functional/event/IntermediateTimerErrorRetry.bpmn2";

    private ExecutorService executorService;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        executorService = ExecutorServiceFactory.newExecutorService(getEmf());
        executorService.setInterval(1);
        executorService.init();
        addEnvironmentEntry("ExecutorService", executorService);
        addWorkItemHandler("SyncError", new FirstErrorWorkItemHandler());
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        executorService.destroy();
    }

    @Test
    public void testCorrectProcessStateAfterException() {
        KieSession ksession = createKSession(BPMN_ATC);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("message", "Ivo");
        ProcessInstance pi = ksession.startProcess(PROCESS_ATC, params);

        List<? extends VariableInstanceLog> vars = getLogService().findVariableInstances(pi.getId(), "message");
        List<String> varValues = new ArrayList<String>();
        for (VariableInstanceLog v : vars) {
            varValues.add(v.getValue());
        }

        Assertions.assertThat(varValues).contains("Hello Ivo").doesNotContain("Hello Ivo asynchronously");
    }

    @Test
    public void testRepeatFailingSyncTask() {
        KieSession ksession = createKSession(BPMN_ATC);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("message", "Ivo");
        ProcessInstance pi = ksession.startProcess(PROCESS_ATC, params);
        try {
            Thread.sleep(1500);
        } catch (Exception ex) {

        }
        List<? extends VariableInstanceLog> vars = getLogService().findVariableInstances(pi.getId(), "message");
        List<String> varValues = new ArrayList<String>();
        for (VariableInstanceLog v : vars) {
            varValues.add(v.getValue());
        }

        Assertions.assertThat(varValues).contains("Hello Ivo asynchronously");
    }

    @Test
    public void testRepeatIntermediateTimerAfterException() {
        KieSession ksession = createKSession(BPMN_IT);
        ProcessInstance pi = ksession.startProcess(PROCESS_IT);
        long pid = pi.getId();
        try {
            Thread.sleep(2200 + 1200); // 2s timer + 1s retry
        } catch (Exception ex) {

        }
        pi = ksession.getProcessInstance(pid);
        Assertions.assertThat(pi).isNull();
        ProcessInstanceLog log = getLogService().findProcessInstance(pid);
        Assertions.assertThat(log.getStatus()).isEqualTo(ProcessInstance.STATE_ABORTED);
    }

}
