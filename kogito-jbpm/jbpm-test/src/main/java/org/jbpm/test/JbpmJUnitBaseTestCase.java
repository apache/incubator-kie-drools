/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.drools.core.audit.WorkingMemoryInMemoryLogger;
import org.drools.core.audit.event.LogEvent;
import org.drools.core.audit.event.RuleFlowNodeLogEvent;
import org.jbpm.process.audit.AuditLogService;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.kie.api.definition.process.Node;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.Context;
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
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * Base test case class that shall be used for jBPM related tests. It provides four sections:
 * <ul>
 *  <li>JUnit life cycle methods</li>
 *  <li>Knowledge Base and KnowledgeSession management methods</li>
 *  <li>Assertions</li>
 *  <li>Helper methods</li>
 * </ul>
 * <b>JUnit life cycle methods</b>:<br/>
 * * setUp: executed @Before and configures data source and <code>EntityManagerFactory</code>, cleans up Singleton's session id<br/>
 * * tearDown: executed @After and clears out history, closes <code>EntityManagerFactory</code> and data source, disposes <code>RuntimeEngine</code>'s and <code>RuntimeManager</code><br/>
 * <br/>
 * <b>KnowledgeBase and KnowledgeSession management methods</b>
 * * createRuntimeManager creates <code>RuntimeManager</code> for gives set of assets and selected strategy 
 * <br/>
 * * disposeRuntimeManager disposes <code>RuntimeManager</code> currently active in the scope of test
 * <br/>
 * * getRuntimeEngine creates new <code>RuntimeEngine</code> for given context<br/>
 * <br/>
 * <b>Assertions</b><br/>
 * Set of useful methods to assert process instance at various stages.
 * <br/>
 * <b>Helper methods</b><br/>
 * * getDs - returns currently configured data source<br/>
 * * getEmf - returns currently configured <code>EntityManagerFactory</code><br/>
 * * getTestWorkItemHandler - returns test work item handler that might be registered in addition to what is registered by default<br/>
 * * clearHistory - clears history log<br/>
 * * setupPoolingDataSource - sets up data source<br/>
 */
public abstract class JbpmJUnitBaseTestCase extends Assert {
    
    /**
     * Currently supported RuntimeEngine strategies
     */
    public enum Strategy {
        SINGLETON,
        REQUEST,
        PROCESS_INSTANCE;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(JbpmJUnitBaseTestCase.class);
  
    protected boolean setupDataSource = false;
    protected boolean sessionPersistence = false;
    private String persistenceUnitName;
    
    private EntityManagerFactory emf;
    private PoolingDataSource ds;
    
    private TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
    
    private RuntimeManagerFactory managerFactory = RuntimeManagerFactory.Factory.get();
    protected RuntimeManager manager;

    private AuditLogService logService;
    private WorkingMemoryInMemoryLogger inMemoryLogger;    
   
    protected List<RuntimeEngine> activeEngines = new ArrayList<RuntimeEngine>();

    /**
     * The most simple test case configuration:
     * <ul>
     *  <li>does NOT initialize data source</li>
     *  <li>does NOT configure session persistence</li>
     * </ul>
     * This is usually used for in memory process management, without human task interaction.
     */
    public JbpmJUnitBaseTestCase() {
        this(false, false, "org.jbpm.persistence.jpa");
    }

    /**
     * Allows to explicitly configure persistence and data source. This is the most common way of
     * bootstrapping test cases for jBPM.<br/> 
     * Use following configuration to execute in memory process management with human tasks persistence <br/>
     * <code>
     * super(true, false);
     * </code>
     * <br/>
     * Use following configuration to execute in persistent process management with human tasks persistence <br/>
     * <code>
     * super(true, true);
     * </code>
     * <br/>
     * This will use default persistence unit name <code>org.jbpm.persistence.jpa</code>
     * @param setupDataSource - true to configure data source under JNDI name: jdbc/jbpm-ds
     * @param sessionPersistence - configures RuntimeEngine to be with JPA persistence enabled
     */
    public JbpmJUnitBaseTestCase(boolean setupDataSource, boolean sessionPersistence) {
        this(setupDataSource, sessionPersistence, "org.jbpm.persistence.jpa");
    }
    
