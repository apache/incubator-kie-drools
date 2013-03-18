package org.jbpm.persistence;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.marshalling.impl.MarshallingConfigurationImpl;
import org.drools.core.marshalling.impl.ProtobufMarshaller;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.task.impl.model.UserImpl;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.task.api.TaskService;
import org.kie.internal.task.api.model.Group;
import org.kie.internal.task.api.model.TaskSummary;
import org.kie.internal.task.api.model.User;

import bitronix.tm.TransactionManagerServices;

public class SerializedTimerRollbackTest extends JbpmJUnitTestCase {

    public SerializedTimerRollbackTest() {
        super(true);
        setPersistence(true);
    }


    @Before
    public void setUp() throws Exception {
        super.setUp();
        try {
            UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
            ut.begin();
            EntityManager em = getEmf().createEntityManager();
            em.createQuery("delete from SessionInfo").executeUpdate();
            em.close();
            ut.commit();
        } catch (Exception e) {
            System.out.println(" >>> Something went wrong deleting the Session Info");
        }

    }

    @Test
    public void testSerizliableTestsWithExternalRollback() {
        try {

            TransactionManager tm = TransactionManagerServices.getTransactionManager();

            StatefulKnowledgeSession sesion = createKnowledgeSession("HumanTaskWithBoundaryTimer.bpmn");
            System.out.println("Created knowledge session");

            TaskService taskService = getTaskService(sesion);
            Map<String, User> users = new HashMap<String, User>();
            users.put("Administrator", new UserImpl("Administrator"));
            users.put("john", new UserImpl("john"));
            Map<String, Group> groups = new HashMap<String, Group>();
            taskService.addUsersAndGroups(users, groups);



            System.out.println("Attached human task work item handler");
            List<Long> committedProcessInstanceIds = new ArrayList<Long>();
            for (int i = 0; i < 10; i++) {
                tm.begin();
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("test", "john");
                System.out.println("Creating process instance: " + i);
                ProcessInstance pi = sesion.startProcess("PROCESS_1", params);
                if (i % 2 == 0) {
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
            ProtobufMarshaller marshaller = new ProtobufMarshaller(builder.newKnowledgeBase(), new MarshallingConfigurationImpl());
            StatefulKnowledgeSession session = marshaller.unmarshall(b.getBinaryStream());
            assertNotNull(session);

            TimerManager timerManager = ((InternalProcessRuntime) ((InternalKnowledgeRuntime) session).getProcessRuntime()).getTimerManager();
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
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown");
        }

    }

    @Test
    @Ignore
    public void testSerizliableTestsWithEngineRollback() {
        try {
    
            StatefulKnowledgeSession sesion = createKnowledgeSession("HumanTaskWithBoundaryTimer.bpmn");
            System.out.println("Created knowledge session");
             
            TaskService taskService = getTaskService(sesion);
            System.out.println("Task service created");
            Map<String, User> users = new HashMap<String, User>();
            users.put("Administrator", new UserImpl("Administrator"));
            users.put("john", new UserImpl("john"));
            Map<String, Group> groups = new HashMap<String, Group>();
            taskService.addUsersAndGroups(users, groups);


            System.out.println("Attached human task work item handler");
            List<Long> committedProcessInstanceIds = new ArrayList<Long>();
            for (int i = 0; i < 10; i++) {
                if (i % 2 == 0) {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("test", "john");
                    System.out.println("Creating process instance: " + i);
                    ProcessInstance pi = sesion.startProcess("PROCESS_1", params);

                    committedProcessInstanceIds.add(pi.getId());

                } else {
                    try {
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("test", "test");
                        System.out.println("Creating process instance: " + i);
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
            ProtobufMarshaller marshaller = new ProtobufMarshaller(builder.newKnowledgeBase(), new MarshallingConfigurationImpl());
            StatefulKnowledgeSession session = marshaller.unmarshall(b.getBinaryStream());
            assertNotNull(session);

            TimerManager timerManager = ((InternalProcessRuntime) ((InternalKnowledgeRuntime) session).getProcessRuntime()).getTimerManager();
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
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown");
        }
    }
}
