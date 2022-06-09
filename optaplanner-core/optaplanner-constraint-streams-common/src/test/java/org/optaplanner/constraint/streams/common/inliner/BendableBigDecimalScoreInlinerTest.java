package org.optaplanner.constraint.streams.common.inliner;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.score.TestdataBendableBigDecimalScoreSolution;

class BendableBigDecimalScoreInlinerTest
        extends AbstractScoreInlinerTest<TestdataBendableBigDecimalScoreSolution, BendableBigDecimalScore> {

    private static final JustificationsSupplier EMPTY_JUSTIFICATIONS_SUPPLIER = Collections::emptyList;

    @Test
    void defaultScore() {
        BendableBigDecimalScoreInliner scoreInliner =
                new BendableBigDecimalScoreInliner(constraintMatchEnabled, 1, 2);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(buildScore(0, 0, 0));
    }

    @Test
    void impactHard() {
        BendableBigDecimalScoreInliner scoreInliner = new BendableBigDecimalScoreInliner(constraintMatchEnabled, 1, 2);

        BendableBigDecimalScore constraintWeight = buildScore(90, 0, 0);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.ONE, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(90, 0, 0));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(2), EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(270, 0, 0));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(90, 0, 0));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 0, 0));
    }

    @Test
    void impactSoft1() {
        BendableBigDecimalScoreInliner scoreInliner = new BendableBigDecimalScoreInliner(constraintMatchEnabled, 1, 2);

        BendableBigDecimalScore constraintWeight = buildScore(0, 90, 0);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.ONE, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 90, 0));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(2), EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 270, 0));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 90, 0));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 0, 0));
    }

    @Test
    void impactSoft2() {
        BendableBigDecimalScoreInliner scoreInliner = new BendableBigDecimalScoreInliner(constraintMatchEnabled, 1, 2);

        BendableBigDecimalScore constraintWeight = buildScore(0, 0, 90);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.ONE, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 0, 90));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(2), EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 0, 270));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 0, 90));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 0, 0));
    }

    @Test
    void impactAll() {
        BendableBigDecimalScoreInliner scoreInliner = new BendableBigDecimalScoreInliner(constraintMatchEnabled, 1, 2);

        BendableBigDecimalScore constraintWeight = buildScore(10, 100, 1_000);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.TEN, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(100, 1_000, 10_000));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(20), EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(300, 3_000, 30_000));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(100, 1_000, 10_000));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(buildScore(0, 0, 0));
    }

    @Override
    protected SolutionDescriptor<TestdataBendableBigDecimalScoreSolution> buildSolutionDescriptor() {
        return TestdataBendableBigDecimalScoreSolution.buildSolutionDescriptor();
    }

    private BendableBigDecimalScore buildScore(long hard, long soft1, long soft2) {
        return BendableBigDecimalScore.of(
                new BigDecimal[] { BigDecimal.valueOf(hard) },
                new BigDecimal[] { BigDecimal.valueOf(soft1), BigDecimal.valueOf(soft2) });
    }

}
