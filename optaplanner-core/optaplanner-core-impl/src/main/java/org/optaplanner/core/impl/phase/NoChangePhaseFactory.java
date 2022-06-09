package org.optaplanner.core.impl.phase;

import org.optaplanner.core.config.phase.NoChangePhaseConfig;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;

public class NoChangePhaseFactory<Solution_> extends AbstractPhaseFactory<Solution_, NoChangePhaseConfig> {

    public NoChangePhaseFactory(NoChangePhaseConfig phaseConfig) {
        super(phaseConfig);
    }

    @Override
    public NoChangePhase<Solution_> buildPhase(int phaseIndex, HeuristicConfigPolicy<Solution_> solverConfigPolicy,
            BestSolutionRecaller<Solution_> bestSolutionRecaller, Termination<Solution_> solverTermination) {
        HeuristicConfigPolicy<Solution_> phaseConfigPolicy = solverConfigPolicy.createPhaseConfigPolicy();
        return new NoChangePhase.Builder<>(phaseIndex, solverConfigPolicy.getLogIndentation(),
                buildPhaseTermination(phaseConfigPolicy, solverTermination)).build();
    }
}
