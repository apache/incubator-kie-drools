package org.optaplanner.constraint.streams.drools;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class ScoreDirectorFactoryFactoryTest {

    @Test
    void constraintStreamsDroolsWithAlphaNetworkCompilationEnabled() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintProvider.class)
                .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS)
                .withDroolsAlphaNetworkCompilationEnabled(true);
        InnerScoreDirectorFactory<TestdataSolution, SimpleScore> uncastScoreDirectorFactory =
                new DroolsConstraintStreamScoreDirectorFactoryService<TestdataSolution, SimpleScore>()
                        .buildScoreDirectorFactory(null, TestdataSolution.buildSolutionDescriptor(), config)
                        .get();
        assertThat(uncastScoreDirectorFactory).isInstanceOf(DroolsConstraintStreamScoreDirectorFactory.class);
        DroolsConstraintStreamScoreDirectorFactory<TestdataSolution, SimpleScore> scoreDirectorFactory =
                (DroolsConstraintStreamScoreDirectorFactory<TestdataSolution, SimpleScore>) uncastScoreDirectorFactory;
        assertThat(scoreDirectorFactory.isDroolsAlphaNetworkCompilationEnabled()).isTrue();
    }

    @Test
    void constraintStreamsDroolsWithAlphaNetworkCompilationDisabled() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintProvider.class)
                .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS)
                .withDroolsAlphaNetworkCompilationEnabled(false);
        InnerScoreDirectorFactory<TestdataSolution, SimpleScore> uncastScoreDirectorFactory =
                new DroolsConstraintStreamScoreDirectorFactoryService<TestdataSolution, SimpleScore>()
                        .buildScoreDirectorFactory(null, TestdataSolution.buildSolutionDescriptor(), config)
                        .get();
        assertThat(uncastScoreDirectorFactory).isInstanceOf(DroolsConstraintStreamScoreDirectorFactory.class);
        DroolsConstraintStreamScoreDirectorFactory<TestdataSolution, SimpleScore> scoreDirectorFactory =
                (DroolsConstraintStreamScoreDirectorFactory<TestdataSolution, SimpleScore>) uncastScoreDirectorFactory;
        assertThat(scoreDirectorFactory.isDroolsAlphaNetworkCompilationEnabled()).isFalse();
    }

    public static class TestdataConstraintProvider implements ConstraintProvider {
        @Override
        public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
            return new Constraint[0];
        }
    }
}
