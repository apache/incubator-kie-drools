package org.optaplanner.core.impl.domain.score.descriptor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.buildin.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class ScoreDescriptorTest {

    @Test
    void scoreDefinition() {
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        ScoreDefinition<?> scoreDefinition = solutionDescriptor.getScoreDefinition();
        assertThat(scoreDefinition).isInstanceOf(SimpleScoreDefinition.class);
        assertThat(scoreDefinition.getScoreClass()).isEqualTo(SimpleScore.class);
    }

    @Test
    void scoreAccess() {
        SolutionDescriptor<TestdataSolution> solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        TestdataSolution solution = new TestdataSolution();

        assertThat((SimpleScore) solutionDescriptor.getScore(solution)).isNull();

        SimpleScore score = SimpleScore.of(-2);
        solutionDescriptor.setScore(solution, score);
        assertThat((SimpleScore) solutionDescriptor.getScore(solution)).isSameAs(score);
    }
}
