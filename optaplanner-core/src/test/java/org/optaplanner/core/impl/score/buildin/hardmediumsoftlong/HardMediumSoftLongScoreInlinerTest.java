/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.impl.score.inliner.JustificationsSupplier;
import org.optaplanner.core.impl.score.inliner.LongWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

public class HardMediumSoftLongScoreInlinerTest {

    @Test
    public void buildWeightedScoreImpacter() {
        boolean constraintMatchEnabled = false;
        JustificationsSupplier justificationsSupplier = null;

        HardMediumSoftLongScoreInliner scoreInliner = new HardMediumSoftLongScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.ZERO);

        LongWeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter("constraintPackage", "constraintName",
                HardMediumSoftLongScore.ofHard(-90L));
        UndoScoreImpacter hardUndo = hardImpacter.impactScore(1L, justificationsSupplier);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-90L, 0L, 0L));
        scoreInliner.buildWeightedScoreImpacter("constraintPackage", "constraintName", HardMediumSoftLongScore.ofHard(-800L))
                .impactScore(1L, justificationsSupplier);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-890L, 0L, 0L));
        hardUndo.run();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-800L, 0L, 0L));

        LongWeightedScoreImpacter mediumImpacter = scoreInliner
                .buildWeightedScoreImpacter("constraintPackage", "constraintName", HardMediumSoftLongScore.ofMedium(-7L));
        UndoScoreImpacter mediumUndo = mediumImpacter.impactScore(1L, justificationsSupplier);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-800L, -7L, 0L));
        mediumUndo.run();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-800L, 0L, 0L));

        LongWeightedScoreImpacter softImpacter = scoreInliner.buildWeightedScoreImpacter("constraintPackage", "constraintName",
                HardMediumSoftLongScore.ofSoft(-1L));
        UndoScoreImpacter softUndo = softImpacter.impactScore(3L, justificationsSupplier);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-800L, 0L, -3L));
        softImpacter.impactScore(10L, justificationsSupplier);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-800L, 0L, -13L));
        softUndo.run();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-800L, 0L, -10L));

        LongWeightedScoreImpacter allLevelsImpacter = scoreInliner
                .buildWeightedScoreImpacter("constraintPackage", "constraintName",
                        HardMediumSoftLongScore.of(-1000L, -2000L, -3000L));
        UndoScoreImpacter allLevelsUndo = allLevelsImpacter.impactScore(1L, justificationsSupplier);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-1800L, -2000L, -3010L));
        allLevelsUndo.run();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(HardMediumSoftLongScore.of(-800L, 0L, -10L));
    }

}
