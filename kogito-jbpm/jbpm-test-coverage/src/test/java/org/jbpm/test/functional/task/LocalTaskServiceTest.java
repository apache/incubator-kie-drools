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

package org.jbpm.test.functional.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.services.task.wih.util.LocalHTWorkItemHandlerUtil;
import org.jbpm.test.JbpmTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;

import static org.junit.Assert.*;

public class LocalTaskServiceTest extends JbpmTestCase {

    private static final String HUMAN_TASK = "org/jbpm/test/functional/common/HumanTask.bpmn2";
    private static final String HUMAN_TASK_ID = "org.jbpm.test.functional.common.HumanTask";
    
    private static final String EVALUATION = "org/jbpm/test/functional/task/Evaluation2.bpmn";
    private static final String EVALUTION_ID = "com.sample.evaluation";
    
    private static final String TASK_MULTIPLE_ACTORS = "org/jbpm/test/functional/task/HumanTaskMultipleActors.bpmn2";
    private static final String TASK_MULTIPLE_ACTORS_ID = "com.sample.humantask.multipleactors";
    
    private static final String TASK_SINGLE_TYPE = "org/jbpm/test/functional/task/HumanTaskWithSingleTypeContent.bpmn2";
    private static final String TASK_SINGLE_TYPE_ID = "com.sample.bpmn.hello1";

    private static final String USER_GROUP_RES = "classpath:/usergroups.properties";
    private static final String BUSINESS_ADMINISTRATOR = "Administrator"; // member of Administrators group from properties

    private KieSession kieSession;
    private TaskService taskService;

    public LocalTaskServiceTest() {
        super(true, true);
    }

    @Before
    public void init() throws Exception {
        createRuntimeManager(HUMAN_TASK, EVALUATION, TASK_MULTIPLE_ACTORS, TASK_SINGLE_TYPE);
        RuntimeEngine re = getRuntimeEngine();
        kieSession = re.getKieSession();
        taskService = LocalHTWorkItemHandlerUtil.registerLocalHTWorkItemHandler(kieSession, getEmf(),
                new JBossUserGroupCallbackImpl(USER_GROUP_RES));
    }

    @Test
    public void executeTaskCompleteTest() {
        ProcessInstance processInstance = kieSession.startProcess(HUMAN_TASK_ID);

        assertProcessInstanceActive(processInstance.getId());
        assertNodeTriggered(processInstance.getId(), "start", "user task");

        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);

        assertNodeTriggered(processInstance.getId(), "end");
        assertProcessInstanceCompleted(processInstance.getId());
    }

    /**
     * Mary should not have permissions to delegate a task.
     */
    @Test(expected = PermissionDeniedException.class)
    public void executeTaskDelegationTest() {
        ProcessInstance processInstance = kieSession.startProcess(HUMAN_TASK_ID);

        assertProcessInstanceActive(processInstance.getId());
        assertNodeTriggered(processInstance.getId(), "start", "user task");

        List<TaskSummary> list = taskService.getTasksAssignedAsBusinessAdministrator(BUSINESS_ADMINISTRATOR, "en-UK");
        TaskSummary task = list.get(0);
        delegateTask(task, "mary", "doctor");
    }

    @Test
    public void executeTaskDelegationByBusinessAdministratorTest() {
        ProcessInstance processInstance = kieSession.startProcess(HUMAN_TASK_ID);

        assertProcessInstanceActive(processInstance.getId());
        assertNodeTriggered(processInstance.getId(), "start", "user task");

        List<TaskSummary> list = taskService.getTasksAssignedAsBusinessAdministrator(BUSINESS_ADMINISTRATOR, "en-UK");
        TaskSummary task = list.get(0);
        delegateTask(task, BUSINESS_ADMINISTRATOR, "doctor");

        assertNodeTriggered(processInstance.getId(), "end");
        assertProcessInstanceCompleted(processInstance.getId());
    }
    
    @Test 
    public void groupTaskQueryTest() throws Exception {

        KnowledgeRuntimeLoggerFactory.newConsoleLogger(kieSession);
 
        logger.info("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("employee", "salaboy");
        ProcessInstance process = kieSession.startProcess(EVALUTION_ID, parameters);

        //The process is in the first Human Task waiting for its completion
        assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());

        //gets salaboy's tasks
        List<TaskSummary> salaboysTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertEquals(1, salaboysTasks.size());


        taskService.start(salaboysTasks.get(0).getId(), "salaboy");

        taskService.complete(salaboysTasks.get(0).getId(), "salaboy", null);

        List<TaskSummary> pmsTasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");

        assertEquals(1, pmsTasks.size());


        List<TaskSummary> hrsTasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");

        assertEquals(1, hrsTasks.size());

    }

    
    @Test
    public void testMultipleActorsClaimedQuery() {
        // start a new process instance
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance pi = kieSession.startProcess(TASK_MULTIPLE_ACTORS_ID, params);

        // krisv claim task
        
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
    }
   
    
    @Test
    public void testHumanTaskWithSingleTypeContent() {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pVar", "sampleValue");

        kieSession.startProcess(TASK_SINGLE_TYPE_ID, params);

        // let john execute Task 1
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        logger.info("John is executing task {}", task.getName());
        taskService.start(task.getId(), "john");

        // let's verify content, first manually by using marshaler helper
        Content content = taskService.getContentById(taskService.getTaskById(task.getId()).getTaskData().getDocumentContentId());
        byte[] contentbyte = content.getContent();
        Object tmpObject = ContentMarshallerHelper.unmarshall(contentbyte, kieSession.getEnvironment());
        assertNotNull(tmpObject);
        assertTrue(tmpObject instanceof String);
        assertEquals("someContent", tmpObject);

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

    }

    private void delegateTask(final TaskSummary task, final String businessAdministrator, final String delegateTo) {
        taskService.delegate(task.getId(), businessAdministrator, delegateTo);
        taskService.start(task.getId(), delegateTo);
        taskService.complete(task.getId(), delegateTo, null);
    }

}
