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

package org.optaplanner.core.impl.score.buildin.simple;

import java.util.function.Consumer;

import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.inliner.IntWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

import static org.junit.Assert.*;

public class SimpleScoreInlinerTest {

    @Test
    public void buildIntWeightedScoreImpacter() {
        boolean constraintMatchEnabled = false;
        Consumer<Score<?>> scoreConsumer = null;

        SimpleScoreInliner scoreInliner = new SimpleScoreInliner(constraintMatchEnabled);
        assertEquals(SimpleScore.ZERO, scoreInliner.extractScore(0));

        IntWeightedScoreImpacter impacter1 = scoreInliner.buildWeightedScoreImpacter(SimpleScore.of(-90));
        UndoScoreImpacter undo1 = impacter1.impactScore(1, scoreConsumer);
        assertEquals(SimpleScore.of(-90), scoreInliner.extractScore(0));
        scoreInliner.buildWeightedScoreImpacter(SimpleScore.of(-800)).impactScore(1, scoreConsumer);
        assertEquals(SimpleScore.of(-890), scoreInliner.extractScore(0));
        undo1.undoScoreImpact();
        assertEquals(SimpleScore.of(-800), scoreInliner.extractScore(0));

        IntWeightedScoreImpacter impacter2 = scoreInliner.buildWeightedScoreImpacter(SimpleScore.of(-1));
        UndoScoreImpacter undo2 = impacter2.impactScore(3, scoreConsumer);
        assertEquals(SimpleScore.of(-803), scoreInliner.extractScore(0));
        impacter2.impactScore(10, scoreConsumer);
        assertEquals(SimpleScore.of(-813), scoreInliner.extractScore(0));
        undo2.undoScoreImpact();
        assertEquals(SimpleScore.of(-810), scoreInliner.extractScore(0));
    }

}
