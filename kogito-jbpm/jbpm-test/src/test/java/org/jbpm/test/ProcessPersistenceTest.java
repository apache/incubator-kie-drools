/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.test;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * This is a sample file to test a process.
 */
public class ProcessPersistenceTest extends JbpmJUnitBaseTestCase {
	
	public ProcessPersistenceTest() {
		super(true, true);
	}

	@Test
	public void testProcess() {
	    RuntimeManager manager = createRuntimeManager("hello.bpmn");
	    RuntimeEngine runtimeEngine = getRuntimeEngine();
	    KieSession ksession = runtimeEngine.getKieSession();
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
		// check whether the process instance has completed successfully
		assertProcessInstanceCompleted(processInstance.getId());
		assertNodeTriggered(processInstance.getId(), "StartProcess", "Hello", "EndProcess");
		manager.disposeRuntimeEngine(runtimeEngine);
		manager.close();
	}

	@Test
	public void testTransactions() throws Exception {
	    createRuntimeManager("humantask.bpmn");
	    RuntimeEngine runtimeEngine = getRuntimeEngine();
	    KieSession ksession = runtimeEngine.getKieSession();
		
		UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
		ut.rollback();

		assertNull(ksession.getProcessInstance(processInstance.getId()));
	}
	
}
