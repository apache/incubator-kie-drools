package org.drools.planner.config.phase;

import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.core.phase.SolverPhase;
import org.drools.planner.core.score.definition.ScoreDefinition;

public abstract class SolverPhaseConfig {

    public abstract SolverPhase buildSolverPhase(EnvironmentMode environmentMode, ScoreDefinition scoreDefinition);

    public void inherit(SolverPhaseConfig inheritedConfig) {
    }

}
