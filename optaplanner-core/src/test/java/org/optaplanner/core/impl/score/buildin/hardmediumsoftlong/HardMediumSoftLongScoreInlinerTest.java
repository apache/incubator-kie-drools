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

package org.optaplanner.core.impl.score.buildin.hardmediumsoftlong;

import java.util.function.Consumer;

import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.impl.score.inliner.LongWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

import static org.junit.Assert.*;

public class HardMediumSoftLongScoreInlinerTest {

    @Test
    public void buildWeightedScoreImpacter() {
        boolean constraintMatchEnabled = false;
        Consumer<Score<?>> scoreConsumer = null;

        HardMediumSoftLongScoreInliner scoreInliner = new HardMediumSoftLongScoreInliner(constraintMatchEnabled);
        assertEquals(HardMediumSoftLongScore.ZERO, scoreInliner.extractScore(0));

        LongWeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(HardMediumSoftLongScore.ofHard(-90L));
        UndoScoreImpacter hardUndo = hardImpacter.impactScore(1L, scoreConsumer);
        assertEquals(HardMediumSoftLongScore.of(-90L, 0L, 0L), scoreInliner.extractScore(0));
        scoreInliner.buildWeightedScoreImpacter(HardMediumSoftLongScore.ofHard(-800L)).impactScore(1L, scoreConsumer);
        assertEquals(HardMediumSoftLongScore.of(-890L, 0L, 0L), scoreInliner.extractScore(0));
        hardUndo.undoScoreImpact();
        assertEquals(HardMediumSoftLongScore.of(-800L, 0L, 0L), scoreInliner.extractScore(0));

        LongWeightedScoreImpacter mediumImpacter = scoreInliner.buildWeightedScoreImpacter(HardMediumSoftLongScore.ofMedium(-7L));
        UndoScoreImpacter mediumUndo = mediumImpacter.impactScore(1L, scoreConsumer);
        assertEquals(HardMediumSoftLongScore.of(-800L, -7L, 0L), scoreInliner.extractScore(0));
        mediumUndo.undoScoreImpact();
        assertEquals(HardMediumSoftLongScore.of(-800L, 0L, 0L), scoreInliner.extractScore(0));

        LongWeightedScoreImpacter softImpacter = scoreInliner.buildWeightedScoreImpacter(HardMediumSoftLongScore.ofSoft(-1L));
        UndoScoreImpacter softUndo = softImpacter.impactScore(3L, scoreConsumer);
        assertEquals(HardMediumSoftLongScore.of(-800L, 0L, -3L), scoreInliner.extractScore(0));
        softImpacter.impactScore(10L, scoreConsumer);
        assertEquals(HardMediumSoftLongScore.of(-800L, 0L, -13L), scoreInliner.extractScore(0));
        softUndo.undoScoreImpact();
        assertEquals(HardMediumSoftLongScore.of(-800L, 0L, -10L), scoreInliner.extractScore(0));

        LongWeightedScoreImpacter allLevelsImpacter = scoreInliner.buildWeightedScoreImpacter(HardMediumSoftLongScore.of(-1000L, -2000L, -3000L));
        UndoScoreImpacter allLevelsUndo = allLevelsImpacter.impactScore(1L, scoreConsumer);
        assertEquals(HardMediumSoftLongScore.of(-1800L, -2000L, -3010L), scoreInliner.extractScore(0));
        allLevelsUndo.undoScoreImpact();
        assertEquals(HardMediumSoftLongScore.of(-800L, 0L, -10L), scoreInliner.extractScore(0));
    }

}
