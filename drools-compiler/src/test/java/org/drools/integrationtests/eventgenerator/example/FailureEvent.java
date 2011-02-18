package org.drools.integrationtests.eventgenerator.example;

import org.drools.integrationtests.eventgenerator.Event;

public class FailureEvent extends Event {

    /**
     * Special constructor for a failure event
     * @param parentId The id of the corresponding site, resource, ...
     */
    public FailureEvent(String parentId) {
        super(EventType.FAILURE, parentId);
    }

    /**
     * Special constructor for a faliure event
     * @param parentId The id of the corresponding site, resource, ...
     * @param start The start instance of the event.
     * @param end The end instance of the event.
     * @param parameters The event parameters.
     */
    public FailureEvent(String parentId, long start, long end) {
        super(EventType.FAILURE, parentId, start, end);
    }

}
