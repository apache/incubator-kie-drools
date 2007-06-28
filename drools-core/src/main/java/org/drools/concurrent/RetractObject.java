/**
 * 
 */
package org.drools.concurrent;

import org.drools.FactHandle;
import org.drools.WorkingMemory;

public class RetractObject
    implements
    Command,
    Future {
    private FactHandle       factHandle;
    private volatile boolean done;
    private Exception     e;

    public RetractObject(final FactHandle factHandle) {
        this.factHandle = factHandle;
    }

    public void execute(final WorkingMemory workingMemory) {
        workingMemory.retract( this.factHandle );
        this.done = true;

    }

    public Object getObject() {
        return null;
    }

    public boolean isDone() {
        return this.done;
    }
    
    public boolean exceptionThrown() {
        return e != null;
    }
    
    public Exception getException() {
        return this.e;
    }    
}