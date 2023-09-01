package org.drools.persistence.jpa;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;

public class JDKCallableJobCommand
    implements
    ExecutableCommand<Void> {

    private static final long   serialVersionUID = 4L;

    private JpaTimerJobInstance job;

    public JDKCallableJobCommand(JpaTimerJobInstance job) {
        this.job = job;
    }

    public Void execute(Context context) {
        try {
            return job.internalCall();
        } catch ( Exception e ) {
            throw new RuntimeException(e);
        }
    }

}
