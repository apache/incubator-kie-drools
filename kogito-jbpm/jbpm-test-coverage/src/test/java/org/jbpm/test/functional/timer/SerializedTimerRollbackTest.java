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

package org.jbpm.test.functional.timer;

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
import org.jbpm.test.JbpmTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class SerializedTimerRollbackTest extends JbpmTestCase {

    private static final Logger logger = LoggerFactory.getLogger(SerializedTimerRollbackTest.class);
    
    public SerializedTimerRollbackTest() {
        super(true, true);
    }


    @Before
    public void setUp() throws Exception {
        super.setUp();
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        try {
            ut.begin();
            EntityManager em = getEmf().createEntityManager();
            em.createQuery("delete from SessionInfo").executeUpdate();
            em.close();
            ut.commit();
        } catch (Exception e) {
            ut.rollback();
            logger.error("Something went wrong deleting the Session Info", e);
        }

    }

    @Test
    public void testSerizliableTestsWithExternalRollback() {
        try {
            createRuntimeManager("org/jbpm/test/functional/timer/HumanTaskWithBoundaryTimer.bpmn");
            RuntimeEngine runtimeEngine = getRuntimeEngine();
            KieSession ksession = runtimeEngine.getKieSession();
            TaskService taskService = runtimeEngine.getTaskService();
            logger.debug("Created knowledge session");
            
            TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();

            List<Long> committedProcessInstanceIds = new ArrayList<Long>();
            for (int i = 0; i < 10; i++) {
                tm.begin();
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("test", "john");
                logger.debug("Creating process instance: {}", i);
                ProcessInstance pi = ksession.startProcess("PROCESS_1", params);
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
            ProtobufMarshaller marshaller = new ProtobufMarshaller(builder.newKieBase(), new MarshallingConfigurationImpl());
            StatefulKnowledgeSession session = marshaller.unmarshall(b.getBinaryStream());
            assertNotNull(session);

            TimerManager timerManager = ((InternalProcessRuntime) ((InternalKnowledgeRuntime) session).getProcessRuntime()).getTimerManager();
            assertNotNull(timerManager);

            Collection<TimerInstance> timers = timerManager.getTimers();
            assertNotNull(timers);
            assertEquals(5, timers.size());

            for (TimerInstance timerInstance : timers) {
                assertTrue(committedProcessInstanceIds.contains(timerInstance.getProcessInstanceId()));
                ksession.abortProcessInstance(timerInstance.getProcessInstanceId());
            }

            List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
            assertEquals(0, tasks.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown");
        }

    }

    @Test
    public void testSerizliableTestsWithEngineRollback() {
        try {
    
            createRuntimeManager("org/jbpm/test/functional/timer/HumanTaskWithBoundaryTimer.bpmn");
            RuntimeEngine runtimeEngine = getRuntimeEngine();
            KieSession ksession = runtimeEngine.getKieSession();
            logger.debug("Created knowledge session");
            TaskService taskService = runtimeEngine.getTaskService();
            logger.debug("Task service created");

            List<Long> committedProcessInstanceIds = new ArrayList<Long>();
            for (int i = 0; i < 10; i++) {
                if (i % 2 == 0) {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("test", "john");
                    logger.debug("Creating process instance: {}", i);
                    ProcessInstance pi = ksession.startProcess("PROCESS_1", params);

                    committedProcessInstanceIds.add(pi.getId());

                } else {
                    try {
                        Map<String, Object> params = new HashMap<String, Object>();
                        // set test variable to null so engine will rollback 
                        params.put("test", null);
                        logger.debug("Creating process instance: {}", i);
                        ksession.startProcess("PROCESS_1", params);
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
            ProtobufMarshaller marshaller = new ProtobufMarshaller(builder.newKieBase(), new MarshallingConfigurationImpl());
            StatefulKnowledgeSession session = marshaller.unmarshall(b.getBinaryStream());
            assertNotNull(session);

            TimerManager timerManager = ((InternalProcessRuntime) ((InternalKnowledgeRuntime) session).getProcessRuntime()).getTimerManager();
            assertNotNull(timerManager);

            Collection<TimerInstance> timers = timerManager.getTimers();
            assertNotNull(timers);
            assertEquals(5, timers.size());

            for (TimerInstance timerInstance : timers) {
                assertTrue(committedProcessInstanceIds.contains(timerInstance.getProcessInstanceId()));
                ksession.abortProcessInstance(timerInstance.getProcessInstanceId());
            }
            List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
            assertEquals(0, tasks.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown");
        }
    }
}
