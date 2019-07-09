/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.function.Consumer;

import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.impl.score.inliner.LongWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

import static org.junit.Assert.*;

public class BendableLongScoreInlinerTest {

    @Test
    public void buildWeightedScoreImpacter() {
        boolean constraintMatchEnabled = false;
        Consumer<Score<?>> scoreConsumer = null;

        BendableLongScoreInliner scoreInliner = new BendableLongScoreInliner(constraintMatchEnabled, 1, 2);
        assertEquals(BendableLongScore.zero(1, 2), scoreInliner.extractScore(0));

        LongWeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(BendableLongScore.ofHard(1, 2, 0, -90L));
        UndoScoreImpacter hardUndo = hardImpacter.impactScore(1L, scoreConsumer);
        assertEquals(BendableLongScore.of(new long[]{-90L}, new long[]{0L, 0L}), scoreInliner.extractScore(0));
        scoreInliner.buildWeightedScoreImpacter(BendableLongScore.ofHard(1, 2, 0, -800L)).impactScore(1L, scoreConsumer);
        assertEquals(BendableLongScore.of(new long[]{-890L}, new long[]{0L, 0L}), scoreInliner.extractScore(0));
        hardUndo.undoScoreImpact();
        assertEquals(BendableLongScore.of(new long[]{-800L}, new long[]{0L, 0L}), scoreInliner.extractScore(0));

        LongWeightedScoreImpacter mediumImpacter = scoreInliner.buildWeightedScoreImpacter(BendableLongScore.ofSoft(1, 2, 0, -7L));
        UndoScoreImpacter mediumUndo = mediumImpacter.impactScore(1L, scoreConsumer);
        assertEquals(BendableLongScore.of(new long[]{-800L}, new long[]{-7L, 0L}), scoreInliner.extractScore(0));
        mediumUndo.undoScoreImpact();
        assertEquals(BendableLongScore.of(new long[]{-800L}, new long[]{0L, 0L}), scoreInliner.extractScore(0));

        LongWeightedScoreImpacter softImpacter = scoreInliner.buildWeightedScoreImpacter(BendableLongScore.ofSoft(1, 2, 1, -1L));
        UndoScoreImpacter softUndo = softImpacter.impactScore(3L, scoreConsumer);
        assertEquals(BendableLongScore.of(new long[]{-800L}, new long[]{0L, -3L}), scoreInliner.extractScore(0));
        softImpacter.impactScore(10L, scoreConsumer);
        assertEquals(BendableLongScore.of(new long[]{-800L}, new long[]{0L, -13L}), scoreInliner.extractScore(0));
        softUndo.undoScoreImpact();
        assertEquals(BendableLongScore.of(new long[]{-800L}, new long[]{0L, -10L}), scoreInliner.extractScore(0));

        LongWeightedScoreImpacter allLevelsImpacter = scoreInliner.buildWeightedScoreImpacter(BendableLongScore.of(new long[]{-1000L}, new long[]{-2000L, -3000L}));
        UndoScoreImpacter allLevelsUndo = allLevelsImpacter.impactScore(1L, scoreConsumer);
        assertEquals(BendableLongScore.of(new long[]{-1800L}, new long[]{-2000L, -3010L}), scoreInliner.extractScore(0));
        allLevelsUndo.undoScoreImpact();
        assertEquals(BendableLongScore.of(new long[]{-800L}, new long[]{0L, -10L}), scoreInliner.extractScore(0));
    }

}
