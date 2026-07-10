/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.constraint.streams.common.inliner;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.score.TestdataHardMediumSoftBigDecimalScoreSolution;

class HardMediumSoftBigDecimalScoreInlinerTest
        extends AbstractScoreInlinerTest<TestdataHardMediumSoftBigDecimalScoreSolution, HardMediumSoftBigDecimalScore> {

    @Test
    void defaultScore() {
        HardMediumSoftBigDecimalScoreInliner scoreInliner =
                new HardMediumSoftBigDecimalScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftBigDecimalScore.ZERO);
    }

    @Test
    void impactHard() {
        HardMediumSoftBigDecimalScoreInliner scoreInliner =
                new HardMediumSoftBigDecimalScoreInliner(constraintMatchEnabled);

        HardMediumSoftBigDecimalScore constraintWeight = HardMediumSoftBigDecimalScore.ofHard(BigDecimal.valueOf(90));
        WeightedScoreImpacter<HardMediumSoftBigDecimalScore, HardMediumSoftBigDecimalScoreContext> hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.ONE, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.valueOf(90), BigDecimal.ZERO, BigDecimal.ZERO));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(2), JustificationsSupplier.empty());
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
    void impactMedium() {
        HardMediumSoftBigDecimalScoreInliner scoreInliner =
                new HardMediumSoftBigDecimalScoreInliner(constraintMatchEnabled);

        HardMediumSoftBigDecimalScore constraintWeight = HardMediumSoftBigDecimalScore.ofMedium(BigDecimal.valueOf(90));
        WeightedScoreImpacter<HardMediumSoftBigDecimalScore, HardMediumSoftBigDecimalScoreContext> hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.ONE, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.valueOf(90), BigDecimal.ZERO));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(2), JustificationsSupplier.empty());
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
    void impactSoft() {
        HardMediumSoftBigDecimalScoreInliner scoreInliner =
                new HardMediumSoftBigDecimalScoreInliner(constraintMatchEnabled);

        HardMediumSoftBigDecimalScore constraintWeight = HardMediumSoftBigDecimalScore.ofSoft(BigDecimal.valueOf(90));
        WeightedScoreImpacter<HardMediumSoftBigDecimalScore, HardMediumSoftBigDecimalScoreContext> hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.ONE, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(90)));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(2), JustificationsSupplier.empty());
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
    void impactAll() {
        HardMediumSoftBigDecimalScoreInliner scoreInliner =
                new HardMediumSoftBigDecimalScoreInliner(constraintMatchEnabled);

        HardMediumSoftBigDecimalScore constraintWeight = HardMediumSoftBigDecimalScore.of(
                BigDecimal.valueOf(10), BigDecimal.valueOf(100), BigDecimal.valueOf(1_000));
        WeightedScoreImpacter<HardMediumSoftBigDecimalScore, HardMediumSoftBigDecimalScoreContext> hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.TEN, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(HardMediumSoftBigDecimalScore.of(BigDecimal.valueOf(100), BigDecimal.valueOf(1_000),
                        BigDecimal.valueOf(10_000)));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(20), JustificationsSupplier.empty());
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
