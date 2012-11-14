package org.jbpm.persistence.timer;

import static org.kie.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;
import static org.jbpm.test.JBPMHelper.*;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.kie.KnowledgeBase;
import org.drools.process.instance.WorkItemHandler;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.jbpm.test.JBPMHelper;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * See JBPM-3170/JBPM-3391
 */
public class TimerPersistenceTest extends JbpmJUnitTestCase {

    // General setup
    private static Logger testLogger = LoggerFactory.getLogger(TimerPersistenceTest.class);

    // Test processses
    private final static String DELAY_TIMER_FILE = "delayTimerEventProcess.bpmn";
    private final static String DELAY_TIMER_PROCESS = "DelayTimerEventProcess";
    
    private final static String PROCESS_FILE_NAME = "boundaryTimerProcess.bpmn";
    private final static String PROCESS_NAME = "BoundaryTimerEventProcess";
    private final static String WORK_ITEM_HANLDER_TASK = "Human Task";
    
    private final static String TIMER_FIRED_PROP = "timerFired";
    private final static String TIMER_FIRED_TIME_PROP = "afterTimerTime";

    public TimerPersistenceTest() { 
        super(true);
        this.setPersistence(true);
    }
    
    @Before
    public void setup() { 
        System.clearProperty(TIMER_FIRED_PROP);
        System.clearProperty(TIMER_FIRED_TIME_PROP);
    }
    
    @Test
    public void boundaryEventTimerAndCompleteHumanTaskWithoutPersistence() throws InterruptedException {
        this.setPersistence(false);
        
        // Setup session
        KnowledgeBase kbase = createKnowledgeBase(PROCESS_FILE_NAME);
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
       
        // Do stuff
        HumanTaskMockHandler humanTaskMockHandler = new HumanTaskMockHandler();
        ProcessInstance process = registerHTHandlerAndStartProcess(ksession, humanTaskMockHandler);
        
        sleepAndVerifyTimerRuns(process.getState());
        completeWork(ksession, humanTaskMockHandler);
   
        // The process reaches the end node
        int processState = process.getState();
        assertEquals("Expected process state to be " + processStateName[ProcessInstance.STATE_COMPLETED],
                ProcessInstance.STATE_COMPLETED, processState);
    }
    
    @Test
    public void boundaryEventTimerAndCompleteHumanTask() throws InterruptedException {
        /**
         * First we set up everything and start the process
         */
        KnowledgeBase kbase = createKnowledgeBase(PROCESS_FILE_NAME);
        StatefulKnowledgeSession ksession = newStatefulKnowledgeSession(kbase);
        int ksessionId = ksession.getId();
        assertTrue("session id not saved.", ksessionId > 0);
        
        HumanTaskMockHandler humanTaskMockHandler = new HumanTaskMockHandler();
        ProcessInstance process = registerHTHandlerAndStartProcess(ksession, humanTaskMockHandler);
        
        sleepAndVerifyTimerRuns(process.getState());
        completeWork(ksession, humanTaskMockHandler);
    
        // The process completes
        process = ksession.getProcessInstance(process.getId());
        assertNull("Expected process to have been completed and removed", process);
    }

    @Test
    @Ignore
    public void timerEventOutsideOfKnowledgeSessionTest() throws InterruptedException {
        /**
         * First we set up everything and start the process
         */
        KnowledgeBase kbase = createKnowledgeBase(DELAY_TIMER_FILE);
        StatefulKnowledgeSession ksession = JBPMHelper.newStatefulKnowledgeSession(kbase);
        int ksessionId = ksession.getId();
    
        // Start the process
        ProcessInstance process = ksession.startProcess(DELAY_TIMER_PROCESS);
        long processId = process.getId();
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());
    
        /**
         * Then we close down the session (imagine that the timer is for a week,
         * for example.)
         */
        EntityManagerFactory emf = (EntityManagerFactory) ksession.getEnvironment().get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        ksession.dispose();
        ksession = null;
        emf.close();
        emf = null;
    
        assertTrue("sesssion id has not been saved", ksessionId > 0);
        assertTrue("process id has not been saved", processId > 0);
        kbase = null;
        process = null;
    
        // The timer fires..
        int sleep = 2000;
        testLogger.debug("Sleeping " + sleep / 1000 + " seconds.");
        Thread.sleep(sleep);
        testLogger.debug("Awake!");
    
        assertTrue("The timer has not fired!", timerHasFired());
        assertTrue("The did not fire at the right time!", System.currentTimeMillis() > timerFiredTime());
        
        /**
         * Now we recreate a knowledge base and sesion and retrieve the process
         * to see what has happened
         */
        kbase = createKnowledgeBase(DELAY_TIMER_FILE);
        ksession = JBPMHelper.loadStatefulKnowledgeSession(kbase, ksessionId);
        assertNotNull("Could not retrieve session " + ksessionId, ksession);
    
