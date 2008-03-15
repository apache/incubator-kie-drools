/**
 * 
 */
package org.drools.concurrent;

import org.drools.FactHandle;
import org.drools.WorkingMemory;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class RetractObject
    implements
    Command,
    Future {
    private FactHandle       factHandle;
    private volatile boolean done;
    private Exception     e;

    public RetractObject() {
    }

    public RetractObject(final FactHandle factHandle) {
        this.factHandle = factHandle;
    }

    public void execute(final WorkingMemory workingMemory) {
        workingMemory.retract( this.factHandle );
        this.done = true;

    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        factHandle  = (FactHandle)in.readObject();
        done        = in.readBoolean();
        e           = (Exception)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(factHandle);
        out.writeBoolean(done);
        out.writeObject(e);
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