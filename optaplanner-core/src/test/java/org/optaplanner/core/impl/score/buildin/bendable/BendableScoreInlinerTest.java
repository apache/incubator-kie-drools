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

package org.optaplanner.core.impl.score.buildin.bendable;

import java.util.function.Consumer;

import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.impl.score.inliner.IntWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

import static org.junit.Assert.*;

public class BendableScoreInlinerTest {

    @Test
    public void buildIntWeightedScoreImpacter() {
        boolean constraintMatchEnabled = false;
        Consumer<Score<?>> scoreConsumer = null;

        BendableScoreInliner scoreInliner = new BendableScoreInliner(constraintMatchEnabled, 1, 2);
        assertEquals(BendableScore.zero(1, 2), scoreInliner.extractScore(0));

        IntWeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(BendableScore.ofHard(1, 2, 0, -90));
        UndoScoreImpacter hardUndo = hardImpacter.impactScore(1, scoreConsumer);
        assertEquals(BendableScore.of(new int[]{-90}, new int[]{0, 0}), scoreInliner.extractScore(0));
        scoreInliner.buildWeightedScoreImpacter(BendableScore.ofHard(1, 2, 0, -800)).impactScore(1, scoreConsumer);
        assertEquals(BendableScore.of(new int[]{-890}, new int[]{0, 0}), scoreInliner.extractScore(0));
        hardUndo.undoScoreImpact();
        assertEquals(BendableScore.of(new int[]{-800}, new int[]{0, 0}), scoreInliner.extractScore(0));

        IntWeightedScoreImpacter mediumImpacter = scoreInliner.buildWeightedScoreImpacter(BendableScore.ofSoft(1, 2, 0, -7));
        UndoScoreImpacter mediumUndo = mediumImpacter.impactScore(1, scoreConsumer);
        assertEquals(BendableScore.of(new int[]{-800}, new int[]{-7, 0}), scoreInliner.extractScore(0));
        mediumUndo.undoScoreImpact();
        assertEquals(BendableScore.of(new int[]{-800}, new int[]{0, 0}), scoreInliner.extractScore(0));

        IntWeightedScoreImpacter softImpacter = scoreInliner.buildWeightedScoreImpacter(BendableScore.ofSoft(1, 2, 1, -1));
        UndoScoreImpacter softUndo = softImpacter.impactScore(3, scoreConsumer);
        assertEquals(BendableScore.of(new int[]{-800}, new int[]{0, -3}), scoreInliner.extractScore(0));
        softImpacter.impactScore(10, scoreConsumer);
        assertEquals(BendableScore.of(new int[]{-800}, new int[]{0, -13}), scoreInliner.extractScore(0));
        softUndo.undoScoreImpact();
        assertEquals(BendableScore.of(new int[]{-800}, new int[]{0, -10}), scoreInliner.extractScore(0));

        IntWeightedScoreImpacter allLevelsImpacter = scoreInliner.buildWeightedScoreImpacter(BendableScore.of(new int[]{-1000}, new int[]{-2000, -3000}));
        UndoScoreImpacter allLevelsUndo = allLevelsImpacter.impactScore(1, scoreConsumer);
        assertEquals(BendableScore.of(new int[]{-1800}, new int[]{-2000, -3010}), scoreInliner.extractScore(0));
        allLevelsUndo.undoScoreImpact();
        assertEquals(BendableScore.of(new int[]{-800}, new int[]{0, -10}), scoreInliner.extractScore(0));
    }

}
