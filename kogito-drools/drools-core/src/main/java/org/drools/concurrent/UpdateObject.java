/**
 * 
 */
package org.drools.concurrent;

import org.drools.FactHandle;
import org.drools.WorkingMemory;

public class UpdateObject
    implements
    Command,
    Future {
    private FactHandle       factHandle;
    private Object           object;
    private volatile boolean done;

    public UpdateObject(final FactHandle factHandle,
                        final Object object) {
        this.factHandle = factHandle;
        this.object = object;
    }

    public void execute(final WorkingMemory workingMemory) {
        workingMemory.update( this.factHandle,
                                    this.object );
        this.done = true;
    }

    public Object getObject() {
        return null;
    }

    public boolean isDone() {
        return this.done == true;
    }
}