/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process;

import java.util.concurrent.ExecutorService;

import junit.framework.Assert;

import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.runtime.StatefulKnowledgeSession;

public class TimerTest extends Assert {

	private int counter = 0;
	   
    static {
        ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
    }
    
    @Test
    @Ignore
	public void testTimer() {
//        AbstractRuleBase ruleBase = (AbstractRuleBase) RuleBaseFactory.newRuleBase();
//        ExecutorService executorService = new DefaultExecutorService();
//        final StatefulSession workingMemory = new ReteooStatefulSession(1, ruleBase, executorService);
//        executorService.setCommandExecutor( new CommandExecutor( workingMemory ) );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        final StatefulKnowledgeSession workingMemory = kbase.newStatefulKnowledgeSession(); 

        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance() {
			private static final long serialVersionUID = 510l;
			public void signalEvent(String type, Object event) {
        		if ("timerTriggered".equals(type)) {
        			TimerInstance timer = (TimerInstance) event;
            		System.out.println("Timer " + timer.getId() + " triggered");
            		counter++;
        		}
        	}
        };
        processInstance.setKnowledgeRuntime(((InternalWorkingMemory) workingMemory).getKnowledgeRuntime());
        processInstance.setId(1234);
        InternalProcessRuntime processRuntime = ((InternalProcessRuntime) ((InternalWorkingMemory) workingMemory).getProcessRuntime());
        processRuntime.getProcessInstanceManager().internalAddProcessInstance(processInstance);

        new Thread(new Runnable() {
			public void run() {
	        	workingMemory.fireUntilHalt();       	
			}
        }).start();

        TimerManager timerManager = ((InternalProcessRuntime) ((InternalWorkingMemory) workingMemory).getProcessRuntime()).getTimerManager();
        TimerInstance timer = new TimerInstance();
        timerManager.registerTimer(timer, processInstance);
        try {
        	Thread.sleep(1000);
        } catch (InterruptedException e) {
        	// do nothing
        }
        assertEquals(1, counter);
        
        counter = 0;
        timer = new TimerInstance();
        timer.setDelay(500);
        timerManager.registerTimer(timer, processInstance);
        assertEquals(0, counter);
        try {
        	Thread.sleep(1000);
        } catch (InterruptedException e) {
        	// do nothing
        }
        assertEquals(1, counter);
        
        counter = 0;
        timer = new TimerInstance();
        timer.setDelay(500);
        timer.setPeriod(300);
        timerManager.registerTimer(timer, processInstance);
        assertEquals(0, counter);
        try {
        	Thread.sleep(700);
        } catch (InterruptedException e) {
        	// do nothing
        }
        assertEquals(1, counter);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // do nothing
        }
        // we can't know exactly how many times this will fire as timers are not precise, but should be atleast 4
        assertTrue( counter >= 4 );
        
        timerManager.cancelTimer(timer.getId());
        int lastCount = counter;
        try {            
        	Thread.sleep(1000);
        } catch (InterruptedException e) {
        	// do nothing
        }
        assertEquals(lastCount, counter);
	}
	
}
