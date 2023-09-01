package org.kie.api.time;

/**
 * A clock interface that all engine clocks must implement
 */
public interface SessionClock {

    /**
     * Returns the current time. There is no semantics attached
     * to the long return value, so it will depend on the actual
     * implementation. For instance, for a real time clock it may be
     * milliseconds.
     *
     * @return The current time. The unit of the time, depends on
     * the actual clock implementation.
     */
    long getCurrentTime();

}
