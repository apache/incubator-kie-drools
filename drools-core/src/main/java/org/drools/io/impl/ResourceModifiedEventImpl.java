/**
 * 
 */
package org.drools.io.impl;

import org.drools.event.io.ResourceModifiedEvent;
import org.drools.io.Resource;

public class ResourceModifiedEventImpl implements ResourceModifiedEvent {
    private Resource resource;
    private long     time;

    public ResourceModifiedEventImpl(Resource resource,
                                 long time) {
        super();
        this.resource = resource;
        this.time = time;
    }

    /* (non-Javadoc)
     * @see org.drools.io.impl.ResourceModifiedEvent#getResource()
     */
    public Resource getResource() {
        return resource;
    }

    /* (non-Javadoc)
     * @see org.drools.io.impl.ResourceModifiedEvent#getTime()
     */
    public long getTime() {
        return time;
    }

}