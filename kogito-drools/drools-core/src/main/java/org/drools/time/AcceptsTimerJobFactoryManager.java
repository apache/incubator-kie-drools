package org.drools.time;

import org.drools.time.impl.TimerJobFactoryManager;

public interface AcceptsTimerJobFactoryManager {
    public void setTimerJobFactoryManager(TimerJobFactoryManager timerJobFactoryManager);
    
    public TimerJobFactoryManager getTimerJobFactoryManager();
}
