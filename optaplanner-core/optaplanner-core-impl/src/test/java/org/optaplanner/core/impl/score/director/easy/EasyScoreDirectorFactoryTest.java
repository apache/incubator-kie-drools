package org.optaplanner.core.impl.score.director.easy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class EasyScoreDirectorFactoryTest {

    @Test
    void buildScoreDirector() {
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        EasyScoreCalculator<TestdataSolution, SimpleScore> scoreCalculator = mock(EasyScoreCalculator.class);
        when(scoreCalculator.calculateScore(any(TestdataSolution.class)))
                .thenAnswer(invocation -> SimpleScore.of(-10));
        EasyScoreDirectorFactory<TestdataSolution, SimpleScore> directorFactory = new EasyScoreDirectorFactory<>(
                solutionDescriptor, scoreCalculator);

        EasyScoreDirector<TestdataSolution, SimpleScore> director =
                directorFactory.buildScoreDirector(false, false);
        TestdataSolution solution = new TestdataSolution();
        solution.setValueList(Collections.emptyList());
        solution.setEntityList(Collections.emptyList());
        director.setWorkingSolution(solution);
        assertThat(director.calculateScore())
                .isEqualTo(SimpleScore.ofUninitialized(0, -10));
    }

}
