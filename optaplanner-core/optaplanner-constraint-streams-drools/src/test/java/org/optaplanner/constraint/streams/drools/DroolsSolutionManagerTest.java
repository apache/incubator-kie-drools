package org.optaplanner.constraint.streams.drools;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.solver.AbstractSolutionManagerTest;
import org.optaplanner.core.impl.testdata.domain.TestdataConstraintProvider;

final class DroolsSolutionManagerTest extends AbstractSolutionManagerTest {

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig() {
        return new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintProvider.class)
                .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
    }

}
