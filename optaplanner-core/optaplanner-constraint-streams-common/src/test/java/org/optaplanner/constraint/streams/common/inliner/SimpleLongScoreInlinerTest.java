package org.optaplanner.constraint.streams.common.inliner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleLongScoreSolution;

class SimpleLongScoreInlinerTest extends AbstractScoreInlinerTest<TestdataSimpleLongScoreSolution, SimpleLongScore> {

    private static final JustificationsSupplier EMPTY_JUSTIFICATIONS_SUPPLIER = Collections::emptyList;

    @Test
    void defaultScore() {
        SimpleLongScoreInliner scoreInliner =
                new SimpleLongScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleLongScore.ZERO);
    }

    @Test
    void impact() {
        SimpleLongScoreInliner scoreInliner =
                new SimpleLongScoreInliner(constraintMatchEnabled);

        SimpleLongScore constraintWeight = SimpleLongScore.of(10);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(10, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleLongScore.of(100));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(20, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleLongScore.of(300));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleLongScore.of(100));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleLongScore.of(0));
    }

    @Override
    protected SolutionDescriptor<TestdataSimpleLongScoreSolution> buildSolutionDescriptor() {
        return TestdataSimpleLongScoreSolution.buildSolutionDescriptor();
    }
}
