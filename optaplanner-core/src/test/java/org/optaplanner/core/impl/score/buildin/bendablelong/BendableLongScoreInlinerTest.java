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

package org.optaplanner.core.impl.score.buildin.bendablelong;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.impl.score.inliner.LongWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

public class BendableLongScoreInlinerTest {

    @Test
    public void buildWeightedScoreImpacter() {
        boolean constraintMatchEnabled = false;
        Consumer<Score<?>> scoreConsumer = null;

        BendableLongScoreInliner scoreInliner = new BendableLongScoreInliner(constraintMatchEnabled, 1, 2);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableLongScore.zero(1, 2));

        LongWeightedScoreImpacter hardImpacter = scoreInliner
                .buildWeightedScoreImpacter(BendableLongScore.ofHard(1, 2, 0, -90L));
        UndoScoreImpacter hardUndo = hardImpacter.impactScore(1L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableLongScore.of(new long[] { -90L }, new long[] { 0L, 0L }));
        scoreInliner.buildWeightedScoreImpacter(BendableLongScore.ofHard(1, 2, 0, -800L)).impactScore(1L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableLongScore.of(new long[] { -890L }, new long[] { 0L, 0L }));
        hardUndo.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableLongScore.of(new long[] { -800L }, new long[] { 0L, 0L }));

        LongWeightedScoreImpacter mediumImpacter = scoreInliner
                .buildWeightedScoreImpacter(BendableLongScore.ofSoft(1, 2, 0, -7L));
        UndoScoreImpacter mediumUndo = mediumImpacter.impactScore(1L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableLongScore.of(new long[] { -800L }, new long[] { -7L, 0L }));
        mediumUndo.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableLongScore.of(new long[] { -800L }, new long[] { 0L, 0L }));

        LongWeightedScoreImpacter softImpacter = scoreInliner
                .buildWeightedScoreImpacter(BendableLongScore.ofSoft(1, 2, 1, -1L));
        UndoScoreImpacter softUndo = softImpacter.impactScore(3L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableLongScore.of(new long[] { -800L }, new long[] { 0L, -3L }));
        softImpacter.impactScore(10L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableLongScore.of(new long[] { -800L }, new long[] { 0L, -13L }));
        softUndo.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableLongScore.of(new long[] { -800L }, new long[] { 0L, -10L }));

        LongWeightedScoreImpacter allLevelsImpacter = scoreInliner
                .buildWeightedScoreImpacter(BendableLongScore.of(new long[] { -1000L }, new long[] { -2000L, -3000L }));
        UndoScoreImpacter allLevelsUndo = allLevelsImpacter.impactScore(1L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0))
                .isEqualTo(BendableLongScore.of(new long[] { -1800L }, new long[] { -2000L, -3010L }));
        allLevelsUndo.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableLongScore.of(new long[] { -800L }, new long[] { 0L, -10L }));
    }

}
