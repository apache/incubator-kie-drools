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

package org.optaplanner.core.impl.score.buildin.simplebigdecimal;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.buildin.AbstractScoreInlinerTest;
import org.optaplanner.core.impl.score.inliner.JustificationsSupplier;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;
import org.optaplanner.core.impl.score.inliner.WeightedScoreImpacter;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleBigDecimalScoreSolution;

public class SimpleBigDecimalScoreInlinerTest
        extends AbstractScoreInlinerTest<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> {

    private static final JustificationsSupplier EMPTY_JUSTIFICATIONS_SUPPLIER = Collections::emptyList;

    @Test
    public void defaultScore() {
        TestConstraint<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> constraint =
                buildConstraint(SimpleBigDecimalScore.ONE);
        SimpleBigDecimalScoreInliner scoreInliner =
                new SimpleBigDecimalScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleBigDecimalScore.ZERO);
    }

    @Test
    public void impact() {
        TestConstraint<TestdataSimpleBigDecimalScoreSolution, SimpleBigDecimalScore> constraint =
                buildConstraint(SimpleBigDecimalScore.of(BigDecimal.valueOf(10)));
        SimpleBigDecimalScoreInliner scoreInliner =
                new SimpleBigDecimalScoreInliner(getConstaintToWeightMap(constraint), constraintMatchEnabled);

        WeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(BigDecimal.TEN, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleBigDecimalScore.of(BigDecimal.valueOf(100)));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(BigDecimal.valueOf(20), EMPTY_JUSTIFICATIONS_SUPPLIER);
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
