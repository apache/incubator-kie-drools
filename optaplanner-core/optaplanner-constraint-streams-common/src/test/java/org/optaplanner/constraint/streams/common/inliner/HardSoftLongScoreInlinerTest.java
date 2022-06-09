package org.optaplanner.constraint.streams.common.inliner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.score.TestdataHardSoftLongScoreSolution;

class HardSoftLongScoreInlinerTest extends AbstractScoreInlinerTest<TestdataHardSoftLongScoreSolution, HardSoftLongScore> {

    private static final JustificationsSupplier EMPTY_JUSTIFICATIONS_SUPPLIER = Collections::emptyList;

    @Test
    void defaultScore() {
        HardSoftLongScoreInliner scoreInliner =
                new HardSoftLongScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardSoftLongScore.ZERO);
    }

    @Test
    void impactHard() {
        HardSoftLongScoreInliner scoreInliner =
                new HardSoftLongScoreInliner(constraintMatchEnabled);

        HardSoftLongScore constraintWeight = HardSoftLongScore.ofHard(90);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftLongScore.of(90, 0));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftLongScore.of(270, 0));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftLongScore.of(90, 0));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftLongScore.of(0, 0));
    }

    @Test
    void impactSoft() {
        HardSoftLongScoreInliner scoreInliner =
                new HardSoftLongScoreInliner(constraintMatchEnabled);

        HardSoftLongScore constraintWeight = HardSoftLongScore.ofSoft(90);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftLongScore.of(0, 90));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftLongScore.of(0, 270));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftLongScore.of(0, 90));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftLongScore.of(0, 0));
    }

    @Test
    void impactAll() {
        HardSoftLongScoreInliner scoreInliner =
                new HardSoftLongScoreInliner(constraintMatchEnabled);

        HardSoftLongScore constraintWeight = HardSoftLongScore.of(10, 100);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(10, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftLongScore.of(100, 1_000));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(20, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftLongScore.of(300, 3_000));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftLongScore.of(100, 1_000));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftLongScore.of(0, 0));
    }

    @Override
    protected SolutionDescriptor<TestdataHardSoftLongScoreSolution> buildSolutionDescriptor() {
        return TestdataHardSoftLongScoreSolution.buildSolutionDescriptor();
    }
}
