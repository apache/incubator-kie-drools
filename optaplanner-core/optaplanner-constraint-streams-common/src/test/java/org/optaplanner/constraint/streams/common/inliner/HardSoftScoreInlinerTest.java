package org.optaplanner.constraint.streams.common.inliner;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.score.TestdataHardSoftScoreSolution;

class HardSoftScoreInlinerTest extends AbstractScoreInlinerTest<TestdataHardSoftScoreSolution, HardSoftScore> {

    @Test
    void defaultScore() {
        HardSoftScoreInliner scoreInliner =
                new HardSoftScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardSoftScore.ZERO);
    }

    @Test
    void impactHard() {
        HardSoftScoreInliner scoreInliner =
                new HardSoftScoreInliner(constraintMatchEnabled);

        HardSoftScore constraintWeight = HardSoftScore.ofHard(90);
        WeightedScoreImpacter<HardSoftScore, HardSoftScoreContext> hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(90, 0));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(270, 0));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(90, 0));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(0, 0));
    }

    @Test
    void impactSoft() {
        HardSoftScoreInliner scoreInliner =
                new HardSoftScoreInliner(constraintMatchEnabled);

        HardSoftScore constraintWeight = HardSoftScore.ofSoft(90);
        WeightedScoreImpacter<HardSoftScore, HardSoftScoreContext> hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(0, 90));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(0, 270));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(0, 90));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(0, 0));
    }

    @Test
    void impactAll() {
        HardSoftScoreInliner scoreInliner =
                new HardSoftScoreInliner(constraintMatchEnabled);

        HardSoftScore constraintWeight = HardSoftScore.of(10, 100);
        WeightedScoreImpacter<HardSoftScore, HardSoftScoreContext> hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(10, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(100, 1_000));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(20, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(300, 3_000));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(100, 1_000));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(0, 0));
    }

    @Override
    protected SolutionDescriptor<TestdataHardSoftScoreSolution> buildSolutionDescriptor() {
        return TestdataHardSoftScoreSolution.buildSolutionDescriptor();
    }
}