    /**
     * Same as {@link #JbpmJUnitBaseTestCase(boolean, boolean)} but allows to use another persistence unit name.
     * @param setupDataSource - true to configure data source under JNDI name: jdbc/jbpm-ds
     * @param sessionPersistence - configures RuntimeEngine to be with JPA persistence enabled
     * @param persistenceUnitName - custom persistence unit name
     */
    public JbpmJUnitBaseTestCase(boolean setupDataSource, boolean sessionPersistence, String persistenceUnitName) {
        this.setupDataSource = setupDataSource;
        this.sessionPersistence = sessionPersistence;
        this.persistenceUnitName = persistenceUnitName;
        if (!this.setupDataSource && this.sessionPersistence) {
            throw new IllegalArgumentException("Unsupported configuration, cannot enable sessionPersistence when setupDataSource is disabled");
        }
        logger.info("Configuring entire test case to have data source enabled {} and session persistence enabled {} with persistence unit name {}",
                this.setupDataSource, this.sessionPersistence, this.persistenceUnitName);
    }


    @Before
    public void setUp() throws Exception {

        if (setupDataSource) {
            ds = setupPoolingDataSource();
            logger.debug("Data source configured with unique id {}", ds.getUniqueName());
            emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        }
        cleanupSingletonSessionId();
        
    }

    @After
    public void tearDown() throws Exception {
        clearHistory();
        disposeRuntimeManager();
        if (setupDataSource) {
            if (emf != null) {
                emf.close();
                emf = null;
            }
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }        
    }

    /**
     * Creates default configuration of <code>RuntimeManager</code> with SINGLETON strategy and all 
     * <code>processes</code> being added to knowledge base.
     * <br/>
     * There should be only one <code>RuntimeManager</code> created during single test.
     * @param process - processes that shall be added to knowledge base
     * @return new instance of RuntimeManager
     */
    protected RuntimeManager createRuntimeManager(String... process) {
        return createRuntimeManager(Strategy.SINGLETON, null, process);
    }
    
    /**
     * Creates default configuration of <code>RuntimeManager</code> with given <code>strategy</code> and all 
     * <code>processes</code> being added to knowledge base.
     * <br/>
     * There should be only one <code>RuntimeManager</code> created during single test.
     * @param strategy - selected strategy of those that are supported
     * @param identifier - identifies the runtime manager
     * @param process - processes that shall be added to knowledge base
     * @return new instance of RuntimeManager
     */
    protected RuntimeManager createRuntimeManager(Strategy strategy, String identifier, String... process) {
        Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
        for (String p : process) {
            resources.put(p, ResourceType.BPMN2);
        }
        return createRuntimeManager(strategy, resources, identifier);
    }

    /**
     * Creates default configuration of <code>RuntimeManager</code> with SINGLETON strategy and all 
     * <code>resources</code> being added to knowledge base.
     * <br/>
     * There should be only one <code>RuntimeManager</code> created during single test.
     * @param resources - resources (processes, rules, etc) that shall be added to knowledge base
     * @return new instance of RuntimeManager
     */
    protected RuntimeManager createRuntimeManager(Map<String, ResourceType> resources) {
        return createRuntimeManager(Strategy.SINGLETON, resources, null);
    }

    /**
     * Creates default configuration of <code>RuntimeManager</code> with SINGLETON strategy and all 
     * <code>resources</code> being added to knowledge base.
     * <br/>
     * There should be only one <code>RuntimeManager</code> created during single test.
     * @param resources - resources (processes, rules, etc) that shall be added to knowledge base
     * @param identifier - identifies the runtime manager
     * @return new instance of RuntimeManager
     */
    protected RuntimeManager createRuntimeManager(Map<String, ResourceType> resources, String identifier) {
        return createRuntimeManager(Strategy.SINGLETON, resources, identifier);
    }
    
