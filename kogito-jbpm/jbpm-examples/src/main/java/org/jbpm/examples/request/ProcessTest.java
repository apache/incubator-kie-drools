/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.examples.request;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.DynamicUtils;
import org.kie.api.KieServices;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.runtime.rule.FactHandle;

/**
 * This is a sample file to launch a process.
 */
public class ProcessTest {

	public static final void main(String[] args) {
		try {
			// load up the knowledge base
			final KieSession ksession = getKieSession();
			UIWorkItemHandler handler = new UIWorkItemHandler();
			ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
			handler.setVisible(true);
			ksession.getWorkItemManager().registerWorkItemHandler("Email", new WorkItemHandler() {
				public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
					System.out.println("Sending email ...");
					manager.completeWorkItem(workItem.getId(), null);
				}
				public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
				}
			});
			KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newFileLogger(ksession, "test");
			// start a new process instance
			Person person = new Person("john", "John Doe");
			person.setAge(20);
			Request request = new Request("12345");
			request.setPersonId("john");
			request.setAmount(1000L);
			ksession.insert(person);
			ksession.insert(request);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("request", request);
			WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("com.sample.requestHandling", params);
			ksession.insert(processInstance);
			ksession.fireAllRules();
			// signaling to select tasks in the ad-hoc sub-process
			ksession.signalEvent("Investigate", null, processInstance.getId());
			// dynamically adding a new sub-process
			DynamicNodeInstance dynamicNodeInstance = (DynamicNodeInstance)
				processInstance.getNodeInstances().iterator().next();
			DynamicUtils.addDynamicSubProcess(dynamicNodeInstance, ksession, "com.sample.contactCustomer", null);
			// event processing to detect too much processes being started
			ksession.addEventListener(new DefaultProcessEventListener() {
				public void beforeProcessStarted(ProcessStartedEvent event) {
					ksession.insert(event);
				}
			});
			Request request2 = new Request("12346");
			request2.setPersonId("john");
			request2.setAmount(1000L);
			params = new HashMap<String, Object>();
			params.put("request", request2);
			ksession.startProcess("com.sample.requestHandling", params);
			ksession.fireAllRules();
			Request request3 = new Request("12347");
			request3.setPersonId("john");
			request3.setAmount(1000L);
			params = new HashMap<String, Object>();
			params.put("request", request);
			ksession.startProcess("com.sample.requestHandling", params);
			ksession.fireAllRules();
			Request request4 = new Request("12348");
			request4.setPersonId("john");
			request4.setAmount(1000L);
			params = new HashMap<String, Object>();
			params.put("request", request4);
			ksession.startProcess("com.sample.requestHandling", params);
			ksession.fireAllRules();
			Request request5 = new Request("12349");
			request5.setPersonId("john");
			request5.setAmount(1000L);
			params = new HashMap<String, Object>();
			params.put("request", request5);
			ksession.startProcess("com.sample.requestHandling", params);
			ksession.fireAllRules();
			Request request6 = new Request("12350");
			request6.setPersonId("john");
			request6.setAmount(1000L);
			params = new HashMap<String, Object>();
			params.put("request", request6);
			processInstance = (WorkflowProcessInstance) ksession.startProcess("com.sample.requestHandling", params);
			FactHandle handle = ksession.insert(request6);
			ksession.insert(processInstance);
			ksession.fireAllRules();
			// exception handling when canceling request
			request6.setCanceled(true);
			ksession.update(handle, request6);
			ksession.fireAllRules();
			logger.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static KieSession getKieSession() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newEmptyBuilder()
            .addAsset(KieServices.Factory.get().getResources().newClassPathResource("request/requestHandling.bpmn"), ResourceType.BPMN2)
            .addAsset(KieServices.Factory.get().getResources().newClassPathResource("request/contactCustomer.bpmn"), ResourceType.BPMN2)
            .addAsset(KieServices.Factory.get().getResources().newClassPathResource("request/validation.drl"), ResourceType.DRL)
            .addAsset(KieServices.Factory.get().getResources().newClassPathResource("request/eventProcessing.drl"), ResourceType.DRL)
            .addAsset(KieServices.Factory.get().getResources().newClassPathResource("request/exceptions.drl"), ResourceType.DRL)
            .addAsset(KieServices.Factory.get().getResources().newClassPathResource("request/adhoc.drl"), ResourceType.DRL)
            .get();
        return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment).getRuntimeEngine(null).getKieSession();
	}
	
}
