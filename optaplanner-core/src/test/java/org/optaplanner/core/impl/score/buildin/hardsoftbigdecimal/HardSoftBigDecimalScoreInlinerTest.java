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

package org.optaplanner.core.impl.score.buildin.hardsoftbigdecimal;

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.impl.score.inliner.BigDecimalWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

import static org.junit.Assert.*;

public class HardSoftBigDecimalScoreInlinerTest {

    @Test
    public void buildWeightedScoreImpacter() {
        boolean constraintMatchEnabled = false;
        Consumer<Score<?>> scoreConsumer = null;

        HardSoftBigDecimalScoreInliner scoreInliner = new HardSoftBigDecimalScoreInliner(constraintMatchEnabled);
        assertEquals(HardSoftBigDecimalScore.ZERO, scoreInliner.extractScore(0));

        BigDecimalWeightedScoreImpacter hardImpacter = scoreInliner.buildWeightedScoreImpacter(HardSoftBigDecimalScore.ofHard(new BigDecimal("90.0")));
        UndoScoreImpacter hardUndo = hardImpacter.impactScore(new BigDecimal("1.0"), scoreConsumer);
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("90.0"), BigDecimal.ZERO), scoreInliner.extractScore(0));
        scoreInliner.buildWeightedScoreImpacter(HardSoftBigDecimalScore.ofHard(new BigDecimal("800.0"))).impactScore(new BigDecimal("1.0"), scoreConsumer);
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("890.0"), BigDecimal.ZERO), scoreInliner.extractScore(0));
        hardUndo.undoScoreImpact();
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("800.0"), BigDecimal.ZERO), scoreInliner.extractScore(0));

        BigDecimalWeightedScoreImpacter softImpacter = scoreInliner.buildWeightedScoreImpacter(HardSoftBigDecimalScore.ofSoft(new BigDecimal("1.0")));
        UndoScoreImpacter softUndo = softImpacter.impactScore(new BigDecimal("3.0"), scoreConsumer);
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("800.0"), new BigDecimal("3.0")), scoreInliner.extractScore(0));
        softImpacter.impactScore(new BigDecimal("10.0"), scoreConsumer);
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("800.0"), new BigDecimal("13.0")), scoreInliner.extractScore(0));
        softUndo.undoScoreImpact();
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("800.0"), new BigDecimal("10.0")), scoreInliner.extractScore(0));

        BigDecimalWeightedScoreImpacter allLevelsImpacter = scoreInliner.buildWeightedScoreImpacter(HardSoftBigDecimalScore.of(new BigDecimal("1000.0"), new BigDecimal("3000.0")));
        UndoScoreImpacter allLevelsUndo = allLevelsImpacter.impactScore(new BigDecimal("1.0"), scoreConsumer);
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("1800.0"), new BigDecimal("3010.0")), scoreInliner.extractScore(0));
        allLevelsUndo.undoScoreImpact();
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("800.0"), new BigDecimal("10.0")), scoreInliner.extractScore(0));
    }

}
