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

package org.jbpm.test;

import static org.jbpm.test.JBPMHelper.txStateName;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import junit.framework.Assert;

import org.drools.core.audit.WorkingMemoryInMemoryLogger;
import org.drools.core.audit.event.LogEvent;
import org.drools.core.audit.event.RuleFlowNodeLogEvent;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.jbpm.process.audit.AuditLogService;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.services.task.identity.MvelUserGroupCallbackImpl;
import org.jbpm.test.util.PoolingDataSource;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.After;
import org.junit.Before;
import org.kie.api.KieBase;
import org.kie.api.definition.process.Node;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.InternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test case is deprecated. JbpmJUnitBaseTestCase shall be used instead.
 * @see JbpmJUnitBaseTestCase
 */
@Deprecated
public abstract class JbpmJUnitTestCase extends AbstractBaseTest {
    
    private static final Logger testLogger = LoggerFactory.getLogger(JbpmJUnitTestCase.class);

    protected final static String EOL = System.getProperty("line.separator");
    private boolean setupDataSource = false;
    private boolean sessionPersistence = false;
    private EntityManagerFactory emf;
    private PoolingDataSource ds;
    private H2Server server = new H2Server();
    private TaskService taskService;
    private TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
    private WorkingMemoryInMemoryLogger logger;    
    
    private RuntimeManager manager;
    private RuntimeEnvironment environment;
    private AuditLogService logService;

//    @Rule
//    public KnowledgeSessionCleanup ksessionCleanupRule = new KnowledgeSessionCleanup();	
//	protected static ThreadLocal<Set<StatefulKnowledgeSession>> knowledgeSessionSetLocal 
//	    = KnowledgeSessionCleanup.knowledgeSessionSetLocal;
//    @Rule
//    public TestName testName = new TestName();
    
    public JbpmJUnitTestCase() {
        this(false);
    }

    public JbpmJUnitTestCase(boolean setupDataSource) {
        System.setProperty("jbpm.user.group.mapping", "classpath:/usergroups.properties");
        System.setProperty("jbpm.usergroup.callback", "org.jbpm.services.task.identity.DefaultUserGroupCallbackImpl");
        this.setupDataSource = setupDataSource;
    }

    public static PoolingDataSource setupPoolingDataSource() {
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-ds");
        pds.setClassName("org.h2.jdbcx.JdbcDataSource");
        pds.getDriverProperties().put("user", "sa");
        pds.getDriverProperties().put("password", "");
        pds.getDriverProperties().put("url", "jdbc:h2:tcp://localhost/~/jbpm-db;MVCC=true");
        pds.getDriverProperties().put("driverClassName", "org.h2.Driver");
        pds.init();
        return pds;
    }

    public void setPersistence(boolean sessionPersistence) {
        this.sessionPersistence = sessionPersistence;
    }

    public boolean isPersistence() {
        return sessionPersistence;
    }

    @Before
    public void setUp() throws Exception {

        if (setupDataSource) {
            server.start();
            ds = setupPoolingDataSource();
            emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        }
        cleanupSingletonSessionId();        
    }

    @After
    public void tearDown() throws Exception {
        if (setupDataSource) {
            taskService = null;
            if (emf != null) {
                emf.close();
                emf = null;
            }
            if (ds != null) {
                ds.close();
                ds = null;
            }
            server.stop();
            DeleteDbFiles.execute("~", "jbpm-db", true);

            // Clean up possible transactions
            Transaction tx = com.arjuna.ats.jta.TransactionManager.transactionManager().getTransaction();
            if (tx != null) {
                int testTxState = tx.getStatus();
                if (testTxState != Status.STATUS_NO_TRANSACTION
                        && testTxState != Status.STATUS_ROLLEDBACK
                        && testTxState != Status.STATUS_COMMITTED) {
                    try {
                        tx.rollback();
                    } catch (Throwable t) {
                        // do nothing..
                    }
                    Assert.fail("Transaction had status " + txStateName[testTxState] + " at the end of the test.");
                }
            }
        }
        if (manager != null) {
            manager.close();
        }
    }

