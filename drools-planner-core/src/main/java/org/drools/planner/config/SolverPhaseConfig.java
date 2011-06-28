package org.drools.planner.config;

import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.solver.SolverPhase;

public abstract class SolverPhaseConfig {

    public abstract SolverPhase buildSolverPhase(EnvironmentMode environmentMode, ScoreDefinition scoreDefinition);

    public void inherit(SolverPhaseConfig inheritedConfig) {
    }

}
