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

package org.jbpm.executor.impl.jms;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.hornetq.jms.server.embedded.EmbeddedJMS;
import org.jboss.narayana.jta.jms.ConnectionFactoryProxy;
import org.jboss.narayana.jta.jms.TransactionHelperImpl;
import org.jbpm.executor.AsynchronousJobEvent;
import org.jbpm.executor.AsynchronousJobListener;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.ClassCacheManager;
import org.jbpm.executor.impl.ExecutorImpl;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.jbpm.test.util.ExecutorTestUtil;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.RequestInfo;
import org.kie.api.runtime.query.QueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsAvaiableJobExecutorTest  {

    private static final Logger logger = LoggerFactory.getLogger(JmsAvaiableJobExecutorTest.class);

    private ConnectionFactory factory;
    private Queue queue;
    
    private EmbeddedJMS jmsServer;   
    
    protected ExecutorService executorService;
    protected PoolingDataSource pds;
    protected EntityManagerFactory emf = null;
    
    @Before
    public void setUp() throws Exception {        
        startHornetQServer();
        pds = ExecutorTestUtil.setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");

        executorService = ExecutorServiceFactory.newExecutorService(emf);
                
        ((ExecutorImpl)((ExecutorServiceImpl)executorService).getExecutor()).setConnectionFactory(factory);
        ((ExecutorImpl)((ExecutorServiceImpl)executorService).getExecutor()).setQueue(queue);
        
        executorService.setThreadPoolSize(0);
        executorService.setInterval(10000);
        executorService.init();
    }

    @After
    public void tearDown() throws Exception {
        executorService.clearAllRequests();
        executorService.clearAllErrors();
        
        executorService.destroy();
        if (emf != null) {
            emf.close();
        }
        pds.close();
        
        System.clearProperty("org.kie.executor.msg.length");
        System.clearProperty("org.kie.executor.stacktrace.length");

        stopHornetQServer();
    }
    
    protected CountDownAsyncJobListener configureListener(int threads) {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(threads);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);
        
        return countDownListener;
    }
    
    @Test
    public void testAsyncAuditProducer() throws Exception {
        
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);
        ut.commit();
        MessageReceiver receiver = new MessageReceiver();
        receiver.receiveAndProcess(queue, countDownListener);

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(1, executedRequests.size());
 
    }
    
    @Test
    public void testAsyncAuditProducerPrioritizedJobs() throws Exception {
        
        CountDownAsyncJobListener countDownListener = configureListener(2);
        final List<String> executedJobs = new ArrayList<String>();
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(new AsynchronousJobListener() {
            
            @Override
            public void beforeJobScheduled(AsynchronousJobEvent event) {
            }
            
            @Override
            public void beforeJobExecuted(AsynchronousJobEvent event) {                
            }
            
            @Override
            public void beforeJobCancelled(AsynchronousJobEvent event) {                
            }
            
            @Override
            public void afterJobScheduled(AsynchronousJobEvent event) {                
            }
            
            @Override
            public void afterJobExecuted(AsynchronousJobEvent event) {
                executedJobs.add(event.getJob().getKey());
            }
            
            @Override
            public void afterJobCancelled(AsynchronousJobEvent event) {                
            }
        });
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", "low priority");
        ctxCMD.setData("priority", 2);
        
        CommandContext ctxCMD2 = new CommandContext();
        ctxCMD2.setData("businessKey", "high priority");
        ctxCMD2.setData("priority", 8);
        
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD2);
        ut.commit();
        MessageReceiver receiver = new MessageReceiver();
        receiver.receiveAndProcess(queue, countDownListener);

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(0, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(2, executedRequests.size());
        
        assertEquals(2,  executedJobs.size());
        assertEquals("high priority",  executedJobs.get(0));
        assertEquals("low priority",  executedJobs.get(1));
 
    }
    
    @Test
    public void testAsyncAuditProducerNotExistingDeployment() throws Exception {
        
        CountDownAsyncJobListener countDownListener = configureListener(1);
        CommandContext ctxCMD = new CommandContext();
        ctxCMD.setData("businessKey", UUID.randomUUID().toString());
        ctxCMD.setData("deploymentId", "not-existing");
        
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        executorService.scheduleRequest("org.jbpm.executor.commands.PrintOutCommand", ctxCMD);
        ut.commit();
        MessageReceiver receiver = new MessageReceiver();
        receiver.receiveAndProcess(queue, countDownListener, 3000);

        List<RequestInfo> inErrorRequests = executorService.getInErrorRequests(new QueryContext());
        assertEquals(0, inErrorRequests.size());
        List<RequestInfo> queuedRequests = executorService.getQueuedRequests(new QueryContext());
        assertEquals(1, queuedRequests.size());
        List<RequestInfo> executedRequests = executorService.getCompletedRequests(new QueryContext());
        assertEquals(0, executedRequests.size());
 
    }
    
    private void startHornetQServer() throws Exception {
        jmsServer = new EmbeddedJMS();
        jmsServer.start();
        logger.debug("Started Embedded JMS Server");

        XAConnectionFactory connectionFactory = (XAConnectionFactory) jmsServer.lookup("ConnectionFactory");

        new InitialContext().rebind("java:comp/UserTransaction", com.arjuna.ats.jta.UserTransaction.userTransaction());
        new InitialContext().rebind("java:comp/TransactionManager", com.arjuna.ats.jta.TransactionManager.transactionManager());
        new InitialContext().rebind("java:comp/TransactionSynchronizationRegistry", new com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionSynchronizationRegistryImple());

        factory =  new ConnectionFactoryProxy(connectionFactory, new TransactionHelperImpl(com.arjuna.ats.jta.TransactionManager.transactionManager()));
        
        queue = (Queue) jmsServer.lookup("/queue/exampleQueue");
        
    }
    
    private void stopHornetQServer() throws Exception {
        jmsServer.stop();
        jmsServer = null;
    }
    
    private class MessageReceiver {
        
        void receiveAndProcess(Queue queue, CountDownAsyncJobListener countDownListener) throws Exception {
            
            receiveAndProcess(queue, countDownListener, 100000);

        }
        
        void receiveAndProcess(Queue queue, CountDownAsyncJobListener countDownListener, long waitTill) throws Exception {
            
            Connection qconnetion = factory.createConnection();
            Session qsession = qconnetion.createSession(true, QueueSession.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = qsession.createConsumer(queue);
            qconnetion.start();
            JmsAvailableJobsExecutor jmsExecutor = new JmsAvailableJobsExecutor();
            jmsExecutor.setClassCacheManager(new ClassCacheManager());
            jmsExecutor.setExecutorStoreService(((ExecutorImpl)((ExecutorServiceImpl)executorService).getExecutor()).getExecutorStoreService());
            jmsExecutor.setQueryService(((ExecutorServiceImpl)executorService).getQueryService());
            jmsExecutor.setEventSupport(((ExecutorServiceImpl)executorService).getEventSupport());
            jmsExecutor.setExecutor(((ExecutorServiceImpl)executorService).getExecutor());
            consumer.setMessageListener(jmsExecutor);
            // since we use message listener allow it to complete the async processing
            countDownListener.waitTillCompleted(waitTill);
            
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
