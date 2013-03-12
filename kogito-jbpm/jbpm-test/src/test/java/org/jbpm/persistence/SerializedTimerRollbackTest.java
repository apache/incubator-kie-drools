package org.jbpm.persistence;

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


import org.drools.common.InternalKnowledgeRuntime;
import org.drools.marshalling.impl.MarshallingConfigurationImpl;
import org.drools.marshalling.impl.ProtobufMarshaller;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.task.Group;
import org.jbpm.task.User;

import org.jbpm.task.query.TaskSummary;

import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;

import bitronix.tm.TransactionManagerServices;
import javax.transaction.TransactionManager;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Ignore;


public class SerializedTimerRollbackTest extends JbpmJUnitTestCase {


    public SerializedTimerRollbackTest() {
        super(true);
    }
    
    
    
//    @Before
//    public void setup() {
//        ds = new PoolingDataSource();
//        ds.setUniqueName( "jdbc/jbpm-ds" );
//        ds.setClassName( "org.h2.jdbcx.JdbcDataSource" );
//        ds.setMaxPoolSize( 3 );
//        ds.setAllowLocalTransactions( true );
//        ds.getDriverProperties().put( "user", "sa" );
//        ds.getDriverProperties().put( "password", "sasa" );
//        ds.getDriverProperties().put( "URL", "jdbc:h2:mem:mydb" );
//        ds.init();
//
//        
//        emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
//        try {
//            UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
//            ut.begin();
//            EntityManager em = emf.createEntityManager().getEntityManagerFactory().createEntityManager();
//            em.createQuery("delete from SessionInfo").executeUpdate();
//            em.close();
//            ut.commit();
//        } catch (Exception e) {
//            
//        }
//    }
   
    @Ignore
    @Test
    public void testSerizliableTestsWithExternalRollback() {
        try {

           TransactionManager tm = TransactionManagerServices.getTransactionManager();
            
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
            StatefulKnowledgeSession sesion = createKnowledgeSession(kbase);
            System.out.println("Created knowledge session");
            
            TaskServiceEntryPoint taskService = getTaskService(sesion);
            Map<String, User> users = new HashMap<String, User>();
            users.put("Administrator", new User("Administrator"));
            users.put("john", new User("john"));
            Map<String, Group> groups = new HashMap<String, Group>();
            taskService.addUsersAndGroups(users, groups);
            
            
            
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
            
            Connection c = getDs().getConnection();
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
                sesion.abortProcessInstance(timerInstance.getProcessInstanceId());
            }
            
            List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
            assertEquals(0, tasks.size());
        } catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown");
        }
        
    }
    @Ignore
    @Test
    public void testSerizliableTestsWithEngineRollback() {
        try {

           
            
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
            StatefulKnowledgeSession sesion = createKnowledgeSession(kbase);
            System.out.println("Created knowledge session");
            
             TaskServiceEntryPoint taskService = getTaskService(sesion);
            Map<String, User> users = new HashMap<String, User>();
            users.put("Administrator", new User("Administrator"));
            users.put("john", new User("john"));
            Map<String, Group> groups = new HashMap<String, Group>();
            taskService.addUsersAndGroups(users, groups);
            
            
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
            
            Connection c = getDs().getConnection();
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
                sesion.abortProcessInstance(timerInstance.getProcessInstanceId());
            }
            List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
            assertEquals(0, tasks.size());
        } catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown");
        }
    }
    
}