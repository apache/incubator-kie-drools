/**
 * 
 */
package org.drools.concurrent;

import org.drools.WorkingMemory;

public class AssertObject
    implements
    Command,
    Future {
    private Object          object;
    private volatile Object result;

    public AssertObject(final Object object) {
        this.object = object;
    }

    public void execute(final WorkingMemory workingMemory) {
        this.result = workingMemory.insert( this.object );
    }

    public Object getObject() {
        return this.result;
    }

    public boolean isDone() {
        return this.result != null;
    }
}