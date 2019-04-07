/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.functional.event;

import java.util.Arrays;
import java.util.Collection;

import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

@RunWith(Parameterized.class)
public class SignalProcessTest extends JbpmTestCase {
	
	@Parameters(name="Persistence={1} - data source={0}")
    public static Collection<Object[]> parameters() {
        Object[][] locking = new Object[][] { 
                { true, true }, 
                { false, false }                
                };
        return Arrays.asList(locking);
    };
	
	public SignalProcessTest(boolean dataSource, boolean persistence) {
		super(dataSource, persistence);
	}

	@Test
    public void testDoubleSignalProcess() {
	    createRuntimeManager("org/jbpm/test/functional/event/sample_doublesignal.bpmn2");
	    RuntimeEngine runtimeEngine = getRuntimeEngine();
	    KieSession ksession = runtimeEngine.getKieSession();

        ProcessInstance processInstance = ksession.startProcess("com.sample.signal");
        assertProcessInstanceActive(processInstance.getId(), ksession);
        
        ksession.signalEvent("Signal1", "", processInstance.getId());
        assertProcessInstanceActive(processInstance.getId(), ksession);
        
        ksession.signalEvent("Signal1", "", processInstance.getId());
        
        // check whether the process instance has completed successfully
        assertProcessInstanceNotActive(processInstance.getId(), ksession);
        
    }
	
	@Test
    public void testDoubleMessageProcess() {
	    createRuntimeManager("org/jbpm/test/functional/event/sample_doublemessagesignal.bpmn2");
	    RuntimeEngine runtimeEngine = getRuntimeEngine();
	    KieSession ksession = runtimeEngine.getKieSession();

        ProcessInstance processInstance = ksession.startProcess("com.sample.msg");
        assertProcessInstanceActive(processInstance.getId(), ksession);
        
        ksession.signalEvent("Message-TestMessage", "", processInstance.getId());
        assertProcessInstanceActive(processInstance.getId(), ksession);
        
        ksession.signalEvent("Message-TestMessage", "", processInstance.getId());
        
        // check whether the process instance has completed successfully
        assertProcessInstanceNotActive(processInstance.getId(), ksession);
        
    }

}
