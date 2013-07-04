package org.optaplanner.core.config.heuristic.policy;

import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

public class HeuristicConfigPolicy {

    private final EnvironmentMode environmentMode;
    private final SolutionDescriptor solutionDescriptor;
    private final ScoreDefinition scoreDefinition;

    private boolean initializedChainedValueFilterEnabled = false;

    public HeuristicConfigPolicy(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            ScoreDefinition scoreDefinition) {
        this.environmentMode = environmentMode;
        this.solutionDescriptor = solutionDescriptor;
        this.scoreDefinition = scoreDefinition;
    }

    public EnvironmentMode getEnvironmentMode() {
        return environmentMode;
    }

    public SolutionDescriptor getSolutionDescriptor() {
        return solutionDescriptor;
    }

    public ScoreDefinition getScoreDefinition() {
        return scoreDefinition;
    }

    public boolean isInitializedChainedValueFilterEnabled() {
        return initializedChainedValueFilterEnabled;
    }

    public void setInitializedChainedValueFilterEnabled(boolean initializedChainedValueFilterEnabled) {
        this.initializedChainedValueFilterEnabled = initializedChainedValueFilterEnabled;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public HeuristicConfigPolicy createPhaseConfigPolicy() {
        return new HeuristicConfigPolicy(environmentMode, solutionDescriptor, scoreDefinition);
    }

}
