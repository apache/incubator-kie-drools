package org.jbpm.test.persistence;

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
import org.jbpm.services.task.impl.model.UserImpl;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.task.api.InternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.TransactionManagerServices;

public class SerializedTimerRollbackTest extends JbpmJUnitTestCase {

    private static final Logger logger = LoggerFactory.getLogger(SerializedTimerRollbackTest.class);
    
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
            logger.error("Something went wrong deleting the Session Info", e);
        }

    }

    @Test
    public void testSerizliableTestsWithExternalRollback() {
        try {

            TransactionManager tm = TransactionManagerServices.getTransactionManager();

            KieSession sesion = createKnowledgeSession("HumanTaskWithBoundaryTimer.bpmn");
            logger.debug("Created knowledge session");

            TaskService taskService = getTaskService();
            Map<String, User> users = new HashMap<String, User>();
            users.put("Administrator", new UserImpl("Administrator"));
            users.put("john", new UserImpl("john"));
            Map<String, Group> groups = new HashMap<String, Group>();
            ((InternalTaskService) taskService).addUsersAndGroups(users, groups);



            logger.debug("Attached human task work item handler");
            List<Long> committedProcessInstanceIds = new ArrayList<Long>();
            for (int i = 0; i < 10; i++) {
                tm.begin();
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("test", "john");
                logger.debug("Creating process instance: {}", i);
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
//    @Ignore
    public void testSerizliableTestsWithEngineRollback() {
        try {
    
            KieSession sesion = createKnowledgeSession("HumanTaskWithBoundaryTimer.bpmn");
            logger.debug("Created knowledge session");
             
            TaskService taskService = getTaskService();
            logger.debug("Task service created");
            Map<String, User> users = new HashMap<String, User>();
            users.put("Administrator", new UserImpl("Administrator"));
            users.put("john", new UserImpl("john"));
            Map<String, Group> groups = new HashMap<String, Group>();
            ((InternalTaskService) taskService).addUsersAndGroups(users, groups);


            logger.debug("Attached human task work item handler");
            List<Long> committedProcessInstanceIds = new ArrayList<Long>();
            for (int i = 0; i < 10; i++) {
                if (i % 2 == 0) {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("test", "john");
                    logger.debug("Creating process instance: {}", i);
                    ProcessInstance pi = sesion.startProcess("PROCESS_1", params);

                    committedProcessInstanceIds.add(pi.getId());

                } else {
                    try {
                        Map<String, Object> params = new HashMap<String, Object>();
                        // set test variable to null so engine will rollback 
                        params.put("test", null);
                        logger.debug("Creating process instance: {}", i);
                        ProcessInstance pi = sesion.startProcess("PROCESS_1", params);
                    } catch (Exception e) {
                        logger.debug("Process rolled back");
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
