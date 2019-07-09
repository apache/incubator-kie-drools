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

package org.optaplanner.core.impl.score.buildin.simplebigdecimal;

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.impl.score.inliner.BigDecimalWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

import static org.junit.Assert.*;

public class SimpleBigDecimalScoreInlinerTest {

    @Test
    public void buildWeightedScoreImpacter() {
        boolean constraintMatchEnabled = false;
        Consumer<Score<?>> scoreConsumer = null;

        SimpleBigDecimalScoreInliner scoreInliner = new SimpleBigDecimalScoreInliner(constraintMatchEnabled);
        assertEquals(SimpleBigDecimalScore.ZERO, scoreInliner.extractScore(0));

        BigDecimalWeightedScoreImpacter impacter1 = scoreInliner.buildWeightedScoreImpacter(SimpleBigDecimalScore.of(new BigDecimal("90.0")));
        UndoScoreImpacter undo1 = impacter1.impactScore(new BigDecimal("1.0"), scoreConsumer);
        assertEquals(SimpleBigDecimalScore.of(new BigDecimal("90.0")), scoreInliner.extractScore(0));
        scoreInliner.buildWeightedScoreImpacter(SimpleBigDecimalScore.of(new BigDecimal("800.0"))).impactScore(new BigDecimal("1.0"), scoreConsumer);
        assertEquals(SimpleBigDecimalScore.of(new BigDecimal("890.0")), scoreInliner.extractScore(0));
        undo1.undoScoreImpact();
        assertEquals(SimpleBigDecimalScore.of(new BigDecimal("800.0")), scoreInliner.extractScore(0));

        BigDecimalWeightedScoreImpacter impacter2 = scoreInliner.buildWeightedScoreImpacter(SimpleBigDecimalScore.of(new BigDecimal("1.0")));
        UndoScoreImpacter undo2 = impacter2.impactScore(new BigDecimal("3.0"), scoreConsumer);
        assertEquals(SimpleBigDecimalScore.of(new BigDecimal("803.0")), scoreInliner.extractScore(0));
        impacter2.impactScore(new BigDecimal("10.0"), scoreConsumer);
        assertEquals(SimpleBigDecimalScore.of(new BigDecimal("813.0")), scoreInliner.extractScore(0));
        undo2.undoScoreImpact();
        assertEquals(SimpleBigDecimalScore.of(new BigDecimal("810.0")), scoreInliner.extractScore(0));
    }

}
