package org.optaplanner.core.impl.constructionheuristic.placer;

import org.optaplanner.core.impl.phase.event.PhaseLifecycleSupport;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract superclass for {@link EntityPlacer}.
 *
 * @see EntityPlacer
 */
public abstract class AbstractEntityPlacer<Solution_> implements EntityPlacer<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected PhaseLifecycleSupport<Solution_> phaseLifecycleSupport = new PhaseLifecycleSupport<>();

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        phaseLifecycleSupport.fireSolvingStarted(solverScope);
    }

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        phaseLifecycleSupport.firePhaseStarted(phaseScope);
    }

    @Override
    public void stepStarted(AbstractStepScope<Solution_> stepScope) {
        phaseLifecycleSupport.fireStepStarted(stepScope);
    }

    @Override
    public void stepEnded(AbstractStepScope<Solution_> stepScope) {
        phaseLifecycleSupport.fireStepEnded(stepScope);
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        phaseLifecycleSupport.firePhaseEnded(phaseScope);
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        phaseLifecycleSupport.fireSolvingEnded(solverScope);
    }

}
