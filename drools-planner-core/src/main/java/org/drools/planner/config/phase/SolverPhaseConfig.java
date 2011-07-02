package org.drools.planner.config.phase;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.termination.TerminationConfig;
import org.drools.planner.core.localsearch.DefaultLocalSearchSolverPhase;
import org.drools.planner.core.phase.AbstractSolverPhase;
import org.drools.planner.core.phase.SolverPhase;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.termination.PhaseToSolverTerminationBridge;
import org.drools.planner.core.termination.Termination;

public abstract class SolverPhaseConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    @XStreamAlias("termination")
    private TerminationConfig terminationConfig = new TerminationConfig();

    public TerminationConfig getTerminationConfig() {
        return terminationConfig;
    }

    public void setTerminationConfig(TerminationConfig terminationConfig) {
        this.terminationConfig = terminationConfig;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public abstract SolverPhase buildSolverPhase(EnvironmentMode environmentMode, ScoreDefinition scoreDefinition,
            Termination solverTermination);

    protected void configureSolverPhase(AbstractSolverPhase solverPhase,
            EnvironmentMode environmentMode, ScoreDefinition scoreDefinition, Termination solverTermination) {
        solverPhase.setTermination(terminationConfig.buildTermination(scoreDefinition,
                new PhaseToSolverTerminationBridge(solverTermination)));
    }

    public void inherit(SolverPhaseConfig inheritedConfig) {
        if (terminationConfig == null) {
            terminationConfig = inheritedConfig.getTerminationConfig();
        } else if (inheritedConfig.getTerminationConfig() != null) {
            terminationConfig.inherit(inheritedConfig.getTerminationConfig());
        }
    }

}
