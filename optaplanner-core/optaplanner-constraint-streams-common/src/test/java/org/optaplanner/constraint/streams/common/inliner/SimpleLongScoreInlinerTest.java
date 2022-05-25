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
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.score.TestdataSimpleLongScoreSolution;

class SimpleLongScoreInlinerTest extends AbstractScoreInlinerTest<TestdataSimpleLongScoreSolution, SimpleLongScore> {

    private static final JustificationsSupplier EMPTY_JUSTIFICATIONS_SUPPLIER = Collections::emptyList;

    @Test
    void defaultScore() {
        SimpleLongScoreInliner scoreInliner =
                new SimpleLongScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleLongScore.ZERO);
    }

    @Test
    void impact() {
        SimpleLongScoreInliner scoreInliner =
                new SimpleLongScoreInliner(constraintMatchEnabled);

        SimpleLongScore constraintWeight = SimpleLongScore.of(10);
        WeightedScoreImpacter hardImpacter =
                scoreInliner.buildWeightedScoreImpacter(buildConstraint(constraintWeight), constraintWeight);
        UndoScoreImpacter undo1 = hardImpacter.impactScore(10, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleLongScore.of(100));

        UndoScoreImpacter undo2 = hardImpacter.impactScore(20, EMPTY_JUSTIFICATIONS_SUPPLIER);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleLongScore.of(300));

        undo2.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleLongScore.of(100));

        undo1.run();
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(SimpleLongScore.of(0));
    }

    @Override
    protected SolutionDescriptor<TestdataSimpleLongScoreSolution> buildSolutionDescriptor() {
        return TestdataSimpleLongScoreSolution.buildSolutionDescriptor();
    }
}
