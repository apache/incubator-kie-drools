package org.jbpm.persistence.timer;

import static junit.framework.Assert.*;
import static org.jbpm.test.JBPMHelper.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.persistence.util.LoggingPrintStream;
import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.h2.tools.Server;
import org.jbpm.test.KnowledgeSessionCleanup;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * Based on -
 * https://github.com/esteban-aliverti/JBPM-Samples/blob/master/Simple
 * -Event-Samples
 * /src/test/java/org/plugtree/training/jbpm/BoundaryTimerEventProcessTest.java
 * 
 * 
 */
public class BoundaryTimerEventProcessTest {

    // General setup
    private static Logger logger = LoggerFactory.getLogger(DelayTimerEventProcessTest.class);

    @Rule
    public KnowledgeSessionCleanup ksessionCleanup = new KnowledgeSessionCleanup();

    // Persistence/process
    private static PoolingDataSource pds = null;
    private static Server server = null;
    private final static String PROCESS_FILE_NAME = "boundaryTimerProcess.bpmn";
    private final static String PROCESS_NAME = "BoundaryTimerEventProcess";

    private StatefulKnowledgeSession ksession;
    private HumanTaskMockHandler humanTaskMockHandler;

    // Test specific
    private static long processId = -1;
    private static long workItemId = -1;

    @BeforeClass
    public static void beforeClass() {
        System.setOut(new LoggingPrintStream(System.out));
        pds = setupDataSource();
        server = startH2Server();
    }

    @Before
    public void before() throws IOException {
        KnowledgeBase kbase = createKnowledgeBase();
        ksession = newStatefulKnowledgeSession(kbase);

        // Register Human Task Handler
        humanTaskMockHandler = new HumanTaskMockHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskMockHandler);

