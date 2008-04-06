package org.drools.process.instance.timer;

import org.drools.process.core.timer.Timer;

public interface TimerListener {
    
    void timerTriggered(Timer timer);
    
}
