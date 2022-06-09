package org.optaplanner.core.impl.phase.event;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.event.AbstractEventSupport;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * Internal API.
 */
public class PhaseLifecycleSupport<Solution_> extends AbstractEventSupport<PhaseLifecycleListener<Solution_>> {

    public void fireSolvingStarted(SolverScope<Solution_> solverScope) {
        for (PhaseLifecycleListener<Solution_> phaseLifecycleListener : eventListenerSet) {
            phaseLifecycleListener.solvingStarted(solverScope);
        }
    }

    public void firePhaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        for (PhaseLifecycleListener<Solution_> phaseLifecycleListener : eventListenerSet) {
            phaseLifecycleListener.phaseStarted(phaseScope);
        }
    }

    public void fireStepStarted(AbstractStepScope<Solution_> stepScope) {
        for (PhaseLifecycleListener<Solution_> phaseLifecycleListener : eventListenerSet) {
            phaseLifecycleListener.stepStarted(stepScope);
        }
    }

    public void fireStepEnded(AbstractStepScope<Solution_> stepScope) {
        for (PhaseLifecycleListener<Solution_> phaseLifecycleListener : eventListenerSet) {
            phaseLifecycleListener.stepEnded(stepScope);
        }
    }

    public void firePhaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        for (PhaseLifecycleListener<Solution_> phaseLifecycleListener : eventListenerSet) {
            phaseLifecycleListener.phaseEnded(phaseScope);
        }
    }

    public void fireSolvingEnded(SolverScope<Solution_> solverScope) {
        for (PhaseLifecycleListener<Solution_> phaseLifecycleListener : eventListenerSet) {
            phaseLifecycleListener.solvingEnded(solverScope);
        }
    }

}
