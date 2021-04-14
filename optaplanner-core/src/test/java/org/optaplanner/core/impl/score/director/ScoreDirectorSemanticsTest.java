/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.director;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfiguration;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfigurationSolution;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintWeighIncrementalScoreCalculator;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintWeightConstraintProvider;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintWeightEasyScoreCalculator;

public class ScoreDirectorSemanticsTest {

    private final SolutionDescriptor<TestdataConstraintConfigurationSolution> solutionDescriptor =
            TestdataConstraintConfigurationSolution.buildSolutionDescriptor();

    @EnumSource(ScoreDirectorType.class)
    @ParameterizedTest
    void independentScoreDirectors(ScoreDirectorType scoreDirectorType) {
        InnerScoreDirectorFactory<TestdataConstraintConfigurationSolution, SimpleScore> scoreDirectorFactory =
                scoreDirectorType.buildScoreDirectorFactory(solutionDescriptor);

        // Create first score director, calculate score.
        TestdataConstraintConfigurationSolution solution1 =
                TestdataConstraintConfigurationSolution.generateSolution(1, 1);
        InnerScoreDirector<TestdataConstraintConfigurationSolution, SimpleScore> scoreDirector1 =
                scoreDirectorFactory.buildScoreDirector(false, false);
        scoreDirector1.setWorkingSolution(solution1);
        SimpleScore score1 = scoreDirector1.calculateScore();
        assertThat(score1).isEqualTo(SimpleScore.of(1));

        // Create second score director, calculate score.
        TestdataConstraintConfigurationSolution solution2 =
                TestdataConstraintConfigurationSolution.generateSolution(2, 2);
        InnerScoreDirector<TestdataConstraintConfigurationSolution, SimpleScore> scoreDirector2 =
                scoreDirectorFactory.buildScoreDirector(false, false);
        scoreDirector2.setWorkingSolution(solution2);
        SimpleScore score2 = scoreDirector2.calculateScore();
        assertThat(score2).isEqualTo(SimpleScore.of(2));

        // Ensure that the second score director did not influence the first.
        assertThat(scoreDirector1.calculateScore()).isEqualTo(SimpleScore.of(1));

        // Make a change on the second score director, ensure it did not affect the first.
        TestdataEntity entity = solution2.getEntityList().get(1);
        scoreDirector2.beforeEntityRemoved(entity);
        solution2.getEntityList().remove(entity);
        scoreDirector2.afterEntityRemoved(entity);
        scoreDirector2.triggerVariableListeners();
        assertThat(scoreDirector2.calculateScore()).isEqualTo(SimpleScore.of(1));
        assertThat(scoreDirector1.calculateScore()).isEqualTo(SimpleScore.of(1));

        // Add the same entity to the first score director, ensure it did not affect the second.
        scoreDirector1.beforeEntityAdded(entity);
        solution1.getEntityList().add(entity);
        scoreDirector1.afterEntityAdded(entity);
        scoreDirector1.triggerVariableListeners();
        assertThat(scoreDirector1.calculateScore()).isEqualTo(SimpleScore.of(2));
        assertThat(scoreDirector2.calculateScore()).isEqualTo(SimpleScore.of(1));
    }

    @EnumSource(ScoreDirectorType.class)
    @ParameterizedTest
    void solutionBasedScoreWeights(ScoreDirectorType scoreDirectorType) {
        InnerScoreDirectorFactory<TestdataConstraintConfigurationSolution, SimpleScore> scoreDirectorFactory =
                scoreDirectorType.buildScoreDirectorFactory(solutionDescriptor);

        // Create score director, calculate score.
        TestdataConstraintConfigurationSolution solution1 =
                TestdataConstraintConfigurationSolution.generateSolution(1, 1);
        InnerScoreDirector<TestdataConstraintConfigurationSolution, SimpleScore> scoreDirector =
                scoreDirectorFactory.buildScoreDirector(false, false);
        scoreDirector.setWorkingSolution(solution1);
        SimpleScore score1 = scoreDirector.calculateScore();
        assertThat(score1).isEqualTo(SimpleScore.of(1));

        // Set new solution with a different constraint weight, calculate score.
        TestdataConstraintConfigurationSolution solution2 =
                TestdataConstraintConfigurationSolution.generateSolution(1, 1);
        TestdataConstraintConfiguration constraintConfiguration = solution2.getConstraintConfiguration();
        constraintConfiguration.setFirstWeight(SimpleScore.of(2));
        scoreDirector.setWorkingSolution(solution2);
        SimpleScore score2 = scoreDirector.calculateScore();
        assertThat(score2).isEqualTo(SimpleScore.of(2));

        // Set new solution with a disabled constraint, calculate score.
        constraintConfiguration.setFirstWeight(SimpleScore.ZERO);
        scoreDirector.setWorkingSolution(solution2);
        SimpleScore score3 = scoreDirector.calculateScore();
        assertThat(score3).isEqualTo(SimpleScore.ZERO);

    }

