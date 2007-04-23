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

    public FireAllRules(final AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public void execute(final WorkingMemory workingMemory) {
        workingMemory.fireAllRules( this.agendaFilter );
        this.done = true;
    }

    public Object getObject() {
        return null;
    }

    public boolean isDone() {
        return this.done;
    }
}