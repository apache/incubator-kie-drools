package org.kie.api.time;

import java.util.concurrent.TimeUnit;


/**
 * A clock interface for the implementation of pseudo clocks,
 * that are clocks where the user have control over the actual
 * clock working.
 *  
 * Pseudo clocks are particularly useful for simulations, 
 * "what if" scenario runs, and for tests.
 */
public interface SessionPseudoClock extends SessionClock {

    /**
     * Advances the clock time in the specified unit amount. 
     * 
     * @param amount the amount of units to advance in the clock
     * @param unit the used time unit
     * @return the current absolute timestamp
     */
    long advanceTime( long amount, TimeUnit unit );

}
