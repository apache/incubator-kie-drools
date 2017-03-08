package org.drools.persistence.mapdb;

import org.drools.core.command.impl.ExecutableCommand;
import org.kie.api.runtime.Context;

public class JDKCallableJobCommand
    implements
    ExecutableCommand<Void> {

    private static final long   serialVersionUID = 4L;

    private MapDBTimerJobInstance job;

    public JDKCallableJobCommand(MapDBTimerJobInstance job) {
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
