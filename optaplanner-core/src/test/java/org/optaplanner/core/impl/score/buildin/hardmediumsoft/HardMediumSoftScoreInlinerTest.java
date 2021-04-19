/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.buildin.hardmediumsoft;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.buildin.AbstractScoreInlinerTest;
import org.optaplanner.core.impl.score.inliner.JustificationsSupplier;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;
import org.optaplanner.core.impl.score.inliner.WeightedScoreImpacter;
import org.optaplanner.core.impl.testdata.domain.score.TestdataHardMediumSoftScoreSolution;

public class HardMediumSoftScoreInlinerTest
        extends AbstractScoreInlinerTest<TestdataHardMediumSoftScoreSolution, HardMediumSoftScore> {

    private static final JustificationsSupplier EMPTY_JUSTIFICATIONS_SUPPLIER = Collections::emptyList;

    @Test
    public void defaultScore() {
        TestConstraint<TestdataHardMediumSoftScoreSolution, HardMediumSoftScore> constraint =
                buildConstraint(HardMediumSoftScore.ONE_HARD);
        HardMediumSoftScoreInliner scoreInliner =
                new HardMediumSoftScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftScore.ZERO);
    }

    @Test
    public void impactHard() {
        TestConstraint<TestdataHardMediumSoftScoreSolution, HardMediumSoftScore> constraint =
                buildConstraint(HardMediumSoftScore.ofHard(90));
        HardMediumSoftScoreInliner scoreInliner =
                new HardMediumSoftScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled);

        WeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
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
    public void impactMedium() {
        TestConstraint<TestdataHardMediumSoftScoreSolution, HardMediumSoftScore> constraint =
                buildConstraint(HardMediumSoftScore.ofMedium(90));
        HardMediumSoftScoreInliner scoreInliner =
                new HardMediumSoftScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled);

        WeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
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
    public void impactSoft() {
        TestConstraint<TestdataHardMediumSoftScoreSolution, HardMediumSoftScore> constraint =
                buildConstraint(HardMediumSoftScore.ofSoft(90));
        HardMediumSoftScoreInliner scoreInliner =
                new HardMediumSoftScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled);

        WeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
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
    public void impactAll() {
        TestConstraint<TestdataHardMediumSoftScoreSolution, HardMediumSoftScore> constraint =
                buildConstraint(HardMediumSoftScore.of(10, 100, 1_000));
        HardMediumSoftScoreInliner scoreInliner =
                new HardMediumSoftScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled);

        WeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
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
