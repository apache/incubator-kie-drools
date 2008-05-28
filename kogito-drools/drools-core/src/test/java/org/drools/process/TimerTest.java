package org.drools.process;

import junit.framework.TestCase;

import org.drools.RuleBaseFactory;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.process.core.timer.Timer;
import org.drools.process.instance.timer.TimerManager;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;

public class TimerTest extends TestCase {

	private int counter = 0;

	public void testTimer() {
        AbstractRuleBase ruleBase = (AbstractRuleBase) RuleBaseFactory.newRuleBase();
        InternalWorkingMemory workingMemory = new ReteooWorkingMemory(1, ruleBase);
        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance() {
        	public void timerTriggered(Timer timer) {
        		System.out.println("Timer " + timer.getId() + " triggered");
        		counter++;
        	}
        };
        processInstance.setId(1234);
        workingMemory.addProcessInstance(processInstance);
        TimerManager timerManager = workingMemory.getTimerManager();
        Timer timer = new Timer();
        timerManager.registerTimer(timer, processInstance);
        try {
        	Thread.sleep(500);
        } catch (InterruptedException e) {
        	// do nothing
        }
        assertEquals(1, counter);
        
        counter = 0;
        timer = new Timer();
        timer.setDelay(1000);
        timerManager.registerTimer(timer, processInstance);
        assertEquals(0, counter);
        try {
        	Thread.sleep(2000);
        } catch (InterruptedException e) {
        	// do nothing
        }
        assertEquals(1, counter);
        
        counter = 0;
        timer = new Timer();
        timer.setDelay(1000);
        timer.setPeriod(1000);
        timerManager.registerTimer(timer, processInstance);
        assertEquals(0, counter);
        try {
        	Thread.sleep(5500);
        } catch (InterruptedException e) {
        	// do nothing
        }
        assertEquals(5, counter);
        timerManager.cancelTimer(timer);
        try {
        	Thread.sleep(2000);
        } catch (InterruptedException e) {
        	// do nothing
        }
        assertEquals(5, counter);
	}
	
}
