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
    private Exception       e;

    public AssertObject(final Object object) {
        this.object = object;
    }

    public void execute(final WorkingMemory workingMemory) {
        try {
            this.result = workingMemory.insert( this.object );
        } catch ( Exception e ) {
            this.e = e;
        }
    }

    public Object getObject() {
        return this.result;
    }    

    public boolean isDone() {
        return this.result != null;
    }
    
    public boolean exceptionThrown() {
        return e != null;
    }
    
    public Exception getException() {
        return this.e;
    }
}