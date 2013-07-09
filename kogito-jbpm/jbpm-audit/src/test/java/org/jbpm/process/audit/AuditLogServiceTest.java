/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.process.audit;

import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.createEnvironment;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.drools.core.io.impl.ClassPathResource;
import org.jbpm.process.audit.AuditLoggerFactory.Type;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class tests the following classes: 
 * <ul>
 * <li>JPAWorkingMemoryDbLogger</li>
 * <li>JPAProcessInstanceDbLog</li>
 * </ul>
 */
public class AuditLogServiceTest {

    private HashMap<String, Object> context;
    private Logger logger = LoggerFactory.getLogger(AuditLogServiceTest.class);

    @Before
    public void setUp() throws Exception {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
    }

    @Test
    public void testLogger1() throws Exception {
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        Environment env = createEnvironment(context);
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        StatefulKnowledgeSession session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
        AbstractAuditLogger dblogger = AuditLoggerFactory.newInstance(Type.JPA, session, null);
        assertNotNull(dblogger);
        assertTrue(dblogger instanceof JPAWorkingMemoryDbLogger);
        AuditLogService auditLogService = new JPAAuditLogService(env);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        assertEquals(initialProcessInstanceSize + 1, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        logger.debug(processInstance.toString() 
        + " -> " + processInstance.getStart() + " - " + processInstance.getEnd());
        assertNotNull(processInstance.getStart());
        assertNotNull("ProcessInstanceLog does not contain end date.", processInstance.getEnd());
        assertEquals(processInstanceId, processInstance.getProcessInstanceId());
        assertEquals("com.sample.ruleflow", processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = auditLogService.findNodeInstances(processInstanceId);
        assertEquals(6, nodeInstances.size());
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            logger.debug(nodeInstance.toString());
            assertEquals(processInstanceId, processInstance.getProcessInstanceId());
            assertEquals("com.sample.ruleflow", processInstance.getProcessId());
            assertNotNull(nodeInstance.getDate());
        }
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        assertTrue(processInstances.isEmpty());
    }
    
    @Test
    public void testLogger2() {
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        Environment env = createEnvironment(context);
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        StatefulKnowledgeSession session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
        AbstractAuditLogger dblogger = AuditLoggerFactory.newInstance(Type.JPA, session, null);
        assertNotNull(dblogger);
        assertTrue(dblogger instanceof JPAWorkingMemoryDbLogger);
        AuditLogService auditLogServiceBean = new JPAAuditLogService(env);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances =
            auditLogServiceBean.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        session.startProcess("com.sample.ruleflow");
        session.startProcess("com.sample.ruleflow");
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow'");
        processInstances = auditLogServiceBean.findProcessInstances("com.sample.ruleflow");
        assertEquals(initialProcessInstanceSize + 2, processInstances.size());
        for (ProcessInstanceLog processInstance: processInstances) {
            logger.debug(processInstance.toString()
            + " -> " + processInstance.getStart() + " - " + processInstance.getEnd());
            List<NodeInstanceLog> nodeInstances = auditLogServiceBean.findNodeInstances(processInstance.getProcessInstanceId());
            for (NodeInstanceLog nodeInstance: nodeInstances) {
                logger.debug(nodeInstance.toString()
              + " -> " + nodeInstance.getDate());
            }
            assertEquals(6, nodeInstances.size());
        }
        auditLogServiceBean.clear();
    }
    
    @Test
    public void testLogger3() {
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        Environment env = createEnvironment(context);
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        StatefulKnowledgeSession session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
        AbstractAuditLogger dblogger = AuditLoggerFactory.newInstance(Type.JPA, session, null);
        assertNotNull(dblogger);
        assertTrue(dblogger instanceof JPAWorkingMemoryDbLogger);
        AuditLogService auditLogService = new JPAAuditLogService(env);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow2").getId();
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow2'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow2");
        assertEquals(initialProcessInstanceSize + 1, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        logger.debug(processInstance.toString() 
        + " -> " + processInstance.getStart() + " - " + processInstance.getEnd());
        assertNotNull(processInstance.getStart());
        assertNotNull("ProcessInstanceLog does not contain end date.", processInstance.getEnd());
        assertEquals(processInstanceId, processInstance.getProcessInstanceId());
        assertEquals("com.sample.ruleflow2", processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = auditLogService.findNodeInstances(processInstanceId);
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            logger.debug(nodeInstance.toString()
            + " -> " + nodeInstance.getDate());
            assertEquals(processInstanceId, processInstance.getProcessInstanceId());
            assertEquals("com.sample.ruleflow2", processInstance.getProcessId());
            assertNotNull(nodeInstance.getDate());
        }
        assertEquals(14, nodeInstances.size());
        auditLogService.clear();
    }
    
    @Test
    public void testLogger4() throws Exception {
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        Environment env = createEnvironment(context);
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        StatefulKnowledgeSession session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
        AbstractAuditLogger dblogger = AuditLoggerFactory.newInstance(Type.JPA, session, null);
        assertNotNull(dblogger);
        assertTrue(dblogger instanceof JPAWorkingMemoryDbLogger);
        AuditLogService auditLogService = new JPAAuditLogService(env);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new WorkItemHandler() {
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                Map<String, Object> results = new HashMap<String, Object>();
                results.put("Result", "ResultValue");
                manager.completeWorkItem(workItem.getId(), results);
            }
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            }
        });
        
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> list = new ArrayList<String>();
        list.add("One");
        list.add("Two");
        list.add("Three");
        params.put("list", list);
        long processInstanceId = session.startProcess("com.sample.ruleflow3", params).getId();
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow3'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        assertEquals(initialProcessInstanceSize + 1, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        logger.debug(processInstance.toString() + " -> " + processInstance.getStart() + " - " + processInstance.getEnd());
        assertNotNull(processInstance.getStart());
        assertNotNull("ProcessInstanceLog does not contain end date.", processInstance.getEnd());
        assertEquals(processInstanceId, processInstance.getProcessInstanceId());
        assertEquals("com.sample.ruleflow3", processInstance.getProcessId());
        List<VariableInstanceLog> variableInstances = auditLogService.findVariableInstances(processInstanceId);
        assertEquals(6, variableInstances.size());
        for (VariableInstanceLog variableInstance: variableInstances) {
            logger.debug(variableInstance.toString());
            assertEquals(processInstanceId, processInstance.getProcessInstanceId());
            assertEquals("com.sample.ruleflow3", processInstance.getProcessId());
            assertNotNull(variableInstance.getDate());
        }
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        assertTrue(processInstances.isEmpty());
    }
    
    @Test
    public void testLogger4LargeVariable() throws Exception {
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        Environment env = createEnvironment(context);
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        StatefulKnowledgeSession session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
        AbstractAuditLogger dblogger = AuditLoggerFactory.newInstance(Type.JPA, session, null);
        assertNotNull(dblogger);
        assertTrue(dblogger instanceof JPAWorkingMemoryDbLogger);
        AuditLogService auditLogService = new JPAAuditLogService(env);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new WorkItemHandler() {
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                Map<String, Object> results = new HashMap<String, Object>();
                results.put("Result", "ResultValue");
                manager.completeWorkItem(workItem.getId(), results);
            }
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            }
        });
        
        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> list = new ArrayList<String>();
        list.add("One");
        list.add("Two");
        String three = "";
        for (int i = 0; i < 1024; i++) {
            three += "*";
        }
        list.add(three);
        params.put("list", list);
        long processInstanceId = session.startProcess("com.sample.ruleflow3", params).getId();
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow3'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        int expected = initialProcessInstanceSize + 1; 
        assertEquals("[Expected " + expected + " ProcessInstanceLog instances, not " + processInstances.size() + "]",  
                expected, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        logger.debug(processInstance.toString()
        + " -> " + processInstance.getStart() + " - " + processInstance.getEnd());
        assertNotNull(processInstance.getStart());
        assertNotNull("ProcessInstanceLog does not contain end date.", processInstance.getEnd());
        assertEquals(processInstanceId, processInstance.getProcessInstanceId());
        assertEquals("com.sample.ruleflow3", processInstance.getProcessId());
        List<VariableInstanceLog> variableInstances = auditLogService.findVariableInstances(processInstanceId);
        assertEquals(6, variableInstances.size());
        for (VariableInstanceLog variableInstance: variableInstances) {
            logger.debug(variableInstance.toString());
            assertEquals(processInstanceId, processInstance.getProcessInstanceId());
            assertEquals("com.sample.ruleflow3", processInstance.getProcessId());
            assertNotNull(variableInstance.getDate());
        }
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow3");
        assertTrue(processInstances.isEmpty());
    }
    
    
    @Test
    public void testLogger5() throws Exception {
        
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        Environment env = createEnvironment(context);
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        StatefulKnowledgeSession session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
        AbstractAuditLogger dblogger = AuditLoggerFactory.newInstance(Type.JPA, session, null);
        assertNotNull(dblogger);
        assertTrue(dblogger instanceof JPAWorkingMemoryDbLogger);
        AuditLogService auditLogService = new JPAAuditLogService(env);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        assertEquals(initialProcessInstanceSize + 1, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        logger.debug(processInstance.toString() 
        + " -> " + processInstance.getStart() + " - " + processInstance.getEnd());
        assertNotNull(processInstance.getStart());
        assertNotNull(processInstance.getEnd());
        assertEquals(processInstanceId, processInstance.getProcessInstanceId());
        assertEquals("com.sample.ruleflow", processInstance.getProcessId());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getStatus().intValue());
        List<NodeInstanceLog> nodeInstances = auditLogService.findNodeInstances(processInstanceId);
        assertEquals(6, nodeInstances.size());
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            logger.debug(nodeInstance.toString());
            assertEquals(processInstanceId, processInstance.getProcessInstanceId());
            assertEquals("com.sample.ruleflow", processInstance.getProcessId());
            assertNotNull(nodeInstance.getDate());
        }
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        assertTrue(processInstances.isEmpty());
    }
    
    @Test
    public void testLoggerWithEMF() throws Exception {
        
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        Environment env = createEnvironment(context);
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        StatefulKnowledgeSession session = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
        AbstractAuditLogger dblogger = AuditLoggerFactory.newJPAInstance((EntityManagerFactory) env.get(EnvironmentName.ENTITY_MANAGER_FACTORY));
        assertNotNull(dblogger);
        assertTrue(dblogger instanceof JPAWorkingMemoryDbLogger);
        session.addEventListener(dblogger);
        AuditLogService auditLogService = new JPAAuditLogService(env);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // record the initial count to compare to later
        List<ProcessInstanceLog> processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        int initialProcessInstanceSize = processInstances.size();
        
        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        
        logger.debug("Checking process instances for process 'com.sample.ruleflow'");
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        assertEquals(initialProcessInstanceSize + 1, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(initialProcessInstanceSize);
        logger.debug(processInstance.toString() 
        + " -> " + processInstance.getStart() + " - " + processInstance.getEnd());
        assertNotNull(processInstance.getStart());
        assertNotNull("ProcessInstanceLog does not contain end date.", processInstance.getEnd());
        assertEquals(processInstanceId, processInstance.getProcessInstanceId());
        assertEquals("com.sample.ruleflow", processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = auditLogService.findNodeInstances(processInstanceId);
        assertEquals(6, nodeInstances.size());
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            logger.debug(nodeInstance.toString());
            assertEquals(processInstanceId, processInstance.getProcessInstanceId());
            assertEquals("com.sample.ruleflow", processInstance.getProcessId());
            assertNotNull(nodeInstance.getDate());
        }
        auditLogService.clear();
        processInstances = auditLogService.findProcessInstances("com.sample.ruleflow");
        assertTrue(processInstances.isEmpty());
    }
    
    private KnowledgeBase createKnowledgeBase() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ClassPathResource("ruleflow.rf"), ResourceType.DRF);
        kbuilder.add(new ClassPathResource("ruleflow2.rf"), ResourceType.DRF);
        kbuilder.add(new ClassPathResource("ruleflow3.rf"), ResourceType.DRF);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }
}
