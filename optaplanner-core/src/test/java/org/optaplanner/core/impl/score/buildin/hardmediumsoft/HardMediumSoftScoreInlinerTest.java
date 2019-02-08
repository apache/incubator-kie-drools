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

package org.optaplanner.core.impl.score.buildin.hardmediumsoft;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.impl.score.inliner.IntWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

import static org.junit.Assert.*;

public class HardMediumSoftScoreInlinerTest {

    @Test
    public void buildIntWeightedScoreImpacter() {
        HardMediumSoftScoreInliner scoreInliner = new HardMediumSoftScoreInliner();
        assertEquals(HardMediumSoftScore.ZERO, scoreInliner.extractScore(0));

        IntWeightedScoreImpacter hardImpacter = scoreInliner.buildIntWeightedScoreImpacter(HardMediumSoftScore.ofHard(-90));
        UndoScoreImpacter hardUndo = hardImpacter.impactScore(1);
        assertEquals(HardMediumSoftScore.of(-90, 0, 0), scoreInliner.extractScore(0));
        scoreInliner.buildIntWeightedScoreImpacter(HardMediumSoftScore.ofHard(-800)).impactScore(1);
        assertEquals(HardMediumSoftScore.of(-890, 0, 0), scoreInliner.extractScore(0));
        hardUndo.undoScoreImpact();
        assertEquals(HardMediumSoftScore.of(-800, 0, 0), scoreInliner.extractScore(0));

        IntWeightedScoreImpacter mediumImpacter = scoreInliner.buildIntWeightedScoreImpacter(HardMediumSoftScore.ofMedium(-7));
        UndoScoreImpacter mediumUndo = mediumImpacter.impactScore(1);
        assertEquals(HardMediumSoftScore.of(-800, -7, 0), scoreInliner.extractScore(0));
        mediumUndo.undoScoreImpact();
        assertEquals(HardMediumSoftScore.of(-800, 0, 0), scoreInliner.extractScore(0));

        IntWeightedScoreImpacter softImpacter = scoreInliner.buildIntWeightedScoreImpacter(HardMediumSoftScore.ofSoft(-1));
        UndoScoreImpacter softUndo = softImpacter.impactScore(3);
        assertEquals(HardMediumSoftScore.of(-800, 0, -3), scoreInliner.extractScore(0));
        softImpacter.impactScore(10);
        assertEquals(HardMediumSoftScore.of(-800, 0, -13), scoreInliner.extractScore(0));
        softUndo.undoScoreImpact();
        assertEquals(HardMediumSoftScore.of(-800, 0, -10), scoreInliner.extractScore(0));
        
        IntWeightedScoreImpacter allLevelsImpacter = scoreInliner.buildIntWeightedScoreImpacter(HardMediumSoftScore.of(-1000, -2000, -3000));
        UndoScoreImpacter allLevelsUndo = allLevelsImpacter.impactScore(1);
        assertEquals(HardMediumSoftScore.of(-1800, -2000, -3010), scoreInliner.extractScore(0));
        allLevelsUndo.undoScoreImpact();
        assertEquals(HardMediumSoftScore.of(-800, 0, -10), scoreInliner.extractScore(0));
    }

}
