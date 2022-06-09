package org.optaplanner.constraint.streams.common.inliner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.score.TestdataHardMediumSoftLongScoreSolution;

class HardMediumSoftLongScoreInlinerTest
        extends AbstractScoreInlinerTest<TestdataHardMediumSoftLongScoreSolution, HardMediumSoftLongScore> {

    private static final JustificationsSupplier EMPTY_JUSTIFICATIONS_SUPPLIER = Collections::emptyList;

    @Test
    void defaultScore() {
        HardMediumSoftLongScoreInliner scoreInliner =
                new HardMediumSoftLongScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.ZERO);
    }

    @Test
    void impactHard() {
        HardMediumSoftLongScoreInliner scoreInliner =
                new HardMediumSoftLongScoreInliner(constraintMatchEnabled);

        HardMediumSoftLongScore constraintWeight = HardMediumSoftLongScore.ofHard(90);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(90, 0, 0));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(270, 0, 0));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(90, 0, 0));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(0, 0, 0));
    }

    @Test
    void impactMedium() {
        HardMediumSoftLongScoreInliner scoreInliner =
                new HardMediumSoftLongScoreInliner(constraintMatchEnabled);

        HardMediumSoftLongScore constraintWeight = HardMediumSoftLongScore.ofMedium(90);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(0, 90, 0));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(0, 270, 0));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(0, 90, 0));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(0, 0, 0));
    }

    @Test
    void impactSoft() {
        HardMediumSoftLongScoreInliner scoreInliner =
                new HardMediumSoftLongScoreInliner(constraintMatchEnabled);

        HardMediumSoftLongScore constraintWeight = HardMediumSoftLongScore.ofSoft(90);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(0, 0, 90));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(0, 0, 270));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(0, 0, 90));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(0, 0, 0));
    }

    @Test
    void impactAll() {
        HardMediumSoftLongScoreInliner scoreInliner =
                new HardMediumSoftLongScoreInliner(constraintMatchEnabled);

        HardMediumSoftLongScore constraintWeight = HardMediumSoftLongScore.of(10, 100, 1_000);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(10, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(100, 1_000, 10_000));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(20, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(300, 3_000, 30_000));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(100, 1_000, 10_000));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftLongScore.of(0, 0, 0));
    }

    @Override
    protected SolutionDescriptor<TestdataHardMediumSoftLongScoreSolution> buildSolutionDescriptor() {
        return TestdataHardMediumSoftLongScoreSolution.buildSolutionDescriptor();
    }
}
