package org.optaplanner.core.impl.score.director.incremental;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorSemanticsTest;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryFactory;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfigurationSolution;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintWeighIncrementalScoreCalculator;

final class IncrementalScoreDirectorSemanticsTest extends AbstractScoreDirectorSemanticsTest {

    @Override
    protected InnerScoreDirectorFactory<TestdataConstraintConfigurationSolution, SimpleScore>
            buildInnerScoreDirectorFactory(SolutionDescriptor<TestdataConstraintConfigurationSolution> solutionDescriptor) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig()
                .withIncrementalScoreCalculatorClass(TestdataConstraintWeighIncrementalScoreCalculator.class);
        ScoreDirectorFactoryFactory<TestdataConstraintConfigurationSolution, SimpleScore> scoreDirectorFactoryFactory =
                new ScoreDirectorFactoryFactory<>(scoreDirectorFactoryConfig);
        return scoreDirectorFactoryFactory.buildScoreDirectorFactory(getClass().getClassLoader(),
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor);
    }

}
