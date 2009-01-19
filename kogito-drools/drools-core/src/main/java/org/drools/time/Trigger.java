package org.drools.time;

import java.util.Date;

/**
 * A trigger interface for scheduling jobs
 */
public interface Trigger {
    
    /**
     * This method is used to query the trigger about the existence of a
     * possible next fire time, but WITHOUT changing any internal state 
     * of the trigger.  In other words, this method MUST not have side
     * effects. As an analogy, if a trigger was a stack, this method would
     * be the equivalent of a "peek()" call.
     * 
     * @return the Date of the next fire time or null if there is no  
     *         next fire time.
     */
    public Date hasNextFireTime();
    
    /**
     * This method returns the date of the next fire time and updates
     * the internal state of the Trigger to the following fire time
     * if one exists. As an analogy, if a trigger was a stack, this method
     * would be the equivalent of a "pop()" call.
     * 
     * @return the Date of the next fire time or null if there is no
     *         next fire time.
     */
    public Date nextFireTime();
}