    protected KieBase createKnowledgeBase(String... process) {
        Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
        for (String p : process) {
            resources.put(p, ResourceType.BPMN2);
        }
        return createKnowledgeBase(resources);
    }

    protected KieBase createKnowledgeBase(Map<String, ResourceType> resources) {
        RuntimeEnvironmentBuilder builder = null;
        if (!setupDataSource){
            builder = RuntimeEnvironmentBuilder.Factory.get()
        			.newEmptyBuilder()
            .addConfiguration("drools.processSignalManagerFactory", DefaultSignalManagerFactory.class.getName())
            .addConfiguration("drools.processInstanceManagerFactory", DefaultProcessInstanceManagerFactory.class.getName());
        } else if (sessionPersistence) {
            builder = RuntimeEnvironmentBuilder.Factory.get()
        			.newDefaultBuilder();
        } else {
            builder = RuntimeEnvironmentBuilder.Factory.get()
        			.newDefaultInMemoryBuilder();       
        }
        builder.userGroupCallback(new JBossUserGroupCallbackImpl("classpath:/usergroups.properties"));
        for (Map.Entry<String, ResourceType> entry : resources.entrySet()) {
            
            builder.addAsset(ResourceFactory.newClassPathResource(entry.getKey()), entry.getValue());
        }
        environment = builder.get();
        return environment.getKieBase();
    }

    protected KieBase createKnowledgeBaseGuvnor(String... packages) throws Exception {
        return createKnowledgeBaseGuvnor(false, "http://localhost:8080/drools-guvnor", "admin", "admin", packages);
    }

    protected KieBase createKnowledgeBaseGuvnorAssets(String pkg, String... assets) throws Exception {
        return createKnowledgeBaseGuvnor(false, "http://localhost:8080/drools-guvnor", "admin", "admin", pkg, assets);
    }

    protected KieBase createKnowledgeBaseGuvnor(boolean dynamic, String url, String username,
            String password, String pkg, String... assets) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        String changeSet =
                "<change-set xmlns='http://drools.org/drools-5.0/change-set'" + EOL
                + "            xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'" + EOL
                + "            xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >" + EOL
                + "    <add>" + EOL;
        for (String a : assets) {
            if (a.indexOf(".bpmn") >= 0) {
                a = a.substring(0, a.indexOf(".bpmn"));
            }
            changeSet += "        <resource source='" + url + "/rest/packages/" + pkg + "/assets/" + a + "/binary' type='BPMN2' basicAuthentication=\"enabled\" username=\"" + username + "\" password=\"" + password + "\" />" + EOL;
        }
        changeSet +=
                "    </add>" + EOL
                + "</change-set>";
        kbuilder.add(ResourceFactory.newByteArrayResource(changeSet.getBytes()), ResourceType.CHANGE_SET);
        return kbuilder.newKieBase();
    }

    protected KieBase createKnowledgeBaseGuvnor(boolean dynamic, String url, String username,
            String password, String... packages) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        String changeSet =
                "<change-set xmlns='http://drools.org/drools-5.0/change-set'" + EOL
                + "            xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'" + EOL
                + "            xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >" + EOL
                + "    <add>" + EOL;
        for (String p : packages) {
            changeSet += "        <resource source='" + url + "/rest/packages/" + p + "/binary' type='PKG' basicAuthentication=\"enabled\" username=\"" + username + "\" password=\"" + password + "\" />" + EOL;
        }
        changeSet +=
                "    </add>" + EOL
                + "</change-set>";
        kbuilder.add(ResourceFactory.newByteArrayResource(changeSet.getBytes()), ResourceType.CHANGE_SET);
        return kbuilder.newKieBase();
    }

    protected KieSession createKnowledgeSession() {
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
                
        KieSession result = runtime.getKieSession();
        if (sessionPersistence) {
            
            logService = new JPAAuditLogService(environment.getEnvironment());               
            
        } else {
            
            logger = new WorkingMemoryInMemoryLogger((StatefulKnowledgeSession) result);
        }
        //knowledgeSessionSetLocal.get().add(result);
        return result;
    }

    protected KieSession createKnowledgeSession(String... process) {
        createKnowledgeBase(process);
        return createKnowledgeSession();
    }

    protected KieSession restoreSession(KieSession ksession, boolean noCache) throws SystemException {
        if (sessionPersistence) {
            manager.close();
            
            return createKnowledgeSession();
        } else {
            return ksession;
        }
    }

