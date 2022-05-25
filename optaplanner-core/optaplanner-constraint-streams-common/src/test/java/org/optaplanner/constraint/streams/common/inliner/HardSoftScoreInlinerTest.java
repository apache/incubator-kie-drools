/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.common.inliner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.score.TestdataHardSoftScoreSolution;

class HardSoftScoreInlinerTest extends AbstractScoreInlinerTest<TestdataHardSoftScoreSolution, HardSoftScore> {

    private static final JustificationsSupplier EMPTY_JUSTIFICATIONS_SUPPLIER = Collections::emptyList;

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
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(90, 0));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, EMPTY_JUSTIFICATIONS_SUPPLIER);
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
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(1, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(0, 90));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(2, EMPTY_JUSTIFICATIONS_SUPPLIER);
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
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(10, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftScore.of(100, 1_000));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(20, EMPTY_JUSTIFICATIONS_SUPPLIER);
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