        process = ksession.getProcessInstance(processId);
        assertNotNull("Process " + processId + "should have completed and been deleted", process);
    }

    @Test
    @Ignore
    public void boundaryEventTimerFiresAndCompleteHumanTaskWithoutKSession() throws InterruptedException {
        /**
         * First we set up everything and start the process
         */
        KnowledgeBase kbase = createKnowledgeBase(PROCESS_FILE_NAME);
        StatefulKnowledgeSession ksession = newStatefulKnowledgeSession(kbase);
        int ksessionId = ksession.getId();
        assertTrue("session id not saved.", ksessionId > 0);
        
        HumanTaskMockHandler humanTaskMockHandler = new HumanTaskMockHandler();
        ProcessInstance process = registerHTHandlerAndStartProcess(ksession, humanTaskMockHandler);
        
        int processState = process.getState();
        /**
         * DISPOSE OF THE KSESSION AND THE EMF
         * (imagine that the timer is for 1 week.. we can't keep the ksession and emf open that long.. 
         */
        EntityManagerFactory emf = (EntityManagerFactory) ksession.getEnvironment().get(ENTITY_MANAGER_FACTORY);
        ksession.dispose();
        ksession = null;
        emf.close();
        emf = null;
        
        sleepAndVerifyTimerRuns(processState);
    
        kbase = createKnowledgeBase(PROCESS_FILE_NAME);
        ksession = loadStatefulKnowledgeSession(kbase, ksessionId);
        
        completeWork(ksession, humanTaskMockHandler);
        
        // The process completes
        process = ksession.getProcessInstance(process.getId());
        assertNull("Expected process to have been completed and removed", process);
    }

    private ProcessInstance registerHTHandlerAndStartProcess(StatefulKnowledgeSession ksession, HumanTaskMockHandler humanTaskMockHandler) { 
        // Register Human Task Handler
        ksession.getWorkItemManager().registerWorkItemHandler(WORK_ITEM_HANLDER_TASK, humanTaskMockHandler);
    
        // Start the process 
        ProcessInstance process = ksession.startProcess(PROCESS_NAME);
        long processId = process.getId();
        assertTrue("process id not saved", processId > 0);
        
        // The process is in the Human Task waiting for its completion
        int processState = process.getState();
        assertEquals("Expected process state to be " + processStateName[ProcessInstance.STATE_ACTIVE] + " not "
                + processStateName[processState], ProcessInstance.STATE_ACTIVE, processState);
        
        return process;
    
    }

    private void completeWork(StatefulKnowledgeSession ksession, HumanTaskMockHandler humanTaskMockHandler) {
        assertTrue("The work item task handler does not have a work item!", humanTaskMockHandler.workItem != null);
        long workItemId = humanTaskMockHandler.workItem.getId();
        assertTrue("work item id not saved", workItemId > 0);
        
        // The Human Task is completed
        Map<String, Object> results = new HashMap<String, Object>();
        try {
            ksession.getWorkItemManager().completeWorkItem(workItemId, results);
        } catch (Exception e) {
            testLogger.warn("Work item could not be completed!");
            e.printStackTrace();
            fail(e.getClass().getSimpleName() + " thrown when completing work item: " + e.getMessage());
        }
    }

    private void sleepAndVerifyTimerRuns(int processState) throws InterruptedException { 
        // wait 3 seconds to see if the boss is notified
        if (processState == ProcessInstance.STATE_ACTIVE) {
            int sleep = 2000;
            testLogger.debug("Sleeping " + sleep / 1000 + " seconds.");
            Thread.sleep(sleep);
            testLogger.debug("Awake!");
        }
        
        long afterSleepTime = System.currentTimeMillis();
        assertTrue("The timer has not fired!", timerHasFired());
        assertTrue("The timer did not fire on time!", afterSleepTime > timerFiredTime() );
        int timerFiredCount = timerFiredCount();
        assertTrue("The timer only fired " + timerFiredCount + " times.", timerFiredCount >= 1 );
    }

    
    private boolean timerHasFired() { 
        String hasFired = System.getProperty(TIMER_FIRED_PROP);
        if( hasFired != null ) { 
            return true;
        }
        return false;
    }


    private int timerFiredCount() { 
        String timerFiredCount = System.getProperty(TIMER_FIRED_PROP);
        if( timerFiredCount == null ) { 
           return 0; 
        }
        return Integer.parseInt(timerFiredCount);
    }


    private long timerFiredTime() { 
        String timerFiredCount = System.getProperty(TIMER_FIRED_TIME_PROP);
        if( timerFiredCount == null ) { 
           return 0; 
        }
        return Long.parseLong(timerFiredCount);
    }


    private static class HumanTaskMockHandler implements WorkItemHandler {

        private org.kie.runtime.process.WorkItemManager workItemManager;
        private org.kie.runtime.process.WorkItem workItem;

        public void executeWorkItem(org.kie.runtime.process.WorkItem workItem, org.kie.runtime.process.WorkItemManager manager) {
            this.workItem = workItem;
            this.workItemManager = manager;
            System.out.println("Work completed!");
        }

        public void abortWorkItem(org.kie.runtime.process.WorkItem workItem, org.kie.runtime.process.WorkItemManager manager) {
            this.workItemManager.abortWorkItem(workItem.getId());
            System.out.println("Work aborted.");
        }

    }
}
