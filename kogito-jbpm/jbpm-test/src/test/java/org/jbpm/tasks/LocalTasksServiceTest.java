package org.jbpm.tasks;

import org.jbpm.persistence.*;
import static org.drools.persistence.util.PersistenceUtil.*;
import static org.drools.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;
import static junit.framework.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.drools.marshalling.util.MarshallingTestUtil;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy;
import org.drools.persistence.util.PersistenceUtil;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

import org.jbpm.persistence.objects.MockUserInfo;

import org.jbpm.process.workitem.wsht.SyncWSHumanTaskHandler;
import org.jbpm.task.*;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.*;
import org.jbpm.task.service.local.LocalTaskService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalTasksServiceTest {

    private static Logger logger = LoggerFactory.getLogger(LocalTasksServiceTest.class);
    private HashMap<String, Object> context;
    private EntityManagerFactory emf;
    private EntityManagerFactory emfDomain;
    private EntityManagerFactory emfTasks;
    protected Map<String, User> users;
    protected Map<String, Group> groups;
    protected TaskService taskService;
    protected LocalTaskService localTaskService;
    protected TaskServiceSession taskSession;
    protected MockUserInfo userInfo;
    protected Properties conf;

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


        emfTasks = Persistence.createEntityManagerFactory("org.jbpm.task");
        Reader reader = null;
        Map vars = new HashMap();
        try {
            reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("LoadUsers.mvel"));
            users = (Map<String, User>) eval(reader, vars);
        } finally {
            if (reader != null) {
                reader.close();
            }
            reader = null;
        }

        try {
            reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("LoadGroups.mvel"));
            groups = (Map<String, Group>) eval(reader, vars);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        userInfo = new MockUserInfo();

        taskService = new TaskService(emfTasks, SystemEventListenerFactory.getSystemEventListener(), null);
        taskSession = taskService.createSession();

        taskService.setUserinfo(userInfo);

        for (User user : users.values()) {
            taskSession.addUser(user);
        }

        for (Group group : groups.values()) {
            taskSession.addGroup(group);
        }
        UserGroupCallbackManager.getInstance().setCallback(null);
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
        if (emfTasks != null && emfTasks.isOpen()) {
            emfTasks.close();
        }
    }

    @Test
    public void groupTaskQueryTest() throws Exception {


        Environment env = createEnvironment();
        KnowledgeBase kbase = createKnowledgeBase("Evaluation2.bpmn");
        StatefulKnowledgeSession ksession = createSession(kbase, env);
        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
        SyncWSHumanTaskHandler htHandler = new SyncWSHumanTaskHandler(localTaskService, ksession);
        htHandler.setLocal(true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", htHandler);
        logger.info("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("employee", "salaboy");
        ProcessInstance process = ksession.startProcess("com.sample.evaluation", parameters);
        long processInstanceId = process.getId();

        //The process is in the first Human Task waiting for its completion
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());

        //gets salaboy's tasks
        List<TaskSummary> salaboysTasks = this.localTaskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        Assert.assertEquals(1, salaboysTasks.size());


        this.localTaskService.start(salaboysTasks.get(0).getId(), "salaboy");

        this.localTaskService.complete(salaboysTasks.get(0).getId(), "salaboy", null);
        List<String> pms = new ArrayList<String>();
        pms.add("PM");
        List<TaskSummary> pmsTasks = this.localTaskService.getTasksAssignedAsPotentialOwner(null, pms, "en-UK");

        Assert.assertEquals(1, pmsTasks.size());

        pmsTasks = this.localTaskService.getTasksAssignedAsPotentialOwner("", pms, "en-UK");

        Assert.assertEquals(1, pmsTasks.size());

        pmsTasks = this.localTaskService.getTasksAssignedAsPotentialOwner("john", pms, "en-UK");

        Assert.assertEquals(1, pmsTasks.size());

        List<String> hrs = new ArrayList<String>();
        hrs.add("HR");
        List<TaskSummary> hrsTasks = this.localTaskService.getTasksAssignedAsPotentialOwner(null, hrs, "en-UK");

        Assert.assertEquals(1, hrsTasks.size());

        hrsTasks = this.localTaskService.getTasksAssignedAsPotentialOwner("", hrs, "en-UK");

        Assert.assertEquals(1, hrsTasks.size());

        hrsTasks = this.localTaskService.getTasksAssignedAsPotentialOwner("mary", hrs, "en-UK");

        Assert.assertEquals(1, hrsTasks.size());






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
