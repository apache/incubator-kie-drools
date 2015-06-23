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

package org.jbpm.test.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.services.task.wih.ExternalTaskEventListener;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.event.KnowledgeRuntimeEventManager;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalTasksServiceTest extends JbpmJUnitBaseTestCase {

    private static final Logger logger = LoggerFactory.getLogger(LocalTasksServiceTest.class);
    
    private EntityManagerFactory emfTasks;
    protected Map<String, User> users;
    protected Map<String, Group> groups;
    
    protected Properties conf;
    
    protected ExternalTaskEventListener externalTaskEventListener;


    public LocalTasksServiceTest() {
        super(true, true);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        emfTasks = Persistence.createEntityManagerFactory("org.jbpm.services.task");       
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        if (emfTasks != null && emfTasks.isOpen()) {
            emfTasks.close();
        }
    }

    @Test 
    public void groupTaskQueryTest() throws Exception {

        createRuntimeManager("Evaluation2.bpmn");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();

        KnowledgeRuntimeLoggerFactory.newConsoleLogger((KnowledgeRuntimeEventManager) ksession);
 
        logger.info("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("employee", "salaboy");
        ProcessInstance process = ksession.startProcess("com.sample.evaluation", parameters);
        long processInstanceId = process.getId();

        //The process is in the first Human Task waiting for its completion
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());

        //gets salaboy's tasks
        List<TaskSummary> salaboysTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        Assert.assertEquals(1, salaboysTasks.size());


        taskService.start(salaboysTasks.get(0).getId(), "salaboy");

        taskService.complete(salaboysTasks.get(0).getId(), "salaboy", null);

        List<TaskSummary> pmsTasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");

        Assert.assertEquals(1, pmsTasks.size());


        List<TaskSummary> hrsTasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");

        Assert.assertEquals(1, hrsTasks.size());

    }

    
    @Test
    public void testMultipleActorsClaimedQuery() {
        RuntimeManager manager = createRuntimeManager("BPMN2-HumanTaskMultipleActors.bpmn2");        
        RuntimeEngine runtime = getRuntimeEngine();
        KieSession ksession = runtime.getKieSession();

        // start a new process instance
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance pi = ksession.startProcess("com.sample.humantask.multipleactors", params);

        // krisv claim task
        TaskService taskService = runtime.getTaskService();
		List<TaskSummary> task1 = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		assertNotNull(task1);
		assertEquals(1, task1.size());
		
        System.out.println("krisv's task:" + task1.get(0).getName());
        taskService.claim(task1.get(0).getId(), "krisv");
        
        // john can get task which krisv has already claimed
        List<TaskSummary> task2 = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(task2);
		assertEquals(0, task2.size());
		
		taskService.start(task1.get(0).getId(), "krisv");
		taskService.complete(task1.get(0).getId(), "krisv", null);
		
		assertProcessInstanceCompleted(pi.getId());
		
		manager.disposeRuntimeEngine(runtime);
    }
   
    
	@Test
	public void testHumanTaskWithSingleTypeContent() {
		RuntimeManager manager = createRuntimeManager("HumanTaskWithSingleTypeContent.bpmn2");
		RuntimeEngine runtime = getRuntimeEngine();
		KieSession ksession = runtime.getKieSession();
		TaskService taskService = runtime.getTaskService();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pVar", "sampleValue");

		ksession.startProcess("com.sample.bpmn.hello1", params);

		// let john execute Task 1
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		TaskSummary task = list.get(0);
		logger.info("John is executing task {}", task.getName());
		taskService.start(task.getId(), "john");

		// let's verify content, first manually by using marshaler helper
		Content content = taskService.getContentById(taskService.getTaskById(task.getId()).getTaskData().getDocumentContentId());
		byte[] contentbyte = content.getContent();
		Object tmpObject = ContentMarshallerHelper.unmarshall(contentbyte, ksession.getEnvironment());
		assertNotNull(tmpObject);
		assertTrue(tmpObject instanceof Map);
		assertEquals("someContent", ((Map)tmpObject).get("Content"));

		// then by using getTaskContent api method
		Map<String, Object> contentMap = taskService.getTaskContent(task.getId());
		assertNotNull(contentMap);
		assertEquals(1, contentMap.size());
		assertTrue(contentMap.containsKey("Content"));
		
		String actualContent = (String) contentMap.get("Content");
		assertNotNull(actualContent);
		assertEquals("someContent", actualContent);
		
		// let's move on to complete the tasks and process instance
		taskService.complete(task.getId(), "john", null);

		// let mary execute Task 2
		list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		task = list.get(0);
		logger.info("Mary is executing task {}", task.getName());
		taskService.start(task.getId(), "mary");
		taskService.complete(task.getId(), "mary", null);

		manager.disposeRuntimeEngine(runtime);
	}
}
