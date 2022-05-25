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

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.score.TestdataHardSoftBigDecimalScoreSolution;

class HardSoftBigDecimalScoreInlinerTest
        extends AbstractScoreInlinerTest<TestdataHardSoftBigDecimalScoreSolution, HardSoftBigDecimalScore> {

    private static final JustificationsSupplier EMPTY_JUSTIFICATIONS_SUPPLIER = Collections::emptyList;

    @Test
    void defaultScore() {
        HardSoftBigDecimalScoreInliner scoreInliner =
                new HardSoftBigDecimalScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardSoftBigDecimalScore.ZERO);
    }

    @Test
    void impactHard() {
        HardSoftBigDecimalScoreInliner scoreInliner =
                new HardSoftBigDecimalScoreInliner(constraintMatchEnabled);

        HardSoftBigDecimalScore constraintWeight = HardSoftBigDecimalScore.ofHard(BigDecimal.valueOf(90));
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.ONE, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.valueOf(90), BigDecimal.ZERO));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(2), EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.valueOf(270), BigDecimal.ZERO));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.valueOf(90), BigDecimal.ZERO));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.valueOf(0), BigDecimal.ZERO));
    }

    @Test
    void impactSoft() {
        HardSoftBigDecimalScoreInliner scoreInliner =
                new HardSoftBigDecimalScoreInliner(constraintMatchEnabled);

        HardSoftBigDecimalScore constraintWeight = HardSoftBigDecimalScore.ofSoft(BigDecimal.valueOf(90));
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.ONE, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.valueOf(90)));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(2), EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.valueOf(270)));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.valueOf(90)));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test
    void impactAll() {
        HardSoftBigDecimalScoreInliner scoreInliner =
                new HardSoftBigDecimalScoreInliner(constraintMatchEnabled);

        HardSoftBigDecimalScore constraintWeight = HardSoftBigDecimalScore.of(BigDecimal.valueOf(10), BigDecimal.valueOf(100));
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.TEN, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.valueOf(100), BigDecimal.valueOf(1000)));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(20), EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.valueOf(300), BigDecimal.valueOf(3000)));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.valueOf(100), BigDecimal.valueOf(1000)));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Override
    protected SolutionDescriptor<TestdataHardSoftBigDecimalScoreSolution> buildSolutionDescriptor() {
        return TestdataHardSoftBigDecimalScoreSolution.buildSolutionDescriptor();
    }
}
