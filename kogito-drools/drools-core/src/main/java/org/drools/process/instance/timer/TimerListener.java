package org.drools.process.instance.timer;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface TimerListener {
    
    void timerTriggered(TimerInstance timer);
    
}
