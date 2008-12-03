package org.drools.event.io;

import org.drools.io.Resource;


public interface ResourceModifiedEvent {

    public abstract Resource getResource();

    public abstract long getTime();

}