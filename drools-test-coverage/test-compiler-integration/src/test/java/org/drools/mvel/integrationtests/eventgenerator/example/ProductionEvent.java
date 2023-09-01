package org.drools.mvel.integrationtests.eventgenerator.example;

import org.drools.mvel.integrationtests.eventgenerator.Event;

public class ProductionEvent extends Event {

    /**
     * Special constructor for a production event
     * @param parentId The id of the corresponding site, resource, ...
     */
    public ProductionEvent(String parentId) {
        super(EventType.PRODUCTION, parentId);
    }

    /**
     * Special constructor for a production event
     * @param parentId The id of the corresponding site, resource, ...
     * @param start The start instance of the event.
     * @param end The end instance of the event.
     * @param parameters The event parameters.
     */
    public ProductionEvent(String parentId, long start, long end) {
        super(EventType.PRODUCTION, parentId, start, end);
    }
}