//    public StatefulKnowledgeSession loadSession(int id, String... process) {
//        KnowledgeBase kbase = createKnowledgeBase(process);
//
//        final KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
//        // Do NOT use the Pseudo clock yet.. 
//        // config.setOption(ClockTypeOption.get(ClockType.PSEUDO_CLOCK.getId()));
//
//        StatefulKnowledgeSession ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, config, createEnvironment(emf));
//        KnowledgeSessionCleanup.knowledgeSessionSetLocal.get().add(ksession);
//        AuditLoggerFactory.newInstance(Type.JPA, ksession, null);
//
//        return ksession;
//    }

    public Object getVariableValue(String name, long processInstanceId, KieSession ksession) {
        return ((WorkflowProcessInstance) ksession.getProcessInstance(processInstanceId)).getVariable(name);
    }

    public void assertProcessInstanceCompleted(long processInstanceId, KieSession ksession) {
        assertNull(ksession.getProcessInstance(processInstanceId));
    }

    public void assertProcessInstanceAborted(long processInstanceId, KieSession ksession) {
        assertNull(ksession.getProcessInstance(processInstanceId));
    }

    public void assertProcessInstanceActive(long processInstanceId, KieSession ksession) {
        assertNotNull(ksession.getProcessInstance(processInstanceId));
    }

    public void assertNodeActive(long processInstanceId, KieSession ksession, String... name) {
        List<String> names = new ArrayList<String>();
        for (String n : name) {
            names.add(n);
        }
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        if (processInstance instanceof WorkflowProcessInstance) {
            assertNodeActive((WorkflowProcessInstance) processInstance, names);
        }
        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Node(s) not active: " + s);
        }
    }

    private void assertNodeActive(NodeInstanceContainer container, List<String> names) {
        for (NodeInstance nodeInstance : container.getNodeInstances()) {
            String nodeName = nodeInstance.getNodeName();
            if (names.contains(nodeName)) {
                names.remove(nodeName);
            }
            if (nodeInstance instanceof NodeInstanceContainer) {
                assertNodeActive((NodeInstanceContainer) nodeInstance, names);
            }
        }
    }

    public void assertNodeTriggered(long processInstanceId, String... nodeNames) {
        List<String> names = new ArrayList<String>();
        for (String nodeName : nodeNames) {
            names.add(nodeName);
        }
        if (sessionPersistence) {
            List<NodeInstanceLog> logs = logService.findNodeInstances(processInstanceId);
            if (logs != null) {
                for (NodeInstanceLog l : logs) {
                    String nodeName = l.getNodeName();
                    if ((l.getType() == NodeInstanceLog.TYPE_ENTER || l.getType() == NodeInstanceLog.TYPE_EXIT) && names.contains(nodeName)) {
                        names.remove(nodeName);
                    }
                }
            }
        } else {
            for (LogEvent event : logger.getLogEvents()) {
                if (event instanceof RuleFlowNodeLogEvent) {
                    String nodeName = ((RuleFlowNodeLogEvent) event).getNodeName();
                    if (names.contains(nodeName)) {
                        names.remove(nodeName);
                    }
                }
            }
        }
        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Node(s) not executed: " + s);
        }
    }

    protected void clearHistory() {
        if (sessionPersistence) {
            logService.clear();
        } else {
            logger.clear();
        }
    }

    public TestWorkItemHandler getTestWorkItemHandler() {
        return workItemHandler;
    }

    public static class TestWorkItemHandler implements WorkItemHandler {

        private List<WorkItem> workItems = new ArrayList<WorkItem>();

        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            workItems.add(workItem);
        }

        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }

        public WorkItem getWorkItem() {
            if (workItems.size() == 0) {
                return null;
            }
            if (workItems.size() == 1) {
                WorkItem result = workItems.get(0);
                this.workItems.clear();
                return result;
            } else {
                throw new IllegalArgumentException("More than one work item active");
            }
        }

        public List<WorkItem> getWorkItems() {
            List<WorkItem> result = new ArrayList<WorkItem>(workItems);
            workItems.clear();
            return result;
        }
    }

    public void assertProcessVarExists(ProcessInstance process, String... processVarNames) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        List<String> names = new ArrayList<String>();
        for (String nodeName : processVarNames) {
            names.add(nodeName);
        }

        for (String pvar : instance.getVariables().keySet()) {
            if (names.contains(pvar)) {
                names.remove(pvar);
            }
        }

        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Process Variable(s) do not exist: " + s);
        }

    }

    public void assertNodeExists(ProcessInstance process, String... nodeNames) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        List<String> names = new ArrayList<String>();
        for (String nodeName : nodeNames) {
            names.add(nodeName);
        }

        for (Node node : instance.getNodeContainer().getNodes()) {
            if (names.contains(node.getName())) {
                names.remove(node.getName());
            }
        }

        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Node(s) do not exist: " + s);
        }
    }

    public void assertNumOfIncommingConnections(ProcessInstance process, String nodeName, int num) {
        assertNodeExists(process, nodeName);
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        for (Node node : instance.getNodeContainer().getNodes()) {
            if (node.getName().equals(nodeName)) {
                if (node.getIncomingConnections().size() != num) {
                    fail("Expected incomming connections: " + num + " - found " + node.getIncomingConnections().size());
                } else {
                    break;
                }
            }
        }
    }

    public void assertNumOfOutgoingConnections(ProcessInstance process, String nodeName, int num) {
        assertNodeExists(process, nodeName);
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        for (Node node : instance.getNodeContainer().getNodes()) {
            if (node.getName().equals(nodeName)) {
                if (node.getOutgoingConnections().size() != num) {
                    fail("Expected outgoing connections: " + num + " - found " + node.getOutgoingConnections().size());
                } else {
                    break;
                }
            }
        }
    }

    public void assertVersionEquals(ProcessInstance process, String version) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if (!instance.getWorkflowProcess().getVersion().equals(version)) {
            fail("Expected version: " + version + " - found " + instance.getWorkflowProcess().getVersion());
        }
    }

    public void assertProcessNameEquals(ProcessInstance process, String name) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if (!instance.getWorkflowProcess().getName().equals(name)) {
            fail("Expected name: " + name + " - found " + instance.getWorkflowProcess().getName());
        }
    }

    public void assertPackageNameEquals(ProcessInstance process, String packageName) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if (!instance.getWorkflowProcess().getPackageName().equals(packageName)) {
            fail("Expected package name: " + packageName + " - found " + instance.getWorkflowProcess().getPackageName());
        }
    }

    public TaskService getTaskService() {
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        return runtime.getTaskService();
    }

    public TaskService getService() {
        return (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
        		.userGroupCallback(new MvelUserGroupCallbackImpl(true))
                .entityManagerFactory(emf)
                .getTaskService();
    }
    private static class H2Server {

        private Server server;

        public synchronized void start() {
            if (server == null || !server.isRunning(false)) {
                try {
                    DeleteDbFiles.execute("~", "jbpm-db", true);
                    server = Server.createTcpServer(new String[0]);
                    server.start();
                } catch (SQLException e) {
                    throw new RuntimeException("Cannot start h2 server database", e);
                }
            }
        }

        public void stop() {
            if (server != null) {
                server.stop();
                server.shutdown();
                DeleteDbFiles.execute("~", "jbpm-db", true);
                server = null;
            }
        }
    }

    public PoolingDataSource getDs() {
        return ds;
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }
    

    public static void cleanupSingletonSessionId() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (tempDir.exists()) {
            
            String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {
                
                @Override
                public boolean accept(File dir, String name) {
                    
                    return name.endsWith("-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {
                
                new File(tempDir, file).delete();
            }
        }
    }
}
