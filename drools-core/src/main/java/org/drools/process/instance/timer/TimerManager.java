package org.drools.process.instance.timer;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import org.drools.WorkingMemory;
import org.drools.process.core.timer.Timer;
import org.drools.process.instance.ProcessInstance;

public class TimerManager {
    
    private Map<Timer, Long> timers = new HashMap<Timer, Long>();
    private Map<Timer, java.util.Timer> utilTimers = new HashMap<Timer, java.util.Timer>();
    private long timerId = 0;
    
    private WorkingMemory workingMemory;
    
    public TimerManager(WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }
    
    public void registerTimer(final Timer timer, ProcessInstance processInstance) {
        timer.setId(++timerId);
        timers.put(timer, processInstance.getId());
        TimerTask timerTask = new TimerTask() {
            public void run() {
                timerTriggered(timer);
            }
        };
        java.util.Timer utilTimer = new java.util.Timer();
        utilTimers.put(timer, utilTimer);
        if (timer.getPeriod() > 0) {
            utilTimer.schedule(
                timerTask, 
                timer.getDelay(), 
                timer.getPeriod());
        } else {
            utilTimer.schedule(
                timerTask, 
                timer.getPeriod());
        }
    }
    
    public void cancelTimer(Timer timer) {
        java.util.Timer utilTimer = utilTimers.get(timer);
        if (utilTimer == null) {
            throw new IllegalArgumentException(
                "Could not find timer implementation for timer " + timer);
        }
        utilTimer.cancel();
        utilTimers.remove(timer);
        timers.remove(timer);
    }
    
    public void timerTriggered(Timer timer) {
        Long processInstanceId = timers.get(timer);
        if (processInstanceId == null) {
            throw new IllegalArgumentException(
                "Could not find process instance for timer " + timer);
        }
        ProcessInstance processInstance = workingMemory.getProcessInstance(processInstanceId);
        // process instance may have finished already
        if (processInstance != null) {
            processInstance.timerTriggered(timer);
        }
        if (timer.getPeriod() == 0) {
            utilTimers.remove(timer);
            timers.remove(timer);
        }
    }

}
