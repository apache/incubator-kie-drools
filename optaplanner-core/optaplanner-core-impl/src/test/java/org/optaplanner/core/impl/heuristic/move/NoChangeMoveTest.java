package org.optaplanner.core.impl.heuristic.move;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockRebasingScoreDirector;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class NoChangeMoveTest {

    @Test
    void isMoveDoable() {
        assertThat(new NoChangeMove<>().isMoveDoable(null)).isTrue();
    }

    @Test
    void createUndoMove() {
        assertThat(new NoChangeMove<>().createUndoMove(null))
                .isInstanceOf(NoChangeMove.class);
    }

    @Test
    void getPlanningEntities() {
        assertThat(new NoChangeMove<>().getPlanningEntities().isEmpty()).isTrue();
    }

    @Test
    void getPlanningValues() {
        assertThat(new NoChangeMove<>().getPlanningValues().isEmpty()).isTrue();
    }

    @Test
    void rebase() {
        ScoreDirector<TestdataSolution> destinationScoreDirector = mockRebasingScoreDirector(
                TestdataSolution.buildSolutionDescriptor(), new Object[][] {});
        NoChangeMove<TestdataSolution> move = new NoChangeMove<>();
        move.rebase(destinationScoreDirector);
    }

}
