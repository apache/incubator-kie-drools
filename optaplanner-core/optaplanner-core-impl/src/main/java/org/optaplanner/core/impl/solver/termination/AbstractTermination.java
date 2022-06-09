package org.optaplanner.core.impl.solver.termination;

import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link Termination}.
 */
public abstract class AbstractTermination<Solution_> extends PhaseLifecycleListenerAdapter<Solution_>
        implements Termination<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public Termination<Solution_> createChildThreadTermination(SolverScope<Solution_> solverScope,
            ChildThreadType childThreadType) {
        throw new UnsupportedOperationException("This terminationClass (" + getClass()
                + ") does not yet support being used in child threads of type (" + childThreadType + ").");
    }

}
