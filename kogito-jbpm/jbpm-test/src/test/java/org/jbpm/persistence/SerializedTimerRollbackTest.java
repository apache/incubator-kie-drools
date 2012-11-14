package org.jbpm.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.TransactionManager;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.SystemEventListenerFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.drools.common.InternalKnowledgeRuntime;
import org.kie.io.ResourceFactory;
import org.drools.marshalling.impl.MarshallingConfigurationImpl;
import org.drools.marshalling.impl.ProtobufMarshaller;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.local.LocalTaskService;
import org.jbpm.task.utils.OnErrorAction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;


public class SerializedTimerRollbackTest {

    private PoolingDataSource ds;
    
    @Before
    public void setup() {
        ds = new PoolingDataSource();
        ds.setUniqueName( "jdbc/jbpm-ds" );
        ds.setClassName( "org.h2.jdbcx.JdbcDataSource" );
        ds.setMaxPoolSize( 3 );
        ds.setAllowLocalTransactions( true );
        ds.getDriverProperties().put( "user", "sa" );
        ds.getDriverProperties().put( "password", "sasa" );
        ds.getDriverProperties().put( "URL", "jdbc:h2:mem:mydb" );
        ds.init();
        UserGroupCallbackManager.getInstance().setCallback(null);
    }
    
    @After
    public void tearDown() {
        ds.close();
    }
    
