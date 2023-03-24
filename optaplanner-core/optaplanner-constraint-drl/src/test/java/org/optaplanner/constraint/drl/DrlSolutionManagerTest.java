package org.optaplanner.constraint.drl;

import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.solver.AbstractSolutionManagerTest;

final class DrlSolutionManagerTest extends AbstractSolutionManagerTest {

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig() {
        return new ScoreDirectorFactoryConfig()
                .withScoreDrls("org/optaplanner/constraint/drl/solutionManagerDroolsConstraints.drl");
    }

}
