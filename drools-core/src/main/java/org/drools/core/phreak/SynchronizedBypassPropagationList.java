package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;

public class SynchronizedBypassPropagationList extends SynchronizedPropagationList {

    public SynchronizedBypassPropagationList(InternalWorkingMemory workingMemory) {
        super(workingMemory);
    }

    @Override
    public void addEntry(final PropagationEntry propagationEntry) {
        workingMemory.getAgenda().executeTask( new ExecutableEntry() {
           @Override
           public void execute() {
               propagationEntry.execute(workingMemory);
           }

           @Override
           public void enqueue() {
               internalAddEntry( propagationEntry );
           }
        });
        notifyHalt();
    }

    @Override
    public void onEngineInactive() {
        flush();
    }
}
