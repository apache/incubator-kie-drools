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

package org.optaplanner.core.impl.score.buildin.hardmediumsoftbigdecimal;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.buildin.AbstractScoreInlinerTest;
import org.optaplanner.core.impl.score.inliner.JustificationsSupplier;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;
import org.optaplanner.core.impl.score.inliner.WeightedScoreImpacter;
import org.optaplanner.core.impl.testdata.domain.score.TestdataHardMediumSoftBigDecimalScoreSolution;

public class HardMediumSoftBigDecimalScoreInlinerTest
        extends AbstractScoreInlinerTest<TestdataHardMediumSoftBigDecimalScoreSolution, HardMediumSoftBigDecimalScore> {

    private static final JustificationsSupplier EMPTY_JUSTIFICATIONS_SUPPLIER = Collections::emptyList;

    @Test
    public void defaultScore() {
        TestConstraint<TestdataHardMediumSoftBigDecimalScoreSolution, HardMediumSoftBigDecimalScore> constraint =
                buildConstraint(HardMediumSoftBigDecimalScore.ONE_HARD);
        HardMediumSoftBigDecimalScoreInliner scoreInliner =
                new HardMediumSoftBigDecimalScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftBigDecimalScore.ZERO);
    }

    @Test
    public void impactHard() {
        TestConstraint<TestdataHardMediumSoftBigDecimalScoreSolution, HardMediumSoftBigDecimalScore> constraint =
                buildConstraint(HardMediumSoftBigDecimalScore.ofHard(BigDecimal.valueOf(90)));
        HardMediumSoftBigDecimalScoreInliner scoreInliner =
                new HardMediumSoftBigDecimalScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled);

        WeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.ONE, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.valueOf(90), BigDecimal.ZERO, BigDecimal.ZERO));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(2), EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.valueOf(270), BigDecimal.ZERO, BigDecimal.ZERO));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.valueOf(90), BigDecimal.ZERO, BigDecimal.ZERO));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.valueOf(0), BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test
    public void impactMedium() {
        TestConstraint<TestdataHardMediumSoftBigDecimalScoreSolution, HardMediumSoftBigDecimalScore> constraint =
                buildConstraint(HardMediumSoftBigDecimalScore.ofMedium(BigDecimal.valueOf(90)));
        HardMediumSoftBigDecimalScoreInliner scoreInliner =
                new HardMediumSoftBigDecimalScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled);

        WeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.ONE, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.valueOf(90), BigDecimal.ZERO));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(2), EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.valueOf(270), BigDecimal.ZERO));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.valueOf(90), BigDecimal.ZERO));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test
    public void impactSoft() {
        TestConstraint<TestdataHardMediumSoftBigDecimalScoreSolution, HardMediumSoftBigDecimalScore> constraint =
                buildConstraint(HardMediumSoftBigDecimalScore.ofSoft(BigDecimal.valueOf(90)));
        HardMediumSoftBigDecimalScoreInliner scoreInliner =
                new HardMediumSoftBigDecimalScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled);

        WeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.ONE, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(90)));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(2), EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(270)));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(90)));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test
    public void impactAll() {
        TestConstraint<TestdataHardMediumSoftBigDecimalScoreSolution, HardMediumSoftBigDecimalScore> constraint =
                buildConstraint(HardMediumSoftBigDecimalScore.of(BigDecimal.valueOf(10), BigDecimal.valueOf(100),
                        BigDecimal.valueOf(1_000)));
        HardMediumSoftBigDecimalScoreInliner scoreInliner =
                new HardMediumSoftBigDecimalScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled);

        WeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.TEN, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.valueOf(100), BigDecimal.valueOf(1_000),
                        BigDecimal.valueOf(10_000)));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(20), EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.valueOf(300), BigDecimal.valueOf(3_000),
                        BigDecimal.valueOf(30_000)));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.valueOf(100), BigDecimal.valueOf(1_000),
                        BigDecimal.valueOf(10_000)));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Override
    protected SolutionDescriptor<TestdataHardMediumSoftBigDecimalScoreSolution> buildSolutionDescriptor() {
        return TestdataHardMediumSoftBigDecimalScoreSolution.buildSolutionDescriptor();
    }
}
