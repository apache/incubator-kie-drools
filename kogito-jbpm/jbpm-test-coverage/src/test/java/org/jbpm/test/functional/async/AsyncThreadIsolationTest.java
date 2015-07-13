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
import org.jbpm.process.workitem.bpmn2.ServiceTaskHandler;
import org.jbpm.test.JbpmTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.executor.api.ExecutorService;

public class AsyncThreadIsolationTest extends JbpmTestCase {

    private static final String PROCESS_ATI = "org.jbpm.test.functional.async.AsyncThreadIsolation";
    private static final String BPMN_ATI = "org/jbpm/test/functional/async/AsyncThreadIsolation.bpmn2";

    private ExecutorService executorService;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        executorService = ExecutorServiceFactory.newExecutorService(getEmf());
        executorService.setInterval(1);
        executorService.init();
        addEnvironmentEntry("ExecutorService", executorService);
        addWorkItemHandler("Service Task", new ServiceTaskHandler());
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        executorService.destroy();
    }

    @Test
    public void testCorrectProcessStateAfterException() {
        KieSession ksession = createKSession(BPMN_ATI);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("message", "Ivo");
        ProcessInstance pi = ksession.startProcess(PROCESS_ATI, params);
        
        try {
            Thread.sleep(2500);
        } catch (Exception ex) {
            
        }
        
        List<? extends VariableInstanceLog> vars = getLogService().findVariableInstances(pi.getId(), "message");
        List<String> varValues = new ArrayList<String>();
        for (VariableInstanceLog v : vars) {
            varValues.add(v.getValue());
        }
        
        Assertions.assertThat(varValues).contains("Hello Ivo asynchronously");
        
        pi = ksession.getProcessInstance(pi.getId());
        Assertions.assertThat(pi).isNotNull();
        Assertions.assertThat(pi.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
    }

}
