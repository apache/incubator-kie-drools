package org.jbpm.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.persistence.objects.MockUserInfo;
import org.jbpm.shared.services.api.JbpmServicesTransactionManager;
import org.jbpm.shared.services.impl.JbpmJTATransactionManager;
import org.jbpm.task.HumanTaskServiceFactory;
import org.jbpm.task.impl.model.GroupImpl;
import org.jbpm.task.impl.model.UserImpl;
import org.jbpm.task.wih.ExternalTaskEventListener;
import org.jbpm.task.wih.HTWorkItemHandlerFactory;
import org.jbpm.task.wih.LocalHTWorkItemHandler;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.task.api.TaskService;
import org.kie.internal.task.api.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalTasksServiceTest extends JbpmJUnitTestCase {

    private static Logger logger = LoggerFactory.getLogger(LocalTasksServiceTest.class);
    private HashMap<String, Object> context;
    
    private EntityManagerFactory emfTasks;
    protected Map<String, UserImpl> users;
    protected Map<String, GroupImpl> groups;
    protected TaskService taskService;

    protected MockUserInfo userInfo;
    protected Properties conf;
    
    protected ExternalTaskEventListener externalTaskEventListener;

    public LocalTasksServiceTest() {
        super(true);
        setPersistence(true);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        

        
//        conf = new Properties();
//        conf.setProperty("mail.smtp.host", "localhost");
//        conf.setProperty("mail.smtp.port", "1125");
//        conf.setProperty("from", "from@domain.com");
//        conf.setProperty("replyTo", "replyTo@domain.com");
//        conf.setProperty("defaultLanguage", "en-UK");
//
//        SendIcal.initInstance(conf);


        emfTasks = Persistence.createEntityManagerFactory("org.jbpm.task");

        userInfo = new MockUserInfo();
        
        
        JbpmServicesTransactionManager txManager = new JbpmJTATransactionManager();
        HumanTaskServiceFactory.setEntityManagerFactory(emfTasks);
        HumanTaskServiceFactory.setJbpmServicesTransactionManager(txManager);
        
        taskService = HumanTaskServiceFactory.newTaskService();
       
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

    	Properties userGroups = new Properties();
        userGroups.setProperty("salaboy", "");
        userGroups.setProperty("john", "PM");
        userGroups.setProperty("mary", "HR");
        
        StatefulKnowledgeSession ksession = createKnowledgeSession("Evaluation2.bpmn");
        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
 
        LocalHTWorkItemHandler htWorkItemHandler = HTWorkItemHandlerFactory.newHandler(ksession, taskService);
        
        
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", htWorkItemHandler);
        logger.info("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("employee", "salaboy");
        ProcessInstance process = ksession.startProcess("com.sample.evaluation", parameters);
        long processInstanceId = process.getId();

        //The process is in the first Human Task waiting for its completion
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());

        //gets salaboy's tasks
        List<TaskSummary> salaboysTasks = this.taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        Assert.assertEquals(1, salaboysTasks.size());


        this.taskService.start(salaboysTasks.get(0).getId(), "salaboy");

        this.taskService.complete(salaboysTasks.get(0).getId(), "salaboy", null);

        List<TaskSummary> pmsTasks = this.taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");

        Assert.assertEquals(1, pmsTasks.size());


        List<TaskSummary> hrsTasks = this.taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");

        Assert.assertEquals(1, hrsTasks.size());

    }

   
}
