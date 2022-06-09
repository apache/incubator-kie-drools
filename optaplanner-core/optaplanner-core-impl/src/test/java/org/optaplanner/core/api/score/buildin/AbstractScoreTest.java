package org.optaplanner.core.api.score.buildin;

import static org.assertj.core.api.Assertions.assertThat;

import org.optaplanner.core.api.score.Score;

public abstract class AbstractScoreTest {

    protected static void assertScoreNotFeasible(Score... scores) {
        for (Score score : scores) {
            assertThat(score.isFeasible()).as(score + " should not be feasible.").isFalse();
        }
    }

    protected static void assertScoreFeasible(Score... scores) {
        for (Score score : scores) {
            assertThat(score.isFeasible()).as(score + " should be feasible.").isTrue();
        }
    }

}
