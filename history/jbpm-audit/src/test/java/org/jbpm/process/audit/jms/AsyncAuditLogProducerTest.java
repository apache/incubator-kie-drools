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

package org.jbpm.process.audit.jms;

import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.createEnvironment;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.jbpm.process.audit.AbstractAuditLogServiceTest.createKieSession;
import static org.jbpm.process.audit.AbstractAuditLogServiceTest.createKnowledgeBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

import org.assertj.core.api.Assertions;
import org.hornetq.jms.server.embedded.EmbeddedJMS;
import org.jboss.narayana.jta.jms.ConnectionFactoryProxy;
import org.jboss.narayana.jta.jms.TransactionHelperImpl;
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
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        KieBase kbase = createKnowledgeBase();
        // create a new session
        KieSession session = createSession(kbase, env);

        Map<String, Object> jmsProps = new HashMap<String, Object>();
        jmsProps.put("jbpm.audit.jms.transacted", false);
        jmsProps.put("jbpm.audit.jms.connection.factory", factory);
        jmsProps.put("jbpm.audit.jms.queue", queue);
        AbstractAuditLogger logger = AuditLoggerFactory.newInstance(Type.JMS, session, jmsProps);
        Assertions.assertThat(logger).isNotNull();
        Assertions.assertThat((logger instanceof AsyncAuditLogProducer)).isTrue();

        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        // setup listener
        
        MessageReceiver receiver = new MessageReceiver();
        List<Message> messages = receiver.receive(queue);
        Assertions.assertThat(messages).isNotNull();
        Assertions.assertThat(messages.size()).isEqualTo(11);

    }
    
    @Test
    public void testAsyncAuditProducerTransactional() throws Exception {
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        Environment env = createEnvironment(context);
        // load the process
        KieBase kbase = createKnowledgeBase();
        // create a new session
        KieSession session = createSession(kbase, env);

        Map<String, Object> jmsProps = new HashMap<String, Object>();
        jmsProps.put("jbpm.audit.jms.transacted", true);
        jmsProps.put("jbpm.audit.jms.connection.factory", factory);
        jmsProps.put("jbpm.audit.jms.queue", queue);
        AbstractAuditLogger logger = AuditLoggerFactory.newInstance(Type.JMS, session, jmsProps);
        Assertions.assertThat(logger).isNotNull();
        Assertions.assertThat((logger instanceof AsyncAuditLogProducer)).isTrue();

        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        
        ut.commit();
        
        MessageReceiver receiver = new MessageReceiver();
        List<Message> messages = receiver.receive(queue);
        Assertions.assertThat(messages).isNotNull();
        Assertions.assertThat(messages.size()).isEqualTo(11);

    }
    
    @Test
    public void testAsyncAuditProducerTransactionalWithRollback() throws Exception {
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        Environment env = createEnvironment(context);
        // load the process
        KieBase kbase = createKnowledgeBase();
        // create a new session
        KieSession session = createSession(kbase, env);

        Map<String, Object> jmsProps = new HashMap<String, Object>();
        jmsProps.put("jbpm.audit.jms.transacted", true);
        jmsProps.put("jbpm.audit.jms.connection.factory", factory);
        jmsProps.put("jbpm.audit.jms.queue", queue);
        AbstractAuditLogger logger = AuditLoggerFactory.newInstance(Type.JMS, session, jmsProps);
        Assertions.assertThat(logger).isNotNull();
        Assertions.assertThat((logger instanceof AsyncAuditLogProducer)).isTrue();

        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        
        ut.rollback();
        
        MessageReceiver receiver = new MessageReceiver();
        List<Message> messages = receiver.receive(queue);
        Assertions.assertThat(messages).isNotNull();
        Assertions.assertThat(messages.size()).isEqualTo(0);
    }
    
    @Test
    public void testAsyncAuditProducerNonTransactionalWithRollback() throws Exception {
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        Environment env = createEnvironment(context);
        // load the process
        KieBase kbase = createKnowledgeBase();
        // create a new session
        KieSession session = createSession(kbase, env);

        Map<String, Object> jmsProps = new HashMap<String, Object>();
        jmsProps.put("jbpm.audit.jms.transacted", false);
        jmsProps.put("jbpm.audit.jms.connection.factory", jmsServer.lookup("ConnectionFactory"));
        jmsProps.put("jbpm.audit.jms.queue", queue);
        AbstractAuditLogger logger = AuditLoggerFactory.newInstance(Type.JMS, session, jmsProps);
        Assertions.assertThat(logger).isNotNull();
        Assertions.assertThat((logger instanceof AsyncAuditLogProducer)).isTrue();

        // start process instance
        long processInstanceId = session.startProcess("com.sample.ruleflow").getId();
        
        ut.rollback();
        
        MessageReceiver receiver = new MessageReceiver();
        List<Message> messages = receiver.receive(queue);
        Assertions.assertThat(messages).isNotNull();
        Assertions.assertThat(messages.size()).isEqualTo(11);
    }
    
    @Test
    public void testAsyncAuditLoggerComplete() throws Exception {
        Environment env = createEnvironment(context);
        // load the process
        KieBase kbase = createKnowledgeBase();
        // create a new session
        KieSession session = createSession(kbase, env);

        Map<String, Object> jmsProps = new HashMap<String, Object>();
        jmsProps.put("jbpm.audit.jms.transacted", false);
        jmsProps.put("jbpm.audit.jms.connection.factory", factory);
        jmsProps.put("jbpm.audit.jms.queue", queue);
        AbstractAuditLogger logger = AuditLoggerFactory.newInstance(Type.JMS, session, jmsProps);
        Assertions.assertThat(logger).isNotNull();
        Assertions.assertThat((logger instanceof AsyncAuditLogProducer)).isTrue();


        // start process instance
        ProcessInstance processInstance = session.startProcess("com.sample.ruleflow");
        
        MessageReceiver receiver = new MessageReceiver();
        receiver.receiveAndProcess(queue, ((EntityManagerFactory)env.get(EnvironmentName.ENTITY_MANAGER_FACTORY)), 2000, 11);
     
        // validate if everything is stored in db
        AuditLogService logService = new JPAAuditLogService(env);
        List<ProcessInstanceLog> processInstances = logService.findProcessInstances("com.sample.ruleflow");
        Assertions.assertThat(processInstances.size()).isEqualTo(1);
        List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstance.getId());
        Assertions.assertThat(nodeInstances.size()).isEqualTo(6);
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            Assertions.assertThat(processInstance.getId()).isEqualTo(nodeInstance.getProcessInstanceId().longValue());
            Assertions.assertThat(nodeInstance.getProcessId()).isEqualTo("com.sample.ruleflow");
            Assertions.assertThat(nodeInstance.getDate()).isNotNull();
        }
        logService.clear();
        processInstances = logService.findProcessInstances("com.sample.ruleflow");
        logService.dispose();
        Assertions.assertThat(processInstances).isEmpty();
    }
    
    @Test
    public void testAsyncAuditLoggerCompleteDirectCreation() throws Exception {
        Environment env = createEnvironment(context);
        // load the process
        KieBase kbase = createKnowledgeBase();
        // create a new session
        KieSession session = createSession(kbase, env);


        AbstractAuditLogger logger = AuditLoggerFactory.newJMSInstance(true, factory, queue);
        Assertions.assertThat(logger).isNotNull();
        Assertions.assertThat((logger instanceof AsyncAuditLogProducer)).isTrue();
        session.addEventListener(logger);

        // start process instance
        ProcessInstance processInstance = session.startProcess("com.sample.ruleflow");
        
        MessageReceiver receiver = new MessageReceiver();
        receiver.receiveAndProcess(queue, ((EntityManagerFactory)env.get(EnvironmentName.ENTITY_MANAGER_FACTORY)), 6000, 11);
     
        // validate if everything is stored in db
        AuditLogService logService = new JPAAuditLogService(env);
        List<ProcessInstanceLog> processInstances = logService.findProcessInstances("com.sample.ruleflow");
        Assertions.assertThat(processInstances.size()).isEqualTo(1);
        List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstance.getId());
        Assertions.assertThat(nodeInstances.size()).isEqualTo(6);
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            Assertions.assertThat(nodeInstance.getProcessInstanceId().longValue()).isEqualTo(processInstance.getId());
            Assertions.assertThat(nodeInstance.getProcessId()).isEqualTo("com.sample.ruleflow");
            Assertions.assertThat(nodeInstance.getDate()).isNotNull();
        }
        logService.clear();
        processInstances = logService.findProcessInstances("com.sample.ruleflow");
        logService.dispose();
        Assertions.assertThat(processInstances).isEmpty();
    }

    @Test
    public void testAsyncAuditLoggerCompleteWithVariables() throws Exception {
        Environment env = createEnvironment(context);
        // load the process
        KieBase kbase = createKnowledgeBase();
        // create a new session
        KieSession session = createSession(kbase, env);

        Map<String, Object> jmsProps = new HashMap<String, Object>();
        jmsProps.put("jbpm.audit.jms.transacted", false);
        jmsProps.put("jbpm.audit.jms.connection.factory", factory);
        jmsProps.put("jbpm.audit.jms.queue", queue);
        AbstractAuditLogger logger = AuditLoggerFactory.newInstance(Type.JMS, session, jmsProps);
        Assertions.assertThat(logger).isNotNull();
        Assertions.assertThat((logger instanceof AsyncAuditLogProducer)).isTrue();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "test value");

        // start process instance
        ProcessInstance processInstance = session.startProcess("com.sample.ruleflow3", params);

        MessageReceiver receiver = new MessageReceiver();
        receiver.receiveAndProcess(queue, ((EntityManagerFactory)env.get(EnvironmentName.ENTITY_MANAGER_FACTORY)), 3000, 13);

        // validate if everything is stored in db
        AuditLogService logService = new JPAAuditLogService(env);
        List<ProcessInstanceLog> processInstances = logService.findProcessInstances("com.sample.ruleflow3");
        Assertions.assertThat(processInstances.size()).isEqualTo(1);
        List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstance.getId());
        Assertions.assertThat(nodeInstances.size()).isEqualTo(6);
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            Assertions.assertThat(nodeInstance.getProcessInstanceId().longValue()).isEqualTo(processInstance.getId());
            Assertions.assertThat(nodeInstance.getProcessId()).isEqualTo("com.sample.ruleflow3");
            Assertions.assertThat(nodeInstance.getDate()).isNotNull();
        }
        //verify variables
        List<VariableInstanceLog> variables = logService.findVariableInstances(processInstance.getId());
        Assertions.assertThat(variables).isNotNull();
        Assertions.assertThat(variables).hasSize(2);
        
        VariableInstanceLog var = variables.get(0);
        // initial value from rule flow definition
        Assertions.assertThat(var.getValue()).isEqualTo("InitialValue");
        Assertions.assertThat(var.getOldValue()).isIn("", " ", null);
        Assertions.assertThat(var.getProcessInstanceId().longValue()).isEqualTo(processInstance.getId());
        Assertions.assertThat(var.getProcessId()).isEqualTo(processInstance.getProcessId());
        Assertions.assertThat(var.getVariableId()).isEqualTo("s");
        Assertions.assertThat(var.getVariableInstanceId()).isEqualTo("s");
        
        // value given at process start
        var = variables.get(1);
        // initial value from rule flow definition
        Assertions.assertThat(var.getValue()).isEqualTo("test value");
        Assertions.assertThat(var.getOldValue()).isEqualTo("InitialValue");
        Assertions.assertThat(var.getProcessInstanceId().longValue()).isEqualTo(processInstance.getId());
        Assertions.assertThat(var.getProcessId()).isEqualTo(processInstance.getProcessId());
        Assertions.assertThat(var.getVariableId()).isEqualTo("s");
        Assertions.assertThat(var.getVariableInstanceId()).isEqualTo("s");

        logService.clear();
        processInstances = logService.findProcessInstances("com.sample.ruleflow3");
        logService.dispose();
        Assertions.assertThat(processInstances).isNullOrEmpty();
    }
    
    @Test
    public void testAsyncAuditLoggerCompleteWithVariablesCustomIndexer() throws Exception {
        Environment env = createEnvironment(context);
        // load the process
        KieBase kbase = createKnowledgeBase();
        // create a new session
        KieSession session = createSession(kbase, env);

        Map<String, Object> jmsProps = new HashMap<String, Object>();
        jmsProps.put("jbpm.audit.jms.transacted", false);
        jmsProps.put("jbpm.audit.jms.connection.factory", factory);
        jmsProps.put("jbpm.audit.jms.queue", queue);
        AbstractAuditLogger logger = AuditLoggerFactory.newInstance(Type.JMS, session, jmsProps);
        Assertions.assertThat(logger).isNotNull();
        Assertions.assertThat((logger instanceof AsyncAuditLogProducer)).isTrue();

        List<String> names = new LinkedList<String>();
        names.add("john");
        names.add("mary");
        names.add("peter");
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("list", names);

        // start process instance
        ProcessInstance processInstance = session.startProcess("com.sample.ruleflow3", params);
        
        MessageReceiver receiver = new MessageReceiver();
        receiver.receiveAndProcess(queue, ((EntityManagerFactory)env.get(EnvironmentName.ENTITY_MANAGER_FACTORY)), 6000, 28);
     
        // validate if everything is stored in db
        AuditLogService logService = new JPAAuditLogService(env);
        List<ProcessInstanceLog> processInstances = logService.findProcessInstances("com.sample.ruleflow3");
        Assertions.assertThat(processInstances.size()).isEqualTo(1);
        List<NodeInstanceLog> nodeInstances = logService.findNodeInstances(processInstance.getId());
        Assertions.assertThat(nodeInstances.size()).isEqualTo(12);
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            Assertions.assertThat(nodeInstance.getProcessInstanceId().longValue()).isEqualTo(processInstance.getId());
            Assertions.assertThat(nodeInstance.getProcessId()).isEqualTo("com.sample.ruleflow3");
            Assertions.assertThat(nodeInstance.getDate()).isNotNull();
        }
        //verify variables
        List<VariableInstanceLog> variables = logService.findVariableInstances(processInstance.getId());
        Assertions.assertThat(variables).isNotNull();
        Assertions.assertThat(variables.size()).isEqualTo(8);
        
        List<VariableInstanceLog> listVariables = new ArrayList<VariableInstanceLog>();
        // collect only those that are related to list process variable
        for (VariableInstanceLog v : variables) {
            if (v.getVariableInstanceId().equals("list")) {
                listVariables.add(v);
            }
        }

        Assertions.assertThat(listVariables.size()).isEqualTo(3);

        List<String> variableValues = new ArrayList<String>();
        List<String> variableIds = new ArrayList<String>();
        for (VariableInstanceLog var : listVariables) {
            variableValues.add(var.getValue());
            variableIds.add(var.getVariableId());
            Assertions.assertThat(var.getOldValue()).isIn("", " ", null);
            Assertions.assertThat(var.getProcessInstanceId().longValue()).isEqualTo(processInstance.getId());
            Assertions.assertThat(var.getProcessId()).isEqualTo(processInstance.getProcessId());
            Assertions.assertThat(var.getVariableInstanceId()).isEqualTo("list");
        }

        Assertions.assertThat(variableValues).contains("john", "mary", "peter");
        Assertions.assertThat(variableIds).contains("list[0]", "list[1]", "list[2]");

        logService.clear();
        processInstances = logService.findProcessInstances("com.sample.ruleflow3");
        logService.dispose();
        Assertions.assertThat(processInstances).isNullOrEmpty();
    }
    
    public KieSession createSession(KieBase kbase, Environment env) {
        
        KieSession session = createKieSession(kbase, env);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        return session;
    }
    
    private void startHornetQServer() throws Exception {
        jmsServer = new EmbeddedJMS();
        jmsServer.start();
        logger.debug("Started Embedded JMS Server");

        XAConnectionFactory connectionFactory = (XAConnectionFactory) jmsServer.lookup("ConnectionFactory");

        new InitialContext().rebind("java:comp/UserTransaction", com.arjuna.ats.jta.UserTransaction.userTransaction());
        new InitialContext().rebind("java:comp/TransactionManager", com.arjuna.ats.jta.TransactionManager.transactionManager());
        new InitialContext().rebind("java:comp/TransactionSynchronizationRegistry", new com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionSynchronizationRegistryImple());
        factory = new ConnectionFactoryProxy(connectionFactory, new TransactionHelperImpl(com.arjuna.ats.jta.TransactionManager.transactionManager()));
        
        queue = (Queue) jmsServer.lookup("/queue/exampleQueue");
    }
    
    private void stopHornetQServer() throws Exception {
        jmsServer.stop();
        jmsServer = null;
    }
    
    private class MessageReceiver {
        
        void receiveAndProcess(Queue queue, EntityManagerFactory entityManagerFactory, long waitTime, int countDown) throws Exception {
            
            Connection qconnetion = factory.createConnection();
            Session qsession = qconnetion.createSession(true, QueueSession.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = qsession.createConsumer(queue);
            qconnetion.start();

            CountDownLatch latch = new CountDownLatch(countDown);
            AsyncAuditLogReceiver rec = new AsyncAuditLogReceiver(entityManagerFactory) {

                @Override
                public void onMessage(Message message) {
                    try {
                        // need to use transaction so entity manager will persist logs
                        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
                        ut.begin();                    
                        super.onMessage(message);
                        ut.commit();
                        latch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
            };
            consumer.setMessageListener(rec);
            Assertions.assertThat(latch.await(waitTime, TimeUnit.MILLISECONDS)).isTrue();
            
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
