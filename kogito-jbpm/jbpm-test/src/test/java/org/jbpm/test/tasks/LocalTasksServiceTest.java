package org.jbpm.test.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.wih.ExternalTaskEventListener;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.event.KnowledgeRuntimeEventManager;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
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

   
}
