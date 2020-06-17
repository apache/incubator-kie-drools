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

package org.optaplanner.core.impl.score.buildin.hardmediumsoftlong;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.impl.score.inliner.LongWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

public class HardMediumSoftLongScoreInlinerTest {

    @Test
    public void buildWeightedScoreImpacter() {
        boolean constraintMatchEnabled = false;
        Consumer<Score<?>> scoreConsumer = null;

        HardMediumSoftLongScoreInliner scoreInliner = new HardMediumSoftLongScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.ZERO);

        LongWeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(HardMediumSoftLongScore.ofHard(-90L));
        UndoScoreImpacter hardUndo = hardImpacter.impactScore(1L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-90L, 0L, 0L));
        scoreInliner.buildWeightedScoreImpacter(HardMediumSoftLongScore.ofHard(-800L)).impactScore(1L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-890L, 0L, 0L));
        hardUndo.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-800L, 0L, 0L));

        LongWeightedScoreImpacter mediumImpacter = scoreInliner
                .buildWeightedScoreImpacter(HardMediumSoftLongScore.ofMedium(-7L));
        UndoScoreImpacter mediumUndo = mediumImpacter.impactScore(1L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-800L, -7L, 0L));
        mediumUndo.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-800L, 0L, 0L));

        LongWeightedScoreImpacter softImpacter = scoreInliner.buildWeightedScoreImpacter(HardMediumSoftLongScore.ofSoft(-1L));
        UndoScoreImpacter softUndo = softImpacter.impactScore(3L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-800L, 0L, -3L));
        softImpacter.impactScore(10L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-800L, 0L, -13L));
        softUndo.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-800L, 0L, -10L));

        LongWeightedScoreImpacter allLevelsImpacter = scoreInliner
                .buildWeightedScoreImpacter(HardMediumSoftLongScore.of(-1000L, -2000L, -3000L));
        UndoScoreImpacter allLevelsUndo = allLevelsImpacter.impactScore(1L, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-1800L, -2000L, -3010L));
        allLevelsUndo.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-800L, 0L, -10L));
    }

}