        // Console log
        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);

    }

    /**
     * Creates a ksession from a kbase containing process definition
     * 
     * @return
     */
    public KnowledgeBase createKnowledgeBase() {
        // Create the kbuilder
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        // Add simpleProcess.bpmn to kbuilder
        kbuilder.add(new ClassPathResource(PROCESS_FILE_NAME), ResourceType.BPMN2);
        logger.debug("Compiling resources");

        // Check for errors
        if (kbuilder.hasErrors()) {
            if (kbuilder.getErrors().size() > 0) {
                for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                    logger.error("Error building kbase: " + error.getMessage());
                }
            }
            throw new RuntimeException("Error building kbase!");
        }

        // Create a knowledge base and add the generated package
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        return kbase;
    }

    @After
    public void after() {
        if (ksession != null) {
            ksession.dispose();
        }
    }

    @AfterClass
    public static void afterClass() {
        if (pds != null) {
            pds.close();
        }
        if (server != null) {
            server.stop();
            server.shutdown();
        }
        assertTrue("H2 tcp server has NOT been stopped!", server == null || !server.isRunning(true));
    }

    @Test
    public void boundaryEventTimerFiresStart() throws InterruptedException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("emailService", new ArrayList<String>());
        {
        // Start the process using its id
        ProcessInstance process = ksession.startProcess(PROCESS_NAME, parameters);
        processId = process.getId();
        assertTrue(processId > 0);
        
        // The process is in the Human Task waiting for its completion
        // The process is in the Human Task waiting for its completion
        int processState = process.getState();
        assertEquals("Expected process state to be " + processStateName[ProcessInstance.STATE_ACTIVE] + " not "
                + processStateName[processState], ProcessInstance.STATE_ACTIVE, processState);
        
        assertTrue("The work item task handler does not have a work item!", humanTaskMockHandler.workItem != null);
        workItemId = humanTaskMockHandler.workItem.getId();
        }
    }

    @Test
    public void boundaryEventTimerFiresFinish() throws InterruptedException {
        assertTrue("Process id has not been saved in previous test!", processId > 0);
        ProcessInstance process = ksession.getProcessInstance(processId);
        assertNotNull("Could not retrieve process " + processId, process);
    
        // wait 3 seconds to see if the boss is notified
        if (process.getState() == ProcessInstance.STATE_ACTIVE) {
            int sleep = 3000;
            logger.debug("Waiting " + sleep / 1000 + " seconds.");
            Thread.sleep(sleep);
            logger.debug("Doing work!");
        }
    
        // The Human Task is completed
        Map<String, Object> results = new HashMap<String, Object>();
        try {
            ksession.getWorkItemManager().completeWorkItem(workItemId, results);
        } catch (Exception e) {
            logger.warn("Work item could not be completed!");
            e.printStackTrace();
            fail(e.getClass().getSimpleName() + " thrown when completing work item: " + e.getMessage());
        }
    
        // The process reaches the end node
        int processState = process.getState();
        assertTrue("Expected process state to be " + processStateName[2] + " not " + processStateName[processState],
                ProcessInstance.STATE_COMPLETED == process.getState());
    
        // The boss should have been notified
        @SuppressWarnings("unchecked")
        List<String> emailService = (List<String>) ((WorkflowProcessInstance) process).getVariable("emailService");
        assertFalse("Timer did NOT fire!", emailService.isEmpty());
    }

    @Test
    public void controlCaseNoTimerFiredStart() throws InterruptedException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("emailService", new ArrayList<String>());

        // Start the process using its id
        ProcessInstance process = ksession.startProcess(PROCESS_NAME, parameters);
        processId = process.getId();
        assertTrue(processId > 0);

        // The process is in the Human Task waiting for its completion
        int processState = process.getState();
        assertEquals("Expected process state to be " + processStateName[ProcessInstance.STATE_ACTIVE] + " not "
                + processStateName[processState], ProcessInstance.STATE_ACTIVE, processState);
        
        assertTrue("The work item task handler does not have a work item!", humanTaskMockHandler.workItem != null);
        workItemId = humanTaskMockHandler.workItem.getId();
    }

    @Test
    public void controlCaseNoTimerFiredFinish() throws InterruptedException {
        assertTrue("Process id has not been saved in previous test!", processId > 0);
        ProcessInstance process = ksession.getProcessInstance(processId);
        assertNotNull("Could not retrieve process " + processId, process);

        // wait 5 seconds to complete the task
        if (process.getState() == ProcessInstance.STATE_ACTIVE) {
            double sleep = 500d;
            logger.debug("Waiting " + (sleep/1000) + " seconds.");
            Thread.sleep((int) sleep);
            logger.debug("Doing work!");
        }

        // The Human Task is completed
        Map<String, Object> results = new HashMap<String, Object>();
        try {
            ksession.getWorkItemManager().completeWorkItem(workItemId, results);
        } catch (Exception e) {
            logger.warn("Work item could not be completed!");
            e.printStackTrace();
            fail(e.getClass().getSimpleName() + " thrown when completing work item: " + e.getMessage());
        }

        // The process reaches the end node
        int processState = process.getState();
        assertTrue("Expected process state to be " + processStateName[2] + " not " + processStateName[processState],
                ProcessInstance.STATE_COMPLETED == process.getState());

        // The boss should NOT have been notified: no e-mail!
        @SuppressWarnings("unchecked")
        List<String> emailService = (List<String>) ((WorkflowProcessInstance) process).getVariable("emailService");
        assertTrue(emailService.isEmpty());
    }

    private static class HumanTaskMockHandler implements WorkItemHandler {

        private org.drools.runtime.process.WorkItemManager workItemManager;
        private org.drools.runtime.process.WorkItem workItem;

        public void executeWorkItem(org.drools.runtime.process.WorkItem workItem, org.drools.runtime.process.WorkItemManager manager) {
            this.workItem = workItem;
            this.workItemManager = manager;
        }

        public void abortWorkItem(org.drools.runtime.process.WorkItem workItem, org.drools.runtime.process.WorkItemManager manager) {
            this.workItemManager.abortWorkItem(workItem.getId());
        }

        /**
        public void completeWorkItem() {
            logger.debug("Completing work item: " + workItem.getName());
            this.workItemManager.completeWorkItem(workItem.getId(), null);
        }
        **/

    }
}
