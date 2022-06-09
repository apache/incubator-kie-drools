package org.optaplanner.core.impl.phase;

import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.partitionedsearch.PartitionedSearchPhaseConfig;
import org.optaplanner.core.config.phase.NoChangePhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.impl.constructionheuristic.DefaultConstructionHeuristicPhaseFactory;
import org.optaplanner.core.impl.exhaustivesearch.DefaultExhaustiveSearchPhaseFactory;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.localsearch.DefaultLocalSearchPhaseFactory;
import org.optaplanner.core.impl.partitionedsearch.DefaultPartitionedSearchPhaseFactory;
import org.optaplanner.core.impl.phase.custom.DefaultCustomPhaseFactory;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;

public interface PhaseFactory<Solution_> {

    static <Solution_> PhaseFactory<Solution_> create(PhaseConfig<?> phaseConfig) {
        if (LocalSearchPhaseConfig.class.isAssignableFrom(phaseConfig.getClass())) {
            return new DefaultLocalSearchPhaseFactory<>((LocalSearchPhaseConfig) phaseConfig);
        } else if (ConstructionHeuristicPhaseConfig.class.isAssignableFrom(phaseConfig.getClass())) {
            return new DefaultConstructionHeuristicPhaseFactory<>((ConstructionHeuristicPhaseConfig) phaseConfig);
        } else if (PartitionedSearchPhaseConfig.class.isAssignableFrom(phaseConfig.getClass())) {
            return new DefaultPartitionedSearchPhaseFactory<>((PartitionedSearchPhaseConfig) phaseConfig);
        } else if (CustomPhaseConfig.class.isAssignableFrom(phaseConfig.getClass())) {
            return new DefaultCustomPhaseFactory<>((CustomPhaseConfig) phaseConfig);
        } else if (ExhaustiveSearchPhaseConfig.class.isAssignableFrom(phaseConfig.getClass())) {
            return new DefaultExhaustiveSearchPhaseFactory<>((ExhaustiveSearchPhaseConfig) phaseConfig);
        } else if (NoChangePhaseConfig.class.isAssignableFrom(phaseConfig.getClass())) {
            return new NoChangePhaseFactory<>((NoChangePhaseConfig) phaseConfig);
        } else {
            throw new IllegalArgumentException(String.format("Unknown %s type: (%s).",
                    PhaseConfig.class.getSimpleName(), phaseConfig.getClass().getName()));
        }
    }

    Phase<Solution_> buildPhase(int phaseIndex, HeuristicConfigPolicy<Solution_> solverConfigPolicy,
            BestSolutionRecaller<Solution_> bestSolutionRecaller, Termination<Solution_> solverTermination);
}
