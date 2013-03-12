package org.jbpm.tasks.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.UserInfo;
import org.jbpm.task.admin.listener.TaskCleanUpProcessEventListener;

import org.jbpm.task.api.TaskServiceEntryPoint;

import org.jbpm.task.query.TaskSummary;

import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminAPIsWithListenerTest extends JbpmJUnitTestCase {

    private static Logger logger = LoggerFactory.getLogger(AdminAPIsWithListenerTest.class);
    private HashMap<String, Object> context;
//    private EntityManagerFactory emf;
//    private EntityManagerFactory emfDomain;
    private EntityManagerFactory emfTasks;
    protected Map<String, User> users;
    protected Map<String, Group> groups;
    protected UserInfo userInfo;
    protected Properties conf;

    //    @Before
    //    public void setUp() throws Exception {
    //        context = setupWithPoolingDataSource("org.jbpm.runtime", false);
    //        emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);
    //
    ////        conf = new Properties();
    ////        conf.setProperty("mail.smtp.host", "localhost");
    ////        conf.setProperty("mail.smtp.port", "1125");
    ////        conf.setProperty("from", "from@domain.com");
    ////        conf.setProperty("replyTo", "replyTo@domain.com");
    ////        conf.setProperty("defaultLanguage", "en-UK");
    ////
    ////        SendIcal.initInstance(conf);
    //
    //        // Use persistence.xml configuration
    //
    //        emfTasks = Persistence.createEntityManagerFactory("org.jbpm.task");
    //
    //        userInfo = new DefaultUserInfo(null);
    //
    //
    //
    //
    //
    //    }
    //
    //    @After
    //    public void tearDown() throws Exception {
    //        cleanUp(context);
    //
    //
    ////        admin.dispose();
    //
    //        if(emfTasks != null && emfTasks.isOpen()){
    //            emfTasks.close();
    //        }
    //    }
    public AdminAPIsWithListenerTest() {
        super(true);
        setPersistence(true);
    }


    @Test @Ignore
    public void automaticCleanUpTest() throws Exception {


        emfTasks = Persistence.createEntityManagerFactory("org.jbpm.task");
        
        KnowledgeBase kbase = createKnowledgeBase("patient-appointment.bpmn");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        TaskServiceEntryPoint taskService = getTaskService(ksession);

        taskService.setUserInfo(userInfo);

        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);

        ksession.addEventListener(new TaskCleanUpProcessEventListener(taskService));

        logger.info("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();

        ProcessInstance process = ksession.startProcess("org.jbpm.PatientAppointment", parameters);
        long processInstanceId = process.getId();

        //The process is in the first Human Task waiting for its completion
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());

        //gets frontDesk's tasks
        List<TaskSummary> frontDeskTasks = taskService.getTasksAssignedAsPotentialOwner("frontDesk", "en-UK");
        Assert.assertEquals(1, frontDeskTasks.size());

        //doctor doesn't have any task
        List<TaskSummary> doctorTasks = taskService.getTasksAssignedAsPotentialOwner("doctor", "en-UK");
        Assert.assertTrue(doctorTasks.isEmpty());

        //manager doesn't have any task
        List<TaskSummary> managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertTrue(managerTasks.isEmpty());


        taskService.start(frontDeskTasks.get(0).getId(), "frontDesk");

        taskService.complete(frontDeskTasks.get(0).getId(), "frontDesk", null);

        //Now doctor has 1 task
        doctorTasks = taskService.getTasksAssignedAsPotentialOwner("doctor", "en-UK");
        Assert.assertEquals(1, doctorTasks.size());

        //No tasks for manager yet
        managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertTrue(managerTasks.isEmpty());


        taskService.start(doctorTasks.get(0).getId(), "doctor");

        taskService.complete(doctorTasks.get(0).getId(), "doctor", null);

        // tasks for manager 
        managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertEquals(1, managerTasks.size());
        taskService.start(managerTasks.get(0).getId(), "manager");

        taskService.complete(managerTasks.get(0).getId(), "manager", null);

        // since persisted process instance is completed it should be null
        process = ksession.getProcessInstance(process.getId());
        Assert.assertNull(process);


        final EntityManager em = emfTasks.createEntityManager();
        Assert.assertEquals(0, em.createQuery("select t from Task t").getResultList().size());
        Assert.assertEquals(0, em.createQuery("select i from I18NText i").getResultList().size());
        Assert.assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_BAs").getResultList().size());
        Assert.assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_ExclOwners").getResultList().size());
        Assert.assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_PotOwners").getResultList().size());
        Assert.assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_Recipients").getResultList().size());
        Assert.assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_Stakeholders").getResultList().size());
        Assert.assertEquals(0, em.createQuery("select c from Content c").getResultList().size());
        em.close();
        emfTasks.close();
    }

    @Test @Ignore
    public void automaticCleanUpTestAbortProcess() throws Exception {

        emfTasks = Persistence.createEntityManagerFactory("org.jbpm.task");
         
        KnowledgeBase kbase = createKnowledgeBase("patient-appointment.bpmn");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);

        TaskServiceEntryPoint taskService = getTaskService(ksession);
        taskService.setUserInfo(userInfo);

        ksession.addEventListener(new TaskCleanUpProcessEventListener(taskService));

        logger.info("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();

        ProcessInstance process = ksession.startProcess("org.jbpm.PatientAppointment", parameters);
        long processInstanceId = process.getId();

        //The process is in the first Human Task waiting for its completion
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());

        //gets frontDesk's tasks
        List<TaskSummary> frontDeskTasks = taskService.getTasksAssignedAsPotentialOwner("frontDesk", "en-UK");
        Assert.assertEquals(1, frontDeskTasks.size());

        //doctor doesn't have any task
        List<TaskSummary> doctorTasks = taskService.getTasksAssignedAsPotentialOwner("doctor", "en-UK");
        Assert.assertTrue(doctorTasks.isEmpty());

        //manager doesn't have any task
        List<TaskSummary> managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertTrue(managerTasks.isEmpty());


        taskService.start(frontDeskTasks.get(0).getId(), "frontDesk");

        taskService.complete(frontDeskTasks.get(0).getId(), "frontDesk", null);

        //Now doctor has 1 task
        doctorTasks = taskService.getTasksAssignedAsPotentialOwner("doctor", "en-UK");
        Assert.assertEquals(1, doctorTasks.size());

        //No tasks for manager yet
        managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertTrue(managerTasks.isEmpty());


        taskService.start(doctorTasks.get(0).getId(), "doctor");

        taskService.complete(doctorTasks.get(0).getId(), "doctor", null);

        // abort process instance
        ksession.abortProcessInstance(processInstanceId);
        // since persisted process instance is completed it should be null
        process = ksession.getProcessInstance(process.getId());
        Assert.assertNull(process);


        final EntityManager em = emfTasks.createEntityManager();

        Assert.assertEquals(0, em.createQuery("select t from Task t").getResultList().size());
        Assert.assertEquals(0, em.createQuery("select i from I18NText i").getResultList().size());
        Assert.assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_BAs").getResultList().size());
        Assert.assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_ExclOwners").getResultList().size());
        Assert.assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_PotOwners").getResultList().size());
        Assert.assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_Recipients").getResultList().size());
        Assert.assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_Stakeholders").getResultList().size());
        Assert.assertEquals(0, em.createQuery("select c from Content c").getResultList().size());
        em.close();
        emfTasks.close();
    }

  
}
