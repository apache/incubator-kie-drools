/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.process.audit.jms;

import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.createEnvironment;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.jbpm.process.audit.AbstractAuditLogServiceTest.createKieSession;
import static org.jbpm.process.audit.AbstractAuditLogServiceTest.createKnowledgeBase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.XAConnectionFactory;
import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

import org.hornetq.jms.server.embedded.EmbeddedJMS;
import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.process.audit.AuditLogService;
import org.jbpm.process.audit.AuditLoggerFactory;
import org.jbpm.process.audit.AuditLoggerFactory.Type;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jms.PoolingConnectionFactory;

public class AsyncAuditLogProducerTest extends AbstractBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(AsyncAuditLogProducerTest.class);
    
    private HashMap<String, Object> context;
    private ConnectionFactory factory;
    private Queue queue;
    
    private EmbeddedJMS jmsServer;    
    
    @Before
    public void setup() throws Exception {
        startHornetQServer();
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);
    }

    @After
    public void tearDown() throws Exception {
        cleanUp(context);
        stopHornetQServer();
    }
    
    @Test
    public void testAsyncAuditProducer() throws Exception {
        Environment env = createEnvironment(context);
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        StatefulKnowledgeSession session = createSession(kbase, env);

        Map<String, Object> jmsProps = new HashMap<String, Object>();
        jmsProps.put("jbpm.audit.jms.transacted", false);
        jmsProps.put("jbpm.audit.jms.connection.factory", factory);
        jmsProps.put("jbpm.audit.jms.queue", queue);
        AbstractAuditLogger logger = AuditLoggerFactory.newInstance(Type.JMS, session, jmsProps);
        assertNotNull(logger);
        assertTrue((logger instanceof AsyncAuditLogProducer));

        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        // setup listener
        
        MessageReceiver receiver = new MessageReceiver();
        List<Message> messages = receiver.receive(queue);
        assertNotNull(messages);
        assertEquals(11, messages.size());

    }
    
    @Test
    public void testAsyncAuditProducerTransactional() throws Exception {
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        Environment env = createEnvironment(context);
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        StatefulKnowledgeSession session = createSession(kbase, env);
        
        Map<String, Object> jmsProps = new HashMap<String, Object>();
        jmsProps.put("jbpm.audit.jms.transacted", true);
        jmsProps.put("jbpm.audit.jms.connection.factory", factory);
        jmsProps.put("jbpm.audit.jms.queue", queue);
        AbstractAuditLogger logger = AuditLoggerFactory.newInstance(Type.JMS, session, jmsProps);
        assertNotNull(logger);
        assertTrue((logger instanceof AsyncAuditLogProducer));

        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        
        ut.commit();
        
        MessageReceiver receiver = new MessageReceiver();
        List<Message> messages = receiver.receive(queue);
        assertNotNull(messages);
        assertEquals(11, messages.size());

    }
    
    @Test
    public void testAsyncAuditProducerTransactionalWithRollback() throws Exception {
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        Environment env = createEnvironment(context);
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        StatefulKnowledgeSession session = createSession(kbase, env);
        
        Map<String, Object> jmsProps = new HashMap<String, Object>();
        jmsProps.put("jbpm.audit.jms.transacted", true);
        jmsProps.put("jbpm.audit.jms.connection.factory", factory);
        jmsProps.put("jbpm.audit.jms.queue", queue);
        AbstractAuditLogger logger = AuditLoggerFactory.newInstance(Type.JMS, session, jmsProps);
        assertNotNull(logger);
        assertTrue((logger instanceof AsyncAuditLogProducer));

        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        
        ut.rollback();
        
        MessageReceiver receiver = new MessageReceiver();
        List<Message> messages = receiver.receive(queue);
        assertNotNull(messages);
        assertEquals(0, messages.size());

    }
    
    @Test
    public void testAsyncAuditProducerNonTransactionalWithRollback() throws Exception {
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        Environment env = createEnvironment(context);
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        StatefulKnowledgeSession session = createSession(kbase, env);
        
        Map<String, Object> jmsProps = new HashMap<String, Object>();
        jmsProps.put("jbpm.audit.jms.transacted", false);
        // do not use bitronix managed connection factory as it enlists it regardless of if queue session
        // is transacted or not thus always participates in transaction
        jmsProps.put("jbpm.audit.jms.connection.factory", jmsServer.lookup("ConnectionFactory"));
        jmsProps.put("jbpm.audit.jms.queue", queue);
        AbstractAuditLogger logger = AuditLoggerFactory.newInstance(Type.JMS, session, jmsProps);
        assertNotNull(logger);
        assertTrue((logger instanceof AsyncAuditLogProducer));

        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        
        ut.rollback();
        
        MessageReceiver receiver = new MessageReceiver();
        List<Message> messages = receiver.receive(queue);
        assertNotNull(messages);
        assertEquals(11, messages.size());

    }
    
    @Test
    public void testAsyncAuditLoggerComplete() throws Exception {
        Environment env = createEnvironment(context);
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        StatefulKnowledgeSession session = createSession(kbase, env);
        
        Map<String, Object> jmsProps = new HashMap<String, Object>();
        jmsProps.put("jbpm.audit.jms.transacted", false);
        jmsProps.put("jbpm.audit.jms.connection.factory", factory);
        jmsProps.put("jbpm.audit.jms.queue", queue);
        AbstractAuditLogger logger = AuditLoggerFactory.newInstance(Type.JMS, session, jmsProps);
        assertNotNull(logger);
        assertTrue((logger instanceof AsyncAuditLogProducer));


        // start process instance
        ProcessInstance processInstance = session.startProcess("com.sample.ruleflow");
        
        MessageReceiver receiver = new MessageReceiver();
        receiver.receiveAndProcess(queue, ((EntityManagerFactory)env.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
     
        // validate if everything is stored in db
        AuditLogService logService = new JPAAuditLogService(env);
        List<ProcessInstanceLog> processInstances = logService.findProcessInstances("com.sample.ruleflow");
        assertEquals(1, processInstances.size());
        List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstance.getId());
        assertEquals(6, nodeInstances.size());
        for (NodeInstanceLog nodeInstance: nodeInstances) {

            assertEquals(processInstance.getId(), nodeInstance.getProcessInstanceId().longValue());
            assertEquals("com.sample.ruleflow", nodeInstance.getProcessId());
            assertNotNull(nodeInstance.getDate());
        }
        logService.clear();
        processInstances = logService.findProcessInstances("com.sample.ruleflow");
        logService.dispose();
        assertTrue(processInstances.isEmpty());
    }
    
    @Test
    public void testAsyncAuditLoggerCompleteDirectCreation() throws Exception {
        Environment env = createEnvironment(context);
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        StatefulKnowledgeSession session = createSession(kbase, env);
        

        AbstractAuditLogger logger = AuditLoggerFactory.newJMSInstance(true, factory, queue);
        assertNotNull(logger);
        assertTrue((logger instanceof AsyncAuditLogProducer));
        session.addEventListener(logger);

        // start process instance
        ProcessInstance processInstance = session.startProcess("com.sample.ruleflow");
        
        MessageReceiver receiver = new MessageReceiver();
        receiver.receiveAndProcess(queue, ((EntityManagerFactory)env.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
     
        // validate if everything is stored in db
        AuditLogService logService = new JPAAuditLogService(env);
        List<ProcessInstanceLog> processInstances = logService.findProcessInstances("com.sample.ruleflow");
        assertEquals(1, processInstances.size());
        List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstance.getId());
        assertEquals(6, nodeInstances.size());
        for (NodeInstanceLog nodeInstance: nodeInstances) {

            assertEquals(processInstance.getId(), nodeInstance.getProcessInstanceId().longValue());
            assertEquals("com.sample.ruleflow", nodeInstance.getProcessId());
            assertNotNull(nodeInstance.getDate());
        }
        logService.clear();
        processInstances = logService.findProcessInstances("com.sample.ruleflow");
        logService.dispose();
        assertTrue(processInstances.isEmpty());
    }
    
    @Test
    public void testAsyncAuditLoggerCompleteWithVariables() throws Exception {
        Environment env = createEnvironment(context);
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        StatefulKnowledgeSession session = createSession(kbase, env);
        
        Map<String, Object> jmsProps = new HashMap<String, Object>();
        jmsProps.put("jbpm.audit.jms.transacted", false);
        jmsProps.put("jbpm.audit.jms.connection.factory", factory);
        jmsProps.put("jbpm.audit.jms.queue", queue);
        AbstractAuditLogger logger = AuditLoggerFactory.newInstance(Type.JMS, session, jmsProps);
        assertNotNull(logger);
        assertTrue((logger instanceof AsyncAuditLogProducer));

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "test value");

        // start process instance
        ProcessInstance processInstance = session.startProcess("com.sample.ruleflow3", params);
        
        MessageReceiver receiver = new MessageReceiver();
        receiver.receiveAndProcess(queue, ((EntityManagerFactory)env.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
     
        // validate if everything is stored in db
        AuditLogService logService = new JPAAuditLogService(env);
        List<ProcessInstanceLog> processInstances = logService.findProcessInstances("com.sample.ruleflow3");
        assertEquals(1, processInstances.size());
        List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstance.getId());
        assertEquals(6, nodeInstances.size());
        for (NodeInstanceLog nodeInstance: nodeInstances) {

            assertEquals(processInstance.getId(), nodeInstance.getProcessInstanceId().longValue());
            assertEquals("com.sample.ruleflow3", nodeInstance.getProcessId());
            assertNotNull(nodeInstance.getDate());
        }
        //verify variables
        List<VariableInstanceLog> variables = logService.findVariableInstances(processInstance.getId());
        assertNotNull(variables);
        assertEquals(2, variables.size());
        
        VariableInstanceLog var = variables.get(0);
        // initial value from rule flow definition
        assertEquals("InitialValue", var.getValue());
        assertEquals("", var.getOldValue());
        assertEquals(processInstance.getId(), var.getProcessInstanceId().longValue());
        assertEquals(processInstance.getProcessId(), var.getProcessId());
        assertEquals("s", var.getVariableId());
        assertEquals("s", var.getVariableInstanceId());
        
        // value given at process start
        var = variables.get(1);
        // initial value from rule flow definition
        assertEquals("test value", var.getValue());
        assertEquals("InitialValue", var.getOldValue());
        assertEquals(processInstance.getId(), var.getProcessInstanceId().longValue());
        assertEquals(processInstance.getProcessId(), var.getProcessId());
        assertEquals("s", var.getVariableId());
        assertEquals("s", var.getVariableInstanceId());
        
        logService.clear();
        processInstances = logService.findProcessInstances("com.sample.ruleflow3");
        logService.dispose();
        assertTrue(processInstances.isEmpty());
    }
    
    @Test
    public void testAsyncAuditLoggerCompleteWithVariablesCustomIndexer() throws Exception {
        Environment env = createEnvironment(context);
        // load the process
        KnowledgeBase kbase = createKnowledgeBase();
        // create a new session
        StatefulKnowledgeSession session = createSession(kbase, env);
        
        Map<String, Object> jmsProps = new HashMap<String, Object>();
        jmsProps.put("jbpm.audit.jms.transacted", false);
        jmsProps.put("jbpm.audit.jms.connection.factory", factory);
        jmsProps.put("jbpm.audit.jms.queue", queue);
        AbstractAuditLogger logger = AuditLoggerFactory.newInstance(Type.JMS, session, jmsProps);
        assertNotNull(logger);
        assertTrue((logger instanceof AsyncAuditLogProducer));

        List<String> names = new LinkedList<String>();
        names.add("john");
        names.add("mary");
        names.add("peter");
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("list", names);

        // start process instance
        ProcessInstance processInstance = session.startProcess("com.sample.ruleflow3", params);
        
        MessageReceiver receiver = new MessageReceiver();
        receiver.receiveAndProcess(queue, ((EntityManagerFactory)env.get(EnvironmentName.ENTITY_MANAGER_FACTORY)));
     
        // validate if everything is stored in db
        AuditLogService logService = new JPAAuditLogService(env);
        List<ProcessInstanceLog> processInstances = logService.findProcessInstances("com.sample.ruleflow3");
        assertEquals(1, processInstances.size());
        List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstance.getId());
        assertEquals(12, nodeInstances.size());
        for (NodeInstanceLog nodeInstance: nodeInstances) {

            assertEquals(processInstance.getId(), nodeInstance.getProcessInstanceId().longValue());
            assertEquals("com.sample.ruleflow3", nodeInstance.getProcessId());
            assertNotNull(nodeInstance.getDate());
        }
        //verify variables
        List<VariableInstanceLog> variables = logService.findVariableInstances(processInstance.getId());
        assertNotNull(variables);
        assertEquals(8, variables.size());
        
        List<VariableInstanceLog> listVariables = new ArrayList<VariableInstanceLog>();
        // collect only those that are related to list process variable
        for (VariableInstanceLog v : variables) {
            if (v.getVariableInstanceId().equals("list")) {
                listVariables.add(v);
            }
        }
        
        assertEquals(3, listVariables.size());
        VariableInstanceLog var = listVariables.get(0);
        
        assertEquals("john", var.getValue());
        assertEquals("", var.getOldValue());
        assertEquals(processInstance.getId(), var.getProcessInstanceId().longValue());
        assertEquals(processInstance.getProcessId(), var.getProcessId());
        assertEquals("list[0]", var.getVariableId());
        assertEquals("list", var.getVariableInstanceId());
        
        var = listVariables.get(1);
        assertEquals("mary", var.getValue());
        assertEquals("", var.getOldValue());
        assertEquals(processInstance.getId(), var.getProcessInstanceId().longValue());
        assertEquals(processInstance.getProcessId(), var.getProcessId());
        assertEquals("list[1]", var.getVariableId());
        assertEquals("list", var.getVariableInstanceId());
        
        var = listVariables.get(2);        
        assertEquals("peter", var.getValue());
        assertEquals("", var.getOldValue());
        assertEquals(processInstance.getId(), var.getProcessInstanceId().longValue());
        assertEquals(processInstance.getProcessId(), var.getProcessId());
        assertEquals("list[2]", var.getVariableId());
        assertEquals("list", var.getVariableInstanceId());
        
        logService.clear();
        processInstances = logService.findProcessInstances("com.sample.ruleflow3");
        logService.dispose();
        assertTrue(processInstances.isEmpty());
    }
    
    public StatefulKnowledgeSession createSession(KnowledgeBase kbase, Environment env) {
        
        StatefulKnowledgeSession session = createKieSession(kbase, env);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        return session;
    }
    
    private void startHornetQServer() throws Exception {
        jmsServer = new EmbeddedJMS();
        jmsServer.start();
        logger.debug("Started Embedded JMS Server");

        BitronixHornetQXAConnectionFactory.connectionFactory = (XAConnectionFactory) jmsServer.lookup("ConnectionFactory");

        PoolingConnectionFactory myConnectionFactory = new PoolingConnectionFactory ();                   
        myConnectionFactory.setClassName("org.jbpm.process.audit.jms.BitronixHornetQXAConnectionFactory");              
        myConnectionFactory.setUniqueName("hornet");                                                    
        myConnectionFactory.setMaxPoolSize(5); 
        myConnectionFactory.setAllowLocalTransactions(true);

        myConnectionFactory.init(); 
        
        factory = myConnectionFactory;
        
        queue = (Queue) jmsServer.lookup("/queue/exampleQueue");
    }
    
    private void stopHornetQServer() throws Exception {
        ((PoolingConnectionFactory) factory).close();
        jmsServer.stop();
        jmsServer = null;
    }
    
    private class MessageReceiver {
        
        void receiveAndProcess(Queue queue, EntityManagerFactory entityManagerFactory) throws Exception {
            
            Connection qconnetion = factory.createConnection();
            Session qsession = qconnetion.createSession(true, QueueSession.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = qsession.createConsumer(queue);
            qconnetion.start();
            AsyncAuditLogReceiver rec = new AsyncAuditLogReceiver(entityManagerFactory) {

                @Override
                public void onMessage(Message message) {
                    try {
                        // need to use transaction so entity manager will persist logs
                        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                        ut.begin();                    
                        super.onMessage(message);
                        ut.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
            };
            consumer.setMessageListener(rec);
            // since we use message listener allow it to complete the async processing
            Thread.sleep(2000);
            
            consumer.close();            
            qsession.close();            
            qconnetion.close();

        }
        
        public List<Message> receive(Queue queue) throws Exception {
            List<Message> messages = new ArrayList<Message>();
            
            Connection qconnetion = factory.createConnection();
            Session qsession = qconnetion.createSession(true, QueueSession.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = qsession.createConsumer(queue);
            qconnetion.start();
            
            Message m = null;
            
            while ((m = consumer.receiveNoWait()) != null) {
                messages.add(m);
            }
            consumer.close();            
            qsession.close();            
            qconnetion.close();
            
            return messages;
        }
    }
}