    @EnumSource(ScoreDirectorType.class)
    @ParameterizedTest
    void mutableConstraintConfiguration(ScoreDirectorType scoreDirectorType) {
        InnerScoreDirectorFactory<TestdataConstraintConfigurationSolution, SimpleScore> scoreDirectorFactory =
                scoreDirectorType.buildScoreDirectorFactory(solutionDescriptor);

        // Create score director, calculate score with a given constraint configuration.
        TestdataConstraintConfigurationSolution solution =
                TestdataConstraintConfigurationSolution.generateSolution(1, 1);
        InnerScoreDirector<TestdataConstraintConfigurationSolution, SimpleScore> scoreDirector =
                scoreDirectorFactory.buildScoreDirector(false, false);
        scoreDirector.setWorkingSolution(solution);
        SimpleScore score1 = scoreDirector.calculateScore();
        assertThat(score1).isEqualTo(SimpleScore.of(1));

        // Change constraint configuration on the current working solution.
        TestdataConstraintConfiguration constraintConfiguration = solution.getConstraintConfiguration();
        scoreDirector.beforeProblemPropertyChanged(constraintConfiguration);
        constraintConfiguration.setFirstWeight(SimpleScore.of(2));
        scoreDirector.afterProblemPropertyChanged(constraintConfiguration);
        SimpleScore score2 = scoreDirector.calculateScore();
        assertThat(score2).isEqualTo(SimpleScore.of(2));
    }

    enum ScoreDirectorType {

        EASY(new ScoreDirectorFactoryConfig()
                .withEasyScoreCalculatorClass(TestdataConstraintWeightEasyScoreCalculator.class)),
        CS_DROOLS(new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintWeightConstraintProvider.class)
                .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS)),
        CS_BAVET(new ScoreDirectorFactoryConfig()
                .withConstraintProviderClass(TestdataConstraintWeightConstraintProvider.class)
                .withConstraintStreamImplType(ConstraintStreamImplType.BAVET)),
        INCREMENTAL(new ScoreDirectorFactoryConfig()
                .withIncrementalScoreCalculatorClass(TestdataConstraintWeighIncrementalScoreCalculator.class)),
        DRL(new ScoreDirectorFactoryConfig()
                .withScoreDrls("org/optaplanner/core/impl/score/director/scoreDirectorSemanticsDroolsConstraints.drl",
                        "org/optaplanner/core/impl/score/director/scoreDirectorSemanticsDroolsConstraints2.drl"));

        private final ScoreDirectorFactoryConfig scoreDirectorFactoryConfig;

        ScoreDirectorType(ScoreDirectorFactoryConfig scoreDirectorFactoryConfig) {
            this.scoreDirectorFactoryConfig = scoreDirectorFactoryConfig;
        }

        /**
         * Creates a score director factory with a simple scoring function that rewards each {@link TestdataEntity}
         * by {@link SimpleScore#ONE}.
         *
         * @param solutionDescriptor never null
         * @return never null
         */
        public InnerScoreDirectorFactory<TestdataConstraintConfigurationSolution, SimpleScore>
                buildScoreDirectorFactory(SolutionDescriptor<TestdataConstraintConfigurationSolution> solutionDescriptor) {
            ScoreDirectorFactoryFactory<TestdataConstraintConfigurationSolution, SimpleScore> scoreDirectorFactoryFactory =
                    new ScoreDirectorFactoryFactory<>(scoreDirectorFactoryConfig);
            return scoreDirectorFactoryFactory.buildScoreDirectorFactory(getClass().getClassLoader(),
                    EnvironmentMode.REPRODUCIBLE, solutionDescriptor);
        }

    }

}
