/**
 * 
 */
package org.drools.concurrent;

import org.drools.WorkingMemory;
import org.drools.spi.AgendaFilter;

public class FireAllRules
    implements
    Command,
    Future {
    private AgendaFilter     agendaFilter;
    private volatile boolean done;
    private Exception     e;

    public FireAllRules(final AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
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