    /**
     * Creates default configuration of <code>RuntimeManager</code> with given <code>strategy</code> and all 
     * <code>resources</code> being added to knowledge base.
     * <br/>
     * There should be only one <code>RuntimeManager</code> created during single test.
     * @param strategy - selected strategy of those that are supported
     * @param resources - resources that shall be added to knowledge base
     * @return new instance of RuntimeManager
     */
    protected RuntimeManager createRuntimeManager(Strategy strategy, Map<String, ResourceType> resources) {
        return createRuntimeManager(strategy, resources, null);
    }
    
    /**
     * Creates default configuration of <code>RuntimeManager</code> with given <code>strategy</code> and all 
     * <code>resources</code> being added to knowledge base.
     * <br/>
     * There should be only one <code>RuntimeManager</code> created during single test.
     * @param strategy - selected strategy of those that are supported
     * @param resources - resources that shall be added to knowledge base
     * @param identifier - identifies the runtime manager
     * @return new instance of RuntimeManager
     */
    protected RuntimeManager createRuntimeManager(Strategy strategy, Map<String, ResourceType> resources, String identifier) {
        if (manager != null) {
            throw new IllegalStateException("There is already one RuntimeManager active");
        }
        
        RuntimeEnvironmentBuilder builder = null;
        if (!setupDataSource){
            builder = RuntimeEnvironmentBuilder.Factory.get()
        			.newEmptyBuilder()
            .addConfiguration("drools.processSignalManagerFactory", DefaultSignalManagerFactory.class.getName())
            .addConfiguration("drools.processInstanceManagerFactory", DefaultProcessInstanceManagerFactory.class.getName());
        } else if (sessionPersistence) {
            builder = RuntimeEnvironmentBuilder.Factory.get()
        			.newDefaultBuilder()
            .entityManagerFactory(emf);
        } else {
            builder = RuntimeEnvironmentBuilder.Factory.get()
        			.newDefaultInMemoryBuilder();       
        }
        builder.userGroupCallback(new JBossUserGroupCallbackImpl("classpath:/usergroups.properties"));
        
        for (Map.Entry<String, ResourceType> entry : resources.entrySet()) {            
            builder.addAsset(ResourceFactory.newClassPathResource(entry.getKey()), entry.getValue());
        }
        
        return createRuntimeManager(strategy, resources, builder.get(), identifier);
    }
    
    /**
     * The lowest level of creation of <code>RuntimeManager</code> that expects to get <code>RuntimeEnvironment</code>
     * to be given as argument. It does not assume any particular configuration as it's considered manual creation
     * that allows to configure every single piece of <code>RuntimeManager</code>. <br/>
     * Use this only when you know what you do!
     * @param strategy - selected strategy of those that are supported
     * @param resources - resources that shall be added to knowledge base 
     * @param environment - runtime environment used for <code>RuntimeManager</code> creation
     * @param identifier - identifies the runtime manager
     * @return new instance of RuntimeManager
     */
    protected RuntimeManager createRuntimeManager(Strategy strategy, Map<String, ResourceType> resources, RuntimeEnvironment environment, String identifier) {
        if (manager != null) {
            throw new IllegalStateException("There is already one RuntimeManager active");
        }

        switch (strategy) {
        case SINGLETON:
            if (identifier == null) {
                manager = managerFactory.newSingletonRuntimeManager(environment);
            } else {
                manager = managerFactory.newSingletonRuntimeManager(environment, identifier);
            }
            break;
        case REQUEST:
            if (identifier == null) {
                manager = managerFactory.newPerRequestRuntimeManager(environment);   
            } else {  
                manager = managerFactory.newPerRequestRuntimeManager(environment, identifier);
            }
            break;
        case PROCESS_INSTANCE:
            if (identifier == null) {
                manager = managerFactory.newPerProcessInstanceRuntimeManager(environment);
            } else {
                manager = managerFactory.newPerProcessInstanceRuntimeManager(environment, identifier);
            }
            break;
        default:
            if (identifier == null) {
                manager = managerFactory.newSingletonRuntimeManager(environment);
            } else {
                manager = managerFactory.newSingletonRuntimeManager(environment, identifier);
            }
            break;
        }
         
        return manager;
    }

