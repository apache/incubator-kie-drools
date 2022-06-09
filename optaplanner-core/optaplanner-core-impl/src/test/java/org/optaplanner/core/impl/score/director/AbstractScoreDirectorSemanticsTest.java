package org.optaplanner.core.impl.score.director;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfiguration;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfigurationSolution;

public abstract class AbstractScoreDirectorSemanticsTest {

    private final SolutionDescriptor<TestdataConstraintConfigurationSolution> solutionDescriptor =
            TestdataConstraintConfigurationSolution.buildSolutionDescriptor();

    protected abstract InnerScoreDirectorFactory<TestdataConstraintConfigurationSolution, SimpleScore>
            buildInnerScoreDirectorFactory(SolutionDescriptor<TestdataConstraintConfigurationSolution> solutionDescriptor);

    @Test
    void independentScoreDirectors() {
        InnerScoreDirectorFactory<TestdataConstraintConfigurationSolution, SimpleScore> scoreDirectorFactory =
                buildInnerScoreDirectorFactory(solutionDescriptor);

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

    @Test
    void solutionBasedScoreWeights() {
        InnerScoreDirectorFactory<TestdataConstraintConfigurationSolution, SimpleScore> scoreDirectorFactory =
                buildInnerScoreDirectorFactory(solutionDescriptor);

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

    @Test
    void mutableConstraintConfiguration() {
        InnerScoreDirectorFactory<TestdataConstraintConfigurationSolution, SimpleScore> scoreDirectorFactory =
                buildInnerScoreDirectorFactory(solutionDescriptor);

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

}
