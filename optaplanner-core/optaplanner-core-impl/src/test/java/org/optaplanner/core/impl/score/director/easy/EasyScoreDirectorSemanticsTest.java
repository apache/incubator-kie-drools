package org.optaplanner.core.impl.score.director.easy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorSemanticsTest;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfigurationSolution;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintWeightEasyScoreCalculator;

final class EasyScoreDirectorSemanticsTest extends AbstractScoreDirectorSemanticsTest {

    @Override
    protected InnerScoreDirectorFactory<TestdataConstraintConfigurationSolution, SimpleScore>
            buildInnerScoreDirectorFactory(SolutionDescriptor<TestdataConstraintConfigurationSolution> solutionDescriptor) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig()
                .withEasyScoreCalculatorClass(TestdataConstraintWeightEasyScoreCalculator.class);
        ScoreDirectorFactoryFactory<TestdataConstraintConfigurationSolution, SimpleScore> scoreDirectorFactoryFactory =
                new ScoreDirectorFactoryFactory<>(scoreDirectorFactoryConfig);
        return scoreDirectorFactoryFactory.buildScoreDirectorFactory(getClass().getClassLoader(),
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor);
    }

    @Test
    void easyScoreCalculatorWithCustomProperties() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig();
        config.setEasyScoreCalculatorClass(TestCustomPropertiesEasyScoreCalculator.class);
        HashMap<String, String> customProperties = new HashMap<>();
        customProperties.put("stringProperty", "string 1");
        customProperties.put("intProperty", "7");
        config.setEasyScoreCalculatorCustomProperties(customProperties);

        EasyScoreDirector<TestdataSolution, ?> scoreDirector =
                (EasyScoreDirector<TestdataSolution, ?>) buildTestdataScoreDirectoryFactory(config).buildScoreDirector();
        TestCustomPropertiesEasyScoreCalculator scoreCalculator =
                (TestCustomPropertiesEasyScoreCalculator) scoreDirector
                        .getEasyScoreCalculator();
        assertThat(scoreCalculator.getStringProperty()).isEqualTo("string 1");
        assertThat(scoreCalculator.getIntProperty()).isEqualTo(7);
    }

    private <Score_ extends Score<Score_>> ScoreDirectorFactory<TestdataSolution> buildTestdataScoreDirectoryFactory(
            ScoreDirectorFactoryConfig config, EnvironmentMode environmentMode) {
        return new ScoreDirectorFactoryFactory<TestdataSolution, Score_>(config)
                .buildScoreDirectorFactory(getClass().getClassLoader(), environmentMode,
                        TestdataSolution.buildSolutionDescriptor());
    }

    private ScoreDirectorFactory<TestdataSolution> buildTestdataScoreDirectoryFactory(ScoreDirectorFactoryConfig config) {
        return buildTestdataScoreDirectoryFactory(config, EnvironmentMode.REPRODUCIBLE);
    }

    public static class TestCustomPropertiesEasyScoreCalculator
            implements EasyScoreCalculator<TestdataSolution, SimpleScore> {

        private String stringProperty;
        private int intProperty;

        public String getStringProperty() {
            return stringProperty;
        }

        @SuppressWarnings("unused")
        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public int getIntProperty() {
            return intProperty;
        }

        @SuppressWarnings("unused")
        public void setIntProperty(int intProperty) {
            this.intProperty = intProperty;
        }

        @Override
        public SimpleScore calculateScore(TestdataSolution testdataSolution) {
            return SimpleScore.ZERO;
        }
    }

}
