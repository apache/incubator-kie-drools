package org.jbpm.tasks.admin;

import static junit.framework.Assert.fail;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.kie.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.io.impl.ClassPathResource;
import org.jbpm.persistence.util.PersistenceUtil;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.AccessType;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.UserInfo;
import org.jbpm.task.admin.TaskCleanUpProcessEventListener;
import org.jbpm.task.admin.TasksAdmin;
import org.jbpm.task.identity.DefaultUserGroupCallbackImpl;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.DefaultUserInfo;
import org.jbpm.task.service.SendIcal;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.local.LocalTaskService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.SystemEventListenerFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminAPIsWithListenerTest {

    private static Logger logger = LoggerFactory.getLogger(AdminAPIsWithListenerTest.class);
    private HashMap<String, Object> context;
    private EntityManagerFactory emf;
    private EntityManagerFactory emfDomain;
    private EntityManagerFactory emfTasks;
    protected Map<String, User> users;
    protected Map<String, Group> groups;
    protected TaskService taskService;
    protected LocalTaskService localTaskService;
    protected TaskServiceSession taskSession;
    protected UserInfo userInfo;
    protected Properties conf;
    protected TasksAdmin admin;
    @Before
    public void setUp() throws Exception {
        context = setupWithPoolingDataSource("org.jbpm.runtime", false);
        emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);

        conf = new Properties();
        conf.setProperty("mail.smtp.host", "localhost");
        conf.setProperty("mail.smtp.port", "1125");
        conf.setProperty("from", "from@domain.com");
        conf.setProperty("replyTo", "replyTo@domain.com");
        conf.setProperty("defaultLanguage", "en-UK");

        SendIcal.initInstance(conf);

        // Use persistence.xml configuration

        emfTasks = Persistence.createEntityManagerFactory("org.jbpm.task");
        
        admin = new TaskService(emfTasks, SystemEventListenerFactory.getSystemEventListener()).createTaskAdmin();

        UserGroupCallbackManager.getInstance().setCallback(new DefaultUserGroupCallbackImpl("classpath:/usergroups.properties"));

        userInfo = new DefaultUserInfo(null);

        taskService = new TaskService(emfTasks, SystemEventListenerFactory.getSystemEventListener(), null);
        taskSession = taskService.createSession();

        taskService.setUserinfo(userInfo);

        localTaskService = new LocalTaskService(taskService);

    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
        
        if (localTaskService != null) {
            System.out.println("Disposing Local Task Service session");
            localTaskService.disconnect();
        }
        if (taskSession != null) {
            System.out.println("Disposing session");
            taskSession.dispose();
        }
        
        admin.dispose();
        
        if(emfTasks != null && emfTasks.isOpen()){
            emfTasks.close();
        }
    }

    @Test
    public void automaticCleanUpTest() throws Exception {
        


        Environment env = createEnvironment();
        KnowledgeBase kbase = createKnowledgeBase("patient-appointment.bpmn");
        StatefulKnowledgeSession ksession = createSession(kbase, env);
        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
        LocalHTWorkItemHandler htHandler = new LocalHTWorkItemHandler(localTaskService, ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", htHandler);
        ksession.addEventListener(new TaskCleanUpProcessEventListener(admin));
        
        logger.info("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        ProcessInstance process = ksession.startProcess("org.jbpm.PatientAppointment", parameters);
        long processInstanceId = process.getId();

        //The process is in the first Human Task waiting for its completion
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());

        //gets frontDesk's tasks
        List<TaskSummary> frontDeskTasks = this.localTaskService.getTasksAssignedAsPotentialOwner("frontDesk", "en-UK");
        Assert.assertEquals(1, frontDeskTasks.size());

        //doctor doesn't have any task
        List<TaskSummary> doctorTasks = this.localTaskService.getTasksAssignedAsPotentialOwner("doctor", "en-UK");
        Assert.assertTrue(doctorTasks.isEmpty());

        //manager doesn't have any task
        List<TaskSummary> managerTasks = this.localTaskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertTrue(managerTasks.isEmpty());


        this.localTaskService.start(frontDeskTasks.get(0).getId(), "frontDesk");
       
        this.localTaskService.complete(frontDeskTasks.get(0).getId(), "frontDesk", null);

        //Now doctor has 1 task
        doctorTasks = this.localTaskService.getTasksAssignedAsPotentialOwner("doctor", "en-UK");
        Assert.assertEquals(1, doctorTasks.size());

        //No tasks for manager yet
        managerTasks = this.localTaskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertTrue(managerTasks.isEmpty());


        this.localTaskService.start(doctorTasks.get(0).getId(), "doctor");

        this.localTaskService.complete(doctorTasks.get(0).getId(), "doctor", null);

        // tasks for manager 
        managerTasks = this.localTaskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertEquals(1, managerTasks.size());
        this.localTaskService.start(managerTasks.get(0).getId(), "manager");

        this.localTaskService.complete(managerTasks.get(0).getId(), "manager", null);

        // since persisted process instance is completed it should be null
        process = ksession.getProcessInstance(process.getId());
        Assert.assertNull(process);

        Assert.assertEquals(0,emfTasks.createEntityManager().createQuery("select t from Task t").getResultList().size());

    }

    private StatefulKnowledgeSession createSession(KnowledgeBase kbase, Environment env) {
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
    }

    private StatefulKnowledgeSession reloadSession(StatefulKnowledgeSession ksession, KnowledgeBase kbase, Environment env) {
        int sessionId = ksession.getId();
        ksession.dispose();
        return JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
    }

    private KnowledgeBase createKnowledgeBase(String flowFile) {
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        conf.setProperty("drools.dialect.java.compiler", "JANINO");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
        kbuilder.add(new ClassPathResource(flowFile), ResourceType.BPMN2);
        if (kbuilder.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                errorMessage.append(error.getMessage());
                errorMessage.append(System.getProperty("line.separator"));
            }
            fail(errorMessage.toString());
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

    private Environment createEnvironment() {
        Environment env = PersistenceUtil.createEnvironment(context);
        
        return env;
    }

    public Object eval(Reader reader,
            Map vars) {
        try {
            return eval(toString(reader),
                    vars);
        } catch (IOException e) {
            throw new RuntimeException("Exception Thrown",
                    e);
        }
    }

    public String toString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder(1024);
        int charValue;

        while ((charValue = reader.read()) != -1) {
            sb.append((char) charValue);
        }
        return sb.toString();
    }

    public Object eval(String str, Map vars) {
        ExpressionCompiler compiler = new ExpressionCompiler(str.trim());

        ParserContext context = new ParserContext();
        context.addPackageImport("org.jbpm.task");
        context.addPackageImport("org.jbpm.task.service");
        context.addPackageImport("org.jbpm.task.query");
        context.addPackageImport("java.util");

        vars.put("now", new Date());
        return MVEL.executeExpression(compiler.compile(context), vars);
    }

    /**
     * Convert a Map<String, Object> into a ContentData object.
     *
     * @param data
     * @return
     */
    private ContentData prepareContentData(Map data) {
        ContentData contentData = null;
        if (data != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(data);
                out.close();
                contentData = new ContentData();
                contentData.setContent(bos.toByteArray());
                contentData.setAccessType(AccessType.Inline);
            } catch (IOException e) {
                System.err.print(e);
            }
        }

        return contentData;
    }
}
