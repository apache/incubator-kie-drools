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

package org.optaplanner.core.impl.score.buildin.hardsoft;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.inliner.IntWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

public class HardSoftScoreInlinerTest {

    @Test
    public void buildIntWeightedScoreImpacter() {
        boolean constraintMatchEnabled = false;
        Consumer<Score<?>> scoreConsumer = null;

        HardSoftScoreInliner scoreInliner = new HardSoftScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardSoftScore.ZERO);

        IntWeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(HardSoftScore.ofHard(-90));
        UndoScoreImpacter hardUndo = hardImpacter.impactScore(1, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardSoftScore.of(-90, 0));
        scoreInliner.buildWeightedScoreImpacter(HardSoftScore.ofHard(-800)).impactScore(1, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardSoftScore.of(-890, 0));
        hardUndo.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardSoftScore.of(-800, 0));

        IntWeightedScoreImpacter softImpacter = scoreInliner.buildWeightedScoreImpacter(HardSoftScore.ofSoft(-1));
        UndoScoreImpacter softUndo = softImpacter.impactScore(3, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardSoftScore.of(-800, -3));
        softImpacter.impactScore(10, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardSoftScore.of(-800, -13));
        softUndo.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardSoftScore.of(-800, -10));

        IntWeightedScoreImpacter allLevelsImpacter = scoreInliner.buildWeightedScoreImpacter(HardSoftScore.of(-1000, -3000));
        UndoScoreImpacter allLevelsUndo = allLevelsImpacter.impactScore(1, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardSoftScore.of(-1800, -3010));
        allLevelsUndo.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardSoftScore.of(-800, -10));
    }

}