    @Test
    public void testSerizliableTestsWithExternalRollback() {
        try {

            Environment env = KnowledgeBaseFactory.newEnvironment();
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
            TransactionManager tm = TransactionManagerServices.getTransactionManager();
            System.out.println("Created JPA EntityManager");
            
            env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, emf );
            env.set( EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager() );
            TaskService taskService = new org.jbpm.task.service.TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
            Map<String, User> users = new HashMap<String, User>();
            users.put("Administrator", new User("Administrator"));
            users.put("john", new User("john"));
            Map<String, Group> groups = new HashMap<String, Group>();
            taskService.addUsersAndGroups(users, groups);
            org.jbpm.task.TaskService humanTaskClient = new LocalTaskService(taskService);;
            
            System.out.println("Task service created");
            
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(ResourceFactory.newClassPathResource("HumanTaskWithBoundaryTimer.bpmn"),ResourceType.BPMN2);
            if (kbuilder.getErrors()!=null){
                for(KnowledgeBuilderError error: kbuilder.getErrors()){
                    System.err.println(error.toString());
                }
            }
            System.out.println("BPMN process knowledge acquired");
            
            KnowledgeBase kbase = kbuilder.newKnowledgeBase();
            StatefulKnowledgeSession sesion = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
            System.out.println("Created knowledge session");
            
            LocalHTWorkItemHandler localHTWorkItemHandler = new LocalHTWorkItemHandler(humanTaskClient, sesion, OnErrorAction.RETHROW);
            localHTWorkItemHandler.connect();
            sesion.getWorkItemManager().registerWorkItemHandler("Human Task", localHTWorkItemHandler);
            System.out.println("Attached human task work item handler");
            List<Long> committedProcessInstanceIds = new ArrayList<Long>();
            for(int i=0;i<10;i++){
                tm.begin();
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("test", "john");
                System.out.println("Creating process instance: "+ i);
                ProcessInstance pi = sesion.startProcess("PROCESS_1", params);
                if (i%2 == 0) {
                    committedProcessInstanceIds.add(pi.getId());
                    tm.commit();
                } else {
                    tm.rollback();
                }
            }
            
            Connection c = ds.getConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("select rulesbytearray from sessioninfo");
            rs.next();
            Blob b = rs.getBlob("rulesbytearray");
            assertNotNull(b);
            
            KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            ProtobufMarshaller marshaller = new ProtobufMarshaller(builder.newKnowledgeBase(),new MarshallingConfigurationImpl());
            StatefulKnowledgeSession session = marshaller.unmarshall(b.getBinaryStream());
            assertNotNull(session);
            
            TimerManager timerManager = ((InternalProcessRuntime)((InternalKnowledgeRuntime)session).getProcessRuntime()).getTimerManager();
            assertNotNull(timerManager);
            
            Collection<TimerInstance> timers = timerManager.getTimers();
            assertNotNull(timers);
            assertEquals(5, timers.size());
            
            for (TimerInstance timerInstance : timers) {
                assertTrue(committedProcessInstanceIds.contains(timerInstance.getProcessInstanceId()));
            }
            
        } catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown");
        }
    }
    
    @Test
    public void testSerizliableTestsWithEngineRollback() {
        try {

            Environment env = KnowledgeBaseFactory.newEnvironment();
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
            System.out.println("Created JPA EntityManager");
            
            env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, emf );
            env.set( EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager() );
            TaskService taskService = new org.jbpm.task.service.TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
            Map<String, User> users = new HashMap<String, User>();
            users.put("Administrator", new User("Administrator"));
            users.put("john", new User("john"));
            Map<String, Group> groups = new HashMap<String, Group>();
            taskService.addUsersAndGroups(users, groups);
            org.jbpm.task.TaskService humanTaskClient = new LocalTaskService(taskService);;
            
            System.out.println("Task service created");
            
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(ResourceFactory.newClassPathResource("HumanTaskWithBoundaryTimer.bpmn"),ResourceType.BPMN2);
            if (kbuilder.getErrors()!=null){
                for(KnowledgeBuilderError error: kbuilder.getErrors()){
                    System.err.println(error.toString());
                }
            }
            System.out.println("BPMN process knowledge acquired");
            
            KnowledgeBase kbase = kbuilder.newKnowledgeBase();
            StatefulKnowledgeSession sesion = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
            System.out.println("Created knowledge session");
            
            LocalHTWorkItemHandler localHTWorkItemHandler = new LocalHTWorkItemHandler(humanTaskClient, sesion, OnErrorAction.RETHROW);
            localHTWorkItemHandler.connect();
            sesion.getWorkItemManager().registerWorkItemHandler("Human Task", localHTWorkItemHandler);
            System.out.println("Attached human task work item handler");
            List<Long> committedProcessInstanceIds = new ArrayList<Long>();
            for(int i=0;i<10;i++){
                if (i%2 == 0) {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("test", "john");
                    System.out.println("Creating process instance: "+ i);
                    ProcessInstance pi = sesion.startProcess("PROCESS_1", params);
                
                    committedProcessInstanceIds.add(pi.getId());
                
                } else {
                    try {
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("test", "test");
                        System.out.println("Creating process instance: "+ i);
                        ProcessInstance pi = sesion.startProcess("PROCESS_1", params);
                    } catch (Exception e) {
                        System.out.println("Process rolled back");
                    }
                }
            }
            
            Connection c = ds.getConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("select rulesbytearray from sessioninfo");
            rs.next();
            Blob b = rs.getBlob("rulesbytearray");
            assertNotNull(b);
            
            KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            ProtobufMarshaller marshaller = new ProtobufMarshaller(builder.newKnowledgeBase(),new MarshallingConfigurationImpl());
            StatefulKnowledgeSession session = marshaller.unmarshall(b.getBinaryStream());
            assertNotNull(session);
            
            TimerManager timerManager = ((InternalProcessRuntime)((InternalKnowledgeRuntime)session).getProcessRuntime()).getTimerManager();
            assertNotNull(timerManager);
            
            Collection<TimerInstance> timers = timerManager.getTimers();
            assertNotNull(timers);
            assertEquals(5, timers.size());
            
            for (TimerInstance timerInstance : timers) {
                assertTrue(committedProcessInstanceIds.contains(timerInstance.getProcessInstanceId()));
            }
            
        } catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown");
        }
    }
    
}