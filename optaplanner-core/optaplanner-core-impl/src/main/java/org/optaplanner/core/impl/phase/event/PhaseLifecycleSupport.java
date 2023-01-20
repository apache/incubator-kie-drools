package org.optaplanner.core.impl.phase.event;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.event.AbstractEventSupport;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * Internal API.
 */
public final class PhaseLifecycleSupport<Solution_> extends AbstractEventSupport<PhaseLifecycleListener<Solution_>> {

    public void fireSolvingStarted(SolverScope<Solution_> solverScope) {
        for (PhaseLifecycleListener<Solution_> listener : getEventListeners()) {
            listener.solvingStarted(solverScope);
        }
    }

    public void firePhaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        for (PhaseLifecycleListener<Solution_> listener : getEventListeners()) {
            listener.phaseStarted(phaseScope);
        }
    }

    public void fireStepStarted(AbstractStepScope<Solution_> stepScope) {
        for (PhaseLifecycleListener<Solution_> listener : getEventListeners()) {
            listener.stepStarted(stepScope);
        }
    }

    public void fireStepEnded(AbstractStepScope<Solution_> stepScope) {
        for (PhaseLifecycleListener<Solution_> listener : getEventListeners()) {
            listener.stepEnded(stepScope);
        }
    }

    public void firePhaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        for (PhaseLifecycleListener<Solution_> listener : getEventListeners()) {
            listener.phaseEnded(phaseScope);
        }
    }

    public void fireSolvingEnded(SolverScope<Solution_> solverScope) {
        for (PhaseLifecycleListener<Solution_> listener : getEventListeners()) {
            listener.solvingEnded(solverScope);
        }
    }

    public void fireSolvingError(SolverScope<Solution_> solverScope, Exception exception) {
        for (PhaseLifecycleListener<Solution_> listener : getEventListeners()) {
            listener.solvingError(solverScope, exception);
        }
    }
}
