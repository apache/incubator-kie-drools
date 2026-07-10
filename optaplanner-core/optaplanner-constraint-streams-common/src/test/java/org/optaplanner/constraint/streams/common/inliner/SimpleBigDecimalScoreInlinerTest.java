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
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleBigDecimalScoreSolution;

class SimpleBigDecimalScoreInlinerTest
        extends AbstractScoreInlinerTest<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> {

    @Test
    void defaultScore() {
        SimpleBigDecimalScoreInliner scoreInliner =
                new SimpleBigDecimalScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleBigDecimalScore.ZERO);
    }

    @Test
    void impact() {
        SimpleBigDecimalScoreInliner scoreInliner =
                new SimpleBigDecimalScoreInliner(constraintMatchEnabled);

        SimpleBigDecimalScore constraintWeight = SimpleBigDecimalScore.of(BigDecimal.valueOf(10));
        WeightedScoreImpacter<SimpleBigDecimalScore, SimpleBigDecimalScoreContext> hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.TEN, JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(100)));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(20), JustificationsSupplier.empty());
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(300)));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(100)));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleBigDecimalScore.of(BigDecimal.ZERO));
    }

    @Override
    protected SolutionDescriptor<TestdataSimpleBigDecimalScoreSolution> buildSolutionDescriptor() {
        return TestdataSimpleBigDecimalScoreSolution.buildSolutionDescriptor();
    }
}
