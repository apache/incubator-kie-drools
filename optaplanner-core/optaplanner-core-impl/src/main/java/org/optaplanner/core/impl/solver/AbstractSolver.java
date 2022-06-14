package org.optaplanner.core.impl.solver;

import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.impl.partitionedsearch.PartitionSolver;
import org.optaplanner.core.impl.phase.AbstractPhase;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleSupport;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.event.SolverEventSupport;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common code between {@link DefaultSolver} and child solvers (such as {@link PartitionSolver}).
 * <p>
 * Do not create a new child {@link Solver} to implement a new heuristic or metaheuristic,
 * just use a new {@link Phase} for that.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see Solver
 * @see DefaultSolver
 */
public abstract class AbstractSolver<Solution_> implements Solver<Solution_> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final SolverEventSupport<Solution_> solverEventSupport = new SolverEventSupport<>(this);
    private final PhaseLifecycleSupport<Solution_> phaseLifecycleSupport = new PhaseLifecycleSupport<>();

    protected final BestSolutionRecaller<Solution_> bestSolutionRecaller;
    // Note that the DefaultSolver.basicPlumbingTermination is a component of this termination.
    // Called "solverTermination" to clearly distinguish from "phaseTermination" inside AbstractPhase.
    protected final Termination<Solution_> solverTermination;
    protected final List<Phase<Solution_>> phaseList;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public AbstractSolver(BestSolutionRecaller<Solution_> bestSolutionRecaller, Termination<Solution_> solverTermination,
            List<Phase<Solution_>> phaseList) {
        this.bestSolutionRecaller = bestSolutionRecaller;
        this.solverTermination = solverTermination;
        bestSolutionRecaller.setSolverEventSupport(solverEventSupport);
        this.phaseList = phaseList;
        phaseList.forEach(phase -> ((AbstractPhase<Solution_>) phase).setSolver(this));
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void solvingStarted(SolverScope<Solution_> solverScope) {
        solverScope.setWorkingSolutionFromBestSolution();
        bestSolutionRecaller.solvingStarted(solverScope);
        solverTermination.solvingStarted(solverScope);
        phaseLifecycleSupport.fireSolvingStarted(solverScope);
        for (Phase<Solution_> phase : phaseList) {
            phase.solvingStarted(solverScope);
        }
    }

    protected void runPhases(SolverScope<Solution_> solverScope) {
        if (!solverScope.getSolutionDescriptor().hasMovableEntities(solverScope.getScoreDirector())) {
            logger.info("Skipped all phases ({}): out of {} planning entities, none are movable (non-pinned).",
                    phaseList.size(),
                    solverScope.getSolutionDescriptor().getEntityCount(solverScope.getWorkingSolution()));
            return;
        }
        Iterator<Phase<Solution_>> it = phaseList.iterator();
        while (!solverTermination.isSolverTerminated(solverScope) && it.hasNext()) {
            Phase<Solution_> phase = it.next();
            phase.solve(solverScope);
            // If there is a next phase, it starts from the best solution, which might differ from the working solution.
            // If there isn't, no need to planning clone the best solution to the working solution.
            if (it.hasNext()) {
                solverScope.setWorkingSolutionFromBestSolution();
            }
        }
    }

    public void solvingEnded(SolverScope<Solution_> solverScope) {
        for (Phase<Solution_> phase : phaseList) {
            phase.solvingEnded(solverScope);
        }
        bestSolutionRecaller.solvingEnded(solverScope);
        solverTermination.solvingEnded(solverScope);
        phaseLifecycleSupport.fireSolvingEnded(solverScope);
    }

    public void solvingError(SolverScope<Solution_> solverScope, Exception exception) {
        phaseLifecycleSupport.fireSolvingError(solverScope, exception);
        for (Phase<Solution_> phase : phaseList) {
            phase.solvingError(solverScope, exception);
        }
    }

    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        bestSolutionRecaller.phaseStarted(phaseScope);
        phaseLifecycleSupport.firePhaseStarted(phaseScope);
        solverTermination.phaseStarted(phaseScope);
        // Do not propagate to phases; the active phase does that for itself and they should not propagate further.
    }

    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        bestSolutionRecaller.phaseEnded(phaseScope);
        phaseLifecycleSupport.firePhaseEnded(phaseScope);
        solverTermination.phaseEnded(phaseScope);
        // Do not propagate to phases; the active phase does that for itself and they should not propagate further.
    }

    public void stepStarted(AbstractStepScope<Solution_> stepScope) {
        bestSolutionRecaller.stepStarted(stepScope);
        phaseLifecycleSupport.fireStepStarted(stepScope);
        solverTermination.stepStarted(stepScope);
        // Do not propagate to phases; the active phase does that for itself and they should not propagate further.
    }

    public void stepEnded(AbstractStepScope<Solution_> stepScope) {
        bestSolutionRecaller.stepEnded(stepScope);
        phaseLifecycleSupport.fireStepEnded(stepScope);
        solverTermination.stepEnded(stepScope);
        // Do not propagate to phases; the active phase does that for itself and they should not propagate further.
    }

    // ************************************************************************
    // Event listeners
    // ************************************************************************

    @Override
    public void addEventListener(SolverEventListener<Solution_> eventListener) {
        solverEventSupport.addEventListener(eventListener);
    }

    @Override
    public void removeEventListener(SolverEventListener<Solution_> eventListener) {
        solverEventSupport.removeEventListener(eventListener);
    }

    /**
     * Add a {@link PhaseLifecycleListener} that is notified
     * of {@link PhaseLifecycleListener#solvingStarted(SolverScope) solving} events
     * and also of the {@link PhaseLifecycleListener#phaseStarted(AbstractPhaseScope) phase}
     * and the {@link PhaseLifecycleListener#stepStarted(AbstractStepScope) step} starting/ending events of all phases.
     * <p>
     * To get notified for only 1 phase, use {@link Phase#addPhaseLifecycleListener(PhaseLifecycleListener)} instead.
     *
     * @param phaseLifecycleListener never null
     */
    public void addPhaseLifecycleListener(PhaseLifecycleListener<Solution_> phaseLifecycleListener) {
        phaseLifecycleSupport.addEventListener(phaseLifecycleListener);
    }

    /**
     * @param phaseLifecycleListener never null
     * @see #addPhaseLifecycleListener(PhaseLifecycleListener)
     */
    public void removePhaseLifecycleListener(PhaseLifecycleListener<Solution_> phaseLifecycleListener) {
        phaseLifecycleSupport.removeEventListener(phaseLifecycleListener);
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public BestSolutionRecaller<Solution_> getBestSolutionRecaller() {
        return bestSolutionRecaller;
    }

    public List<Phase<Solution_>> getPhaseList() {
        return phaseList;
    }

}
