package org.optaplanner.constraint.streams.common.inliner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.score.TestdataHardMediumSoftScoreSolution;

class HardMediumSoftScoreInlinerTest
        extends AbstractScoreInlinerTest<TestdataHardMediumSoftScoreSolution, HardMediumSoftScore> {

    private static final JustificationsSupplier EMPTY_JUSTIFICATIONS_SUPPLIER = Collections::emptyList;

    @Test
    void defaultScore() {
        HardMediumSoftScoreInliner scoreInliner =
                new HardMediumSoftScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftScore.ZERO);
    }

    @Test
    void impactHard() {
        HardMediumSoftScoreInliner scoreInliner =
                new HardMediumSoftScoreInliner(constraintMatchEnabled);

        HardMediumSoftScore constraintWeight = HardMediumSoftScore.ofHard(90);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(90, 0, 0));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(270, 0, 0));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(90, 0, 0));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(0, 0, 0));
    }

    @Test
    void impactMedium() {
        HardMediumSoftScoreInliner scoreInliner =
                new HardMediumSoftScoreInliner(constraintMatchEnabled);

        HardMediumSoftScore constraintWeight = HardMediumSoftScore.ofMedium(90);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(0, 90, 0));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(0, 270, 0));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(0, 90, 0));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(0, 0, 0));
    }

    @Test
    void impactSoft() {
        HardMediumSoftScoreInliner scoreInliner =
                new HardMediumSoftScoreInliner(constraintMatchEnabled);

        HardMediumSoftScore constraintWeight = HardMediumSoftScore.ofSoft(90);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(0, 0, 90));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(0, 0, 270));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(0, 0, 90));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(0, 0, 0));
    }

    @Test
    void impactAll() {
        HardMediumSoftScoreInliner scoreInliner =
                new HardMediumSoftScoreInliner(constraintMatchEnabled);

        HardMediumSoftScore constraintWeight = HardMediumSoftScore.of(10, 100, 1_000);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(10, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(100, 1_000, 10_000));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(20, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(300, 3_000, 30_000));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(100, 1_000, 10_000));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftScore.of(0, 0, 0));
    }

    @Override
    protected SolutionDescriptor<TestdataHardMediumSoftScoreSolution> buildSolutionDescriptor() {
        return TestdataHardMediumSoftScoreSolution.buildSolutionDescriptor();
    }
}
