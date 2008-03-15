/**
 * 
 */
package org.drools.concurrent;

import org.drools.WorkingMemory;
import org.drools.spi.AgendaFilter;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class FireAllRules
    implements
    Command,
    Future {
    private AgendaFilter     agendaFilter;
    private volatile boolean done;
    private Exception     e;

    public FireAllRules() {
    }

    public FireAllRules(final AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        agendaFilter    = (AgendaFilter)in.readObject();
        done            = in.readBoolean();
        e               = (Exception)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(agendaFilter);
        out.writeBoolean(done);
        out.writeObject(e);
    }

    public void execute(final WorkingMemory workingMemory) {
        try {
            workingMemory.fireAllRules( this.agendaFilter );
        } catch ( Exception e ) {
            this.e = e;
        }
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