package org.drools.core.phreak;

import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.core.common.ReteEvaluator;

public class SynchronizedBypassPropagationList extends SynchronizedPropagationList {

    private AtomicBoolean executing = new AtomicBoolean( false );

    public SynchronizedBypassPropagationList(ReteEvaluator reteEvaluator) {
        super(reteEvaluator);
    }

    @Override
    public void addEntry(final PropagationEntry propagationEntry) {
        reteEvaluator.getActivationsManager().executeTask( new ExecutableEntry() {
           @Override
           public void execute() {
               if (executing.compareAndSet( false, true )) {
                   try {
                       propagationEntry.execute( reteEvaluator );
                   } finally {
                       executing.set( false );
                       flush();
                   }
               } else {
                   doAdd();
               }
           }

           @Override
           public void enqueue() {
               doAdd();
           }

            private void doAdd() {
                internalAddEntry( propagationEntry );
           }
        });
        notifyWaitOnRest();
    }

    @Override
    public void flush() {
        if (!executing.get()) {
            PropagationEntry head = takeAll();
            while (head != null) {
                flush( head );
                head = takeAll();
            }
        }
    }

    @Override
    public void onEngineInactive() {
        flush();
    }
}
