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

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.impl.score.inliner.LongWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

import static org.junit.Assert.*;

public class HardMediumSoftLongScoreInlinerTest {

    @Test
    public void buildLongWeightedScoreImpacter() {
        HardMediumSoftLongScoreInliner scoreInliner = new HardMediumSoftLongScoreInliner();
        assertEquals(HardMediumSoftLongScore.ZERO, scoreInliner.extractScore(0));

        LongWeightedScoreImpacter hardImpacter = scoreInliner.buildLongWeightedScoreImpacter(HardMediumSoftLongScore.ofHard(-90L));
        UndoScoreImpacter hardUndo = hardImpacter.impactScore(1L);
        assertEquals(HardMediumSoftLongScore.of(-90L, 0L, 0L), scoreInliner.extractScore(0));
        scoreInliner.buildLongWeightedScoreImpacter(HardMediumSoftLongScore.ofHard(-800L)).impactScore(1L);
        assertEquals(HardMediumSoftLongScore.of(-890L, 0L, 0L), scoreInliner.extractScore(0));
        hardUndo.undoScoreImpact();
        assertEquals(HardMediumSoftLongScore.of(-800L, 0L, 0L), scoreInliner.extractScore(0));

        LongWeightedScoreImpacter mediumImpacter = scoreInliner.buildLongWeightedScoreImpacter(HardMediumSoftLongScore.ofMedium(-7L));
        UndoScoreImpacter mediumUndo = mediumImpacter.impactScore(1L);
        assertEquals(HardMediumSoftLongScore.of(-800L, -7L, 0L), scoreInliner.extractScore(0));
        mediumUndo.undoScoreImpact();
        assertEquals(HardMediumSoftLongScore.of(-800L, 0L, 0L), scoreInliner.extractScore(0));

        LongWeightedScoreImpacter softImpacter = scoreInliner.buildLongWeightedScoreImpacter(HardMediumSoftLongScore.ofSoft(-1L));
        UndoScoreImpacter softUndo = softImpacter.impactScore(3L);
        assertEquals(HardMediumSoftLongScore.of(-800L, 0L, -3L), scoreInliner.extractScore(0));
        softImpacter.impactScore(10L);
        assertEquals(HardMediumSoftLongScore.of(-800L, 0L, -13L), scoreInliner.extractScore(0));
        softUndo.undoScoreImpact();
        assertEquals(HardMediumSoftLongScore.of(-800L, 0L, -10L), scoreInliner.extractScore(0));

        LongWeightedScoreImpacter allLevelsImpacter = scoreInliner.buildLongWeightedScoreImpacter(HardMediumSoftLongScore.of(-1000L, -2000L, -3000L));
        UndoScoreImpacter allLevelsUndo = allLevelsImpacter.impactScore(1L);
        assertEquals(HardMediumSoftLongScore.of(-1800L, -2000L, -3010L), scoreInliner.extractScore(0));
        allLevelsUndo.undoScoreImpact();
        assertEquals(HardMediumSoftLongScore.of(-800L, 0L, -10L), scoreInliner.extractScore(0));
    }

}
