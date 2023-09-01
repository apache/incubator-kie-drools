package org.drools.kiesession.audit;

/**
 * An event filter that can be used to filter log events.
 */
public interface ILogEventFilter {

    /**
     * Returns whether the given event should be filtered from the event log or not.
     * @param event The log event
     * @return Whether the event should be filtered from the event log or not.
     */
    boolean acceptEvent(LogEvent event);

}
