/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.buildin.simplelong;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.impl.score.inliner.LongWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

public class SimpleLongScoreInlinerTest {

    @Test
    public void buildWeightedScoreImpacter() {
        boolean constraintMatchEnabled = false;
        Consumer<Score<?>> scoreConsumer = null;

        SimpleLongScoreInliner scoreInliner = new SimpleLongScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleLongScore.ZERO);

        LongWeightedScoreImpacter impacter1 = scoreInliner.buildWeightedScoreImpacter(SimpleLongScore.of(-90L));
        UndoScoreImpacter undo1 = impacter1.impactScore(1L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleLongScore.of(-90L));
        scoreInliner.buildWeightedScoreImpacter(SimpleLongScore.of(-800L)).impactScore(1L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleLongScore.of(-890L));
        undo1.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleLongScore.of(-800L));

        LongWeightedScoreImpacter impacter2 = scoreInliner.buildWeightedScoreImpacter(SimpleLongScore.of(-1L));
        UndoScoreImpacter undo2 = impacter2.impactScore(3L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleLongScore.of(-803L));
        impacter2.impactScore(10L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleLongScore.of(-813L));
        undo2.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleLongScore.of(-810L));
    }

}
