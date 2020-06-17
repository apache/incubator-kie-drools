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

package org.optaplanner.core.impl.score.buildin.simple;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.inliner.IntWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

public class SimpleScoreInlinerTest {

    @Test
    public void buildIntWeightedScoreImpacter() {
        boolean constraintMatchEnabled = false;
        Consumer<Score<?>> scoreConsumer = null;

        SimpleScoreInliner scoreInliner = new SimpleScoreInliner(constraintMatchEnabled);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleScore.ZERO);

        IntWeightedScoreImpacter impacter1 = scoreInliner.buildWeightedScoreImpacter(SimpleScore.of(-90));
        UndoScoreImpacter undo1 = impacter1.impactScore(1, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleScore.of(-90));
        scoreInliner.buildWeightedScoreImpacter(SimpleScore.of(-800)).impactScore(1, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleScore.of(-890));
        undo1.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleScore.of(-800));

        IntWeightedScoreImpacter impacter2 = scoreInliner.buildWeightedScoreImpacter(SimpleScore.of(-1));
        UndoScoreImpacter undo2 = impacter2.impactScore(3, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleScore.of(-803));
        impacter2.impactScore(10, scoreConsumer);
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleScore.of(-813));
        undo2.undoScoreImpact();
        assertThat(scoreInliner.extractScore(0)).isEqualTo(SimpleScore.of(-810));
    }

}
