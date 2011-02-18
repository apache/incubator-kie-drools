/**
 * 
 */
package org.drools.integrationtests.eventgenerator.example;

import org.drools.integrationtests.eventgenerator.Event;


/**
 * @author Matthias Groch
 *
 */
public class HeartbeatEvent extends Event {

    /**
     * Special constructor for a heartbeat event
     * @param parentId The id of the corresponding site, resource, ...
     */
    public HeartbeatEvent(String parentId) {
        super(EventType.HEARTBEAT, parentId);
    }

    /**
     * Special constructor for a heartbeat event
     * @param parentId The id of the corresponding site, resource, ...
     * @param start The start instance of the event.
     * @param end The end instance of the event.
     * @param parameters The event parameters.
     */
    public HeartbeatEvent(String parentId, long start, long end) {
        super(EventType.HEARTBEAT, parentId, start, end);
    }
}
