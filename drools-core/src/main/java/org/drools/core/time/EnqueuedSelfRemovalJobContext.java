package org.drools.core.time;

import java.util.Map;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.time.impl.TimerJobInstance;

public class EnqueuedSelfRemovalJobContext extends SelfRemovalJobContext {
    public EnqueuedSelfRemovalJobContext( JobContext jobContext, Map<Long, TimerJobInstance> timerInstances ) {
        super( jobContext, timerInstances );
    }

    @Override
    public void remove() {
        getReteEvaluator().addPropagation( new PropagationEntry.AbstractPropagationEntry() {
            @Override
            public void internalExecute(ReteEvaluator reteEvaluator) {
                timerInstances.remove( jobContext.getJobHandle().getId() );
            }
        } );
    }
}
