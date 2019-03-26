/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.test.functional.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy;
import org.jbpm.services.task.impl.command.CommandBasedTaskService;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.JbpmJUnitBaseTestCase.Strategy;
import org.jbpm.test.entity.Application;
import org.jbpm.test.entity.Person;
import org.junit.Test;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ParentChildMarshallingJpaTest extends JbpmTestCase {

    private EntityManagerFactory emfDomain;


    public ParentChildMarshallingJpaTest() {
        super(true, true);
    }

	@Test
	public void testProcess() throws Exception {

	    emfDomain = Persistence.createEntityManagerFactory("org.jbpm.persistence.parent-child");
	    addEnvironmentEntry(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, 
	                        new ObjectMarshallingStrategy[] {
	                        new JPAPlaceholderResolverStrategy(emfDomain),
	                        new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT) });
	    
		RuntimeManager manager = createRuntimeManager(Strategy.PROCESS_INSTANCE, "manager", "org/jbpm/test/functional/jpa/parent-child.bpmn");
		RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
		KieSession ksession = runtime.getKieSession();

		// start a new process instance
		Map<String, Object> params = new HashMap<String, Object>();
		Application application = new Application();
		application.setType("A");
		params.put("application", application);
		ProcessInstance pi = ksession.startProcess("com.sample.bpmn.hello", params);
		System.out.println("A process instance started : pid = " + pi.getId());

		TaskService taskService = runtime.getTaskService();
		assertTrue(taskService instanceof CommandBasedTaskService);
		assertTrue(((CommandBasedTaskService) taskService).getEnvironment().get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES) != null);

		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		for (TaskSummary taskSummary : list) {
			System.out.println("john starts a task : taskId = " + taskSummary.getId());

			Task task = taskService.getTaskById(taskSummary.getId());
			long documentContentId = task.getTaskData().getDocumentContentId();
			Content content = taskService.getContentById(documentContentId);
			HashMap<String, Object> contents = (HashMap<String, Object>) ContentMarshallerHelper.unmarshall(content.getContent(), ksession.getEnvironment());

			Application outputApplication = (Application) contents.get("input1_application");
			Person person = new Person();
			person.setFullName("John Doe");
			outputApplication.setPerson(person);

			Map<String, Object> results = new LinkedHashMap<String, Object>();
			results.put("output1_application", outputApplication);
			

			taskService.start(taskSummary.getId(), "john");
			taskService.complete(taskSummary.getId(), "john", results);
		}

		list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		for (TaskSummary taskSummary : list) {
			System.out.println("mary starts a task : taskId = " + taskSummary.getId());
			taskService.start(taskSummary.getId(), "mary");
			taskService.complete(taskSummary.getId(), "mary", null);
		}
	
		manager.disposeRuntimeEngine(runtime);

		// Check!
		EntityManager em = emfDomain.createEntityManager();
		int size = em.createQuery("select i from Person i").getResultList().size();
		assertEquals(1, size);

	}

}