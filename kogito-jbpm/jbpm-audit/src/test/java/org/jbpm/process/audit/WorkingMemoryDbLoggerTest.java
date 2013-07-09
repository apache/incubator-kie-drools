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

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.drools.compiler.compiler.PackageBuilder;
import org.drools.core.RuleBase;
import org.drools.core.RuleBaseFactory;
import org.drools.core.SessionConfiguration;
import org.drools.core.StatefulSession;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.rule.Package;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;

/**
 * This class tests the following classes: 
 * <ul>
 * <li>WorkingMemoryDbLogger</li>
 * <li>ProcessInstanceDbLog</li>
 * </ul>
 */
public class WorkingMemoryDbLoggerTest extends AbstractBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkingMemoryDbLoggerTest.class);
    private HashMap<String, Object> context;
    private AuditLogService logService;

    @Before
    public void setUp() throws Exception {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
        Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, context.get(EnvironmentName.ENTITY_MANAGER_FACTORY));
        logService = new JPAAuditLogService(env);
    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
        logService.dispose();
    }
    
    @Test
	public void testLogger1() {
        // load the process
        RuleBase ruleBase = createKnowledgeBase();
        // create a new session
        Properties properties = new Properties();
		properties.put("drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
		properties.put("drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
		SessionConfiguration config = new SessionConfiguration(properties);
        StatefulSession session = ruleBase.newStatefulSession(config, createEnvironment(context));
        new JPAWorkingMemoryDbLogger(session);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        
        logger.info("Checking process instances for process 'com.sample.ruleflow'");
        List<ProcessInstanceLog> processInstances =
        	logService.findProcessInstances("com.sample.ruleflow");
        assertEquals(1, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(0);
        logger.info("{}", processInstance);
        assertNotNull(processInstance.getStart());
        assertNotNull(processInstance.getEnd());
        assertEquals(processInstanceId, processInstance.getProcessInstanceId());
        assertEquals("com.sample.ruleflow", processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstanceId);
        assertEquals(6, nodeInstances.size());
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            logger.info("{}", nodeInstance);
            assertEquals(processInstanceId, processInstance.getProcessInstanceId());
            assertEquals("com.sample.ruleflow", processInstance.getProcessId());
            assertNotNull(nodeInstance.getDate());
        }
        logService.clear();
        
        BitronixTransactionManager txm = TransactionManagerServices.getTransactionManager();
        assertTrue("There is still a transaction running!", txm.getCurrentTransaction() == null );
	}

    @Test
	public void testLogger2() {
        // load the process
        RuleBase ruleBase = createKnowledgeBase();
        // create a new session
        Properties properties = new Properties();
		properties.put("drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
		properties.put("drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
		SessionConfiguration config = new SessionConfiguration(properties);
        StatefulSession session = ruleBase.newStatefulSession(config, createEnvironment(context));
        new JPAWorkingMemoryDbLogger(session);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // start process instance
        session.startProcess("com.sample.ruleflow");
        session.startProcess("com.sample.ruleflow");
        
        logger.info("Checking process instances for process 'com.sample.ruleflow'");
        List<ProcessInstanceLog> processInstances =
        	logService.findProcessInstances("com.sample.ruleflow");
        assertEquals(2, processInstances.size());
        for (ProcessInstanceLog processInstance: processInstances) {
            logger.info("{}", processInstance);
            logger.info(" -> {} - {}", processInstance.getStart(), processInstance.getEnd());
            List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstance.getProcessInstanceId());
            for (NodeInstanceLog nodeInstance: nodeInstances) {
                logger.info("{}", nodeInstance);
                logger.info(" -> {}", nodeInstance.getDate());
            }
            assertEquals(6, nodeInstances.size());
        }
        logService.clear();
        
        BitronixTransactionManager txm = TransactionManagerServices.getTransactionManager();
        assertTrue("There is still a transaction running!", txm.getCurrentTransaction() == null );
	}

    @Test
	public void testLogger3() {
        // load the process
        RuleBase ruleBase = createKnowledgeBase();
        // create a new session
        Properties properties = new Properties();
		properties.put("drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
		properties.put("drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
		SessionConfiguration config = new SessionConfiguration(properties);
        StatefulSession session = ruleBase.newStatefulSession(config, createEnvironment(context));
        new JPAWorkingMemoryDbLogger(session);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow2").getId();
        
        logger.info("Checking process instances for process 'com.sample.ruleflow2'");
        List<ProcessInstanceLog> processInstances =
        	logService.findProcessInstances("com.sample.ruleflow2");
        assertEquals(1, processInstances.size());
        ProcessInstanceLog processInstance = processInstances.get(0);
        logger.info("{}", processInstance);
        logger.info(" -> {} - {} ", processInstance.getStart(), processInstance.getEnd());
        assertNotNull(processInstance.getStart());
        assertNotNull(processInstance.getEnd());
        assertEquals(processInstanceId, processInstance.getProcessInstanceId());
        assertEquals("com.sample.ruleflow2", processInstance.getProcessId());
        List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstanceId);
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            logger.info("{}", nodeInstance);
            logger.info(" -> {}", nodeInstance.getDate());
            assertEquals(processInstanceId, processInstance.getProcessInstanceId());
            assertEquals("com.sample.ruleflow2", processInstance.getProcessId());
            assertNotNull(nodeInstance.getDate());
        }
        assertEquals(14, nodeInstances.size());
        logService.clear();
        
        BitronixTransactionManager txm = TransactionManagerServices.getTransactionManager();
        assertTrue("There is still a transaction running!", txm.getCurrentTransaction() == null );
	}
	
    private static RuleBase createKnowledgeBase() {
        // create a builder
        PackageBuilder builder = new PackageBuilder();
        // load the process
        Reader source = new InputStreamReader(WorkingMemoryDbLoggerTest.class.getResourceAsStream("/ruleflow.rf"));
        builder.addProcessFromXml(source);
        source = new InputStreamReader(WorkingMemoryDbLoggerTest.class.getResourceAsStream("/ruleflow2.rf"));
        builder.addProcessFromXml(source);
        source = new InputStreamReader(WorkingMemoryDbLoggerTest.class.getResourceAsStream("/ruleflow3.rf"));
        builder.addProcessFromXml(source);
        // create the knowledge base 
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(pkg);
        return ruleBase;
    }
}
