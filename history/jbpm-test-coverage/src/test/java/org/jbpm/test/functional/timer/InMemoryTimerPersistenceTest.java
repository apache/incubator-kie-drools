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

import static org.jbpm.test.JBPMHelper.processStateName;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.process.instance.WorkItemHandler;
import org.jbpm.test.JbpmTestCase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * See JBPM-3170/JBPM-3391
 */
public class InMemoryTimerPersistenceTest extends JbpmTestCase {

    // General setup
    private static final Logger logger = LoggerFactory.getLogger(InMemoryTimerPersistenceTest.class);

    // Test processses
    
    private final static String PROCESS_FILE_NAME = "org/jbpm/test/functional/timer/boundaryTimerProcess.bpmn";
    private final static String PROCESS_NAME = "BoundaryTimerEventProcess";
    private final static String WORK_ITEM_HANLDER_TASK = "Human Task";
    
    private final static String TIMER_FIRED_PROP = "timerFired";
    private final static String TIMER_FIRED_TIME_PROP = "afterTimerTime";

    public InMemoryTimerPersistenceTest() { 
        super(true, false);
    }
    
    @Before
    public void setup() { 
        System.clearProperty(TIMER_FIRED_PROP);
        System.clearProperty(TIMER_FIRED_TIME_PROP);
    }
    
    @Test
    public void boundaryEventTimerAndCompleteHumanTaskWithoutPersistence() throws InterruptedException {
        createRuntimeManager(PROCESS_FILE_NAME);
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
       
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
    

    private ProcessInstance registerHTHandlerAndStartProcess(KieSession ksession, HumanTaskMockHandler humanTaskMockHandler) { 
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

    private void completeWork(KieSession ksession, HumanTaskMockHandler humanTaskMockHandler) {
        assertTrue("The work item task handler does not have a work item!", humanTaskMockHandler.workItem != null);
        long workItemId = humanTaskMockHandler.workItem.getId();
        assertTrue("work item id not saved", workItemId > 0);
        
        // The Human Task is completed
        Map<String, Object> results = new HashMap<String, Object>();
        try {
            ksession.getWorkItemManager().completeWorkItem(workItemId, results);
        } catch (Exception e) {
            logger.warn("Work item could not be completed!");
            e.printStackTrace();
            fail(e.getClass().getSimpleName() + " thrown when completing work item: " + e.getMessage());
        }
    }

    private void sleepAndVerifyTimerRuns(int processState) throws InterruptedException { 
        // wait 3 seconds to see if the boss is notified
        if (processState == ProcessInstance.STATE_ACTIVE) {
            int sleep = 2000;
            logger.debug("Sleeping {} seconds", sleep / 1000);
            Thread.sleep(sleep);
            logger.debug("Awake!");
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

        private org.kie.api.runtime.process.WorkItemManager workItemManager;
        private org.kie.api.runtime.process.WorkItem workItem;

        public void executeWorkItem(org.kie.api.runtime.process.WorkItem workItem, org.kie.api.runtime.process.WorkItemManager manager) {
            this.workItem = workItem;
            this.workItemManager = manager;
            logger.debug("Work completed!");
        }

        public void abortWorkItem(org.kie.api.runtime.process.WorkItem workItem, org.kie.api.runtime.process.WorkItemManager manager) {
            this.workItemManager.abortWorkItem(workItem.getId());
            logger.debug("Work aborted.");
        }

    }
}
