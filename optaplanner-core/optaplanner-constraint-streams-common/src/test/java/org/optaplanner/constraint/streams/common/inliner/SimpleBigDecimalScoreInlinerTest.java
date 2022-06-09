package org.optaplanner.constraint.streams.common.inliner;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleBigDecimalScoreSolution;

class SimpleBigDecimalScoreInlinerTest
        extends AbstractScoreInlinerTest<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> {

    private static final JustificationsSupplier EMPTY_JUSTIFICATIONS_SUPPLIER = Collections::emptyList;

    @Test
    void defaultScore() {
        SimpleBigDecimalScoreInliner scoreInliner =
                new SimpleBigDecimalScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleBigDecimalScore.ZERO);
    }

    @Test
    void impact() {
        SimpleBigDecimalScoreInliner scoreInliner =
                new SimpleBigDecimalScoreInliner(constraintMatchEnabled);

        SimpleBigDecimalScore constraintWeight = SimpleBigDecimalScore.of(BigDecimal.valueOf(10));
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.TEN, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(100)));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(20), EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(300)));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(100)));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleBigDecimalScore.of(BigDecimal.ZERO));
    }

    @Override
    protected SolutionDescriptor<TestdataSimpleBigDecimalScoreSolution> buildSolutionDescriptor() {
        return TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor();
    }
}
