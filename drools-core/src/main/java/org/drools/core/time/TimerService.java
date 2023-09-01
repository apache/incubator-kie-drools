package org.drools.core.time;

import java.util.Collection;

import org.drools.core.time.impl.TimerJobFactoryManager;
import org.drools.core.time.impl.TimerJobInstance;

/**
 * An interface for all timer service implementations used in a drools session.
 */
public interface TimerService extends SchedulerService {
    
    /**
     * Returns the current time from the scheduler clock
     * 
     * @return the current timestamp
     */
    long getCurrentTime();

    /**
     * Reset this service
     */
    void reset();

    /**
     * Shuts the service down
     */
    void shutdown();

    /**
     * Returns the number of time units (usually ms) to
     * the next scheduled job
     * 
     * @return the number of time units until the next scheduled job or -1 if
     *         there is no job scheduled
     */
    long getTimeToNextJob();
    
    /**
     * This method may return null for some TimerService implementations that do not want the overhead of maintain this.
     * @return
     */
    Collection<TimerJobInstance> getTimerJobInstances(long id);

    void setTimerJobFactoryManager(TimerJobFactoryManager timerJobFactoryManager);

    TimerJobFactoryManager getTimerJobFactoryManager();
}
