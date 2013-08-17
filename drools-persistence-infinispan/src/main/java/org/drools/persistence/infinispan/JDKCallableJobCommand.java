package org.drools.persistence.infinispan;

import org.drools.core.command.impl.GenericCommand;
import org.kie.internal.command.Context;

public class JDKCallableJobCommand
    implements
    GenericCommand<Void> {

    private static final long   serialVersionUID = 4L;

    private InfinispanTimerJobInstance job;

    public JDKCallableJobCommand(InfinispanTimerJobInstance job) {
        this.job = job;
    }

    public Void execute(Context context) {
        try {
            return job.internalCall();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

}
