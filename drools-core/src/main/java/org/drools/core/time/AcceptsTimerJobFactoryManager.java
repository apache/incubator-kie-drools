package org.drools.core.time;

import org.drools.core.time.impl.TimerJobFactoryManager;

public interface AcceptsTimerJobFactoryManager {
    public void setTimerJobFactoryManager(TimerJobFactoryManager timerJobFactoryManager);
    
    public TimerJobFactoryManager getTimerJobFactoryManager();
}
