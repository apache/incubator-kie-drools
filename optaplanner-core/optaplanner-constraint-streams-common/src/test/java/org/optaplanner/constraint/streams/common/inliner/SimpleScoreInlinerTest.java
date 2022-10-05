package org.optaplanner.constraint.streams.common.inliner;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class SimpleScoreInlinerTest extends AbstractScoreInlinerTest<TestdataSolution, SimpleScore> {

    @Test
    void defaultScore() {
        SimpleScoreInliner scoreInliner =
                new SimpleScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleScore.ZERO);
    }

    @Test
    void impact() {
        SimpleScoreInliner scoreInliner =
                new SimpleScoreInliner(constraintMatchEnabled);

        SimpleScore constraintWeight = SimpleScore.of(10);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(10, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleScore.of(100));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(20, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleScore.of(300));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleScore.of(100));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleScore.of(0));
    }

    @Override
    protected SolutionDescriptor<TestdataSolution> buildSolutionDescriptor() {
        return TestdataSolution.buildSolutionDescriptor();
    }
}
