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

package org.optaplanner.core.impl.score.buildin.bendable;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.impl.score.inliner.IntWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.JustificationsSupplier;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

public class BendableScoreInlinerTest {

    @Test
    public void buildIntWeightedScoreImpacter() {
        boolean constraintMatchEnabled = false;
        JustificationsSupplier justificationsSupplier = null;

        BendableScoreInliner scoreInliner = new BendableScoreInliner(constraintMatchEnabled, 1, 2);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableScore.zero(1, 2));

        IntWeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter("constraintPackage", "constraintName",
                BendableScore.ofHard(1, 2, 0, -90));
        UndoScoreImpacter hardUndo = hardImpacter.impactScore(1, justificationsSupplier);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -90 }, new int[] { 0, 0 }));
        scoreInliner.buildWeightedScoreImpacter("constraintPackage", "constraintName", BendableScore.ofHard(1, 2, 0, -800))
                .impactScore(1, justificationsSupplier);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -890 }, new int[] { 0, 0 }));
        hardUndo.run();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -800 }, new int[] { 0, 0 }));

        IntWeightedScoreImpacter mediumImpacter = scoreInliner.buildWeightedScoreImpacter("constraintPackage", "constraintName",
                BendableScore.ofSoft(1, 2, 0, -7));
        UndoScoreImpacter mediumUndo = mediumImpacter.impactScore(1, justificationsSupplier);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -800 }, new int[] { -7, 0 }));
        mediumUndo.run();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -800 }, new int[] { 0, 0 }));

        IntWeightedScoreImpacter softImpacter = scoreInliner.buildWeightedScoreImpacter("constraintPackage", "constraintName",
                BendableScore.ofSoft(1, 2, 1, -1));
        UndoScoreImpacter softUndo = softImpacter.impactScore(3, justificationsSupplier);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -800 }, new int[] { 0, -3 }));
        softImpacter.impactScore(10, justificationsSupplier);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -800 }, new int[] { 0, -13 }));
        softUndo.run();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -800 }, new int[] { 0, -10 }));

        IntWeightedScoreImpacter allLevelsImpacter = scoreInliner
                .buildWeightedScoreImpacter("constraintPackage", "constraintName",
                        BendableScore.of(new int[] { -1000 }, new int[] { -2000, -3000 }));
        UndoScoreImpacter allLevelsUndo = allLevelsImpacter.impactScore(1, justificationsSupplier);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -1800 }, new int[] { -2000, -3010 }));
        allLevelsUndo.run();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(BendableScore.of(new int[] { -800 }, new int[] { 0, -10 }));
    }

}
