package org.jbpm.test.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.services.task.impl.model.GroupImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.jbpm.services.task.wih.ExternalTaskEventListener;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.event.KnowledgeRuntimeEventManager;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.TaskService;
import org.kie.internal.task.api.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalTasksServiceTest extends JbpmJUnitTestCase {

    private static Logger logger = LoggerFactory.getLogger(LocalTasksServiceTest.class);
    
    private EntityManagerFactory emfTasks;
    protected Map<String, UserImpl> users;
    protected Map<String, GroupImpl> groups;
    

    protected Properties conf;
    
    protected ExternalTaskEventListener externalTaskEventListener;
    
    protected RuntimeManager manager;

    public LocalTasksServiceTest() {
        super(true);
        setPersistence(true);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        emfTasks = Persistence.createEntityManagerFactory("org.jbpm.services.task");       
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        manager.close();
        if (emfTasks != null && emfTasks.isOpen()) {
            emfTasks.close();
        }
    }

    @Test 
    public void groupTaskQueryTest() throws Exception {

    	Properties userGroups = new Properties();
        userGroups.setProperty("salaboy", "");
        userGroups.setProperty("john", "PM");
        userGroups.setProperty("mary", "HR");
        
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault()
                .userGroupCallback(new JBossUserGroupCallbackImpl(userGroups))
                .addAsset(ResourceFactory.newClassPathResource("Evaluation2.bpmn"), ResourceType.BPMN2)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        org.kie.internal.runtime.manager.Runtime runtime = manager.getRuntime(EmptyContext.get());
        
        KieSession ksession = runtime.getKieSession();
        TaskService taskService = runtime.getTaskService();
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
