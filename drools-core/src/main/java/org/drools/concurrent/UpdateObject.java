/**
 * 
 */
package org.drools.concurrent;

import org.drools.FactHandle;
import org.drools.WorkingMemory;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

public class UpdateObject
    implements
    Command,
    Future {
    private FactHandle       factHandle;
    private Object           object;
    private volatile boolean done;
    private Exception     e;

    public UpdateObject() {
    }

    public UpdateObject(final FactHandle factHandle,
                        final Object object) {
        this.factHandle = factHandle;
        this.object = object;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        factHandle  = (FactHandle)in.readObject();
        object      = in.readObject();
        done        = in.readBoolean();
        e           = (Exception)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(factHandle);
        out.writeObject(object);
        out.writeBoolean(done);
        out.writeObject(e);
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

    public boolean exceptionThrown() {
        return e != null;
    }

    public Exception getException() {
        return this.e;
    }
}