    /**
     * Disposes currently active (in scope of a test) <code>RuntimeManager</code> together with all
     * active <code>RuntimeEngine</code>'s that were created (in scope of a test). Usual use case is 
     * to simulate system shutdown.
     */
    protected void disposeRuntimeManager() {
        if (!activeEngines.isEmpty()) {
            for (RuntimeEngine engine : activeEngines) {
                manager.disposeRuntimeEngine(engine);
            }
            activeEngines.clear();
        }
        if (manager != null) {
            manager.close();
            manager = null;
        }
    }

    /**
     * Returns new <code>RuntimeEngine</code> built from the manager of this test case.
     * It uses <code>EmptyContext</code> that is suitable for following strategies:
     * <ul>
     *  <li>Singleton</li>
     *  <li>Request</li>
     * </ul>
     * @see #getRuntimeEngine(Context)
     * @return new RuntimeEngine instance
     */
    protected RuntimeEngine getRuntimeEngine() {
        return getRuntimeEngine(EmptyContext.get());
    }
    
    /**
     * Returns new <code>RuntimeEngine</code> built from the manager of this test case. Common use case is to maintain 
     * same session for process instance and thus <code>ProcessInstanceIdContext</code> shall be used.
     * @param context - instance of the context that shall be used to create <code>RuntimeManager</code>
     * @return new RuntimeEngine instance
     */
    protected RuntimeEngine getRuntimeEngine(Context<?> context) {
        if (manager == null) {
            throw new IllegalStateException("RuntimeManager is not initialized, did you forgot to create it?");
        }
         
        RuntimeEngine runtimeEngine = manager.getRuntimeEngine(context);
        activeEngines.add(runtimeEngine);
        if (sessionPersistence) {            
            logService = new JPAAuditLogService(runtimeEngine.getKieSession().getEnvironment());               
            
        } else {            
            inMemoryLogger = new WorkingMemoryInMemoryLogger((StatefulKnowledgeSession) runtimeEngine.getKieSession());
        }
        
        return runtimeEngine;
    }

    /**
     * Retrieves value of the variable given by <code>name</code> from process instance given by <code>processInstanceId</code>
     * using given session.
     * @param name - name of the variable
     * @param processInstanceId - id of process instance
     * @param ksession - ksession used to retrieve the value
     * @return returns variable value or null if there is no such variable
     */
    public Object getVariableValue(String name, long processInstanceId, KieSession ksession) {
        return ((WorkflowProcessInstance) ksession.getProcessInstance(processInstanceId)).getVariable(name);
    }

    /*
     * ****************************************
     * *********** assert methods *************
     * ****************************************
     */
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
            for (LogEvent event : inMemoryLogger.getLogEvents()) {
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

    /*
     * ****************************************
     * *********** helper methods *************
     * ****************************************
     */
    
    protected EntityManagerFactory getEmf() {
        return this.emf;
    }
    
    protected DataSource getDs() {
        return this.ds;
    }

    protected PoolingDataSource setupPoolingDataSource() {
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-ds");
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setMaxPoolSize(5);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", "sa");
        pds.getDriverProperties().put("password", "");
        pds.getDriverProperties().put("url", "jdbc:h2:mem:jbpm-db;MVCC=true");
        pds.getDriverProperties().put("driverClassName", "org.h2.Driver");
        pds.init();
        return pds;
    }
    
    protected void clearHistory() {
        if (sessionPersistence && logService != null) {
            logService.clear();
        } else if (inMemoryLogger != null) {
            inMemoryLogger.clear();
        }
    }
    
    protected TestWorkItemHandler getTestWorkItemHandler() {
        return workItemHandler;
    }
    
    protected AuditLogService getLogService() {
        return logService;
    }
    
    protected WorkingMemoryInMemoryLogger getInMemoryLogger() {
        return inMemoryLogger;
    }

    protected static class TestWorkItemHandler implements WorkItemHandler {

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

    protected static void cleanupSingletonSessionId() {
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
