/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score;

import java.math.BigDecimal;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardsoftdouble.HardSoftDoubleScore;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;

import static org.junit.Assert.*;

public class ScoreUtilsTest {

    @Test
    public void parseScore() {
        assertEquals(SimpleScore.of(-1000), ScoreUtils.parseScore(SimpleScore.class, "-1000"));
        assertEquals(SimpleLongScore.of(-1000L), ScoreUtils.parseScore(SimpleLongScore.class, "-1000"));
        assertEquals(SimpleDoubleScore.of(-1000.0), ScoreUtils.parseScore(SimpleDoubleScore.class, "-1000.0"));
        assertEquals(SimpleBigDecimalScore.of(new BigDecimal("-1000")),
                ScoreUtils.parseScore(SimpleBigDecimalScore.class, "-1000"));
        assertEquals(HardSoftScore.of(-1000, -200), ScoreUtils.parseScore(HardSoftScore.class, "-1000hard/-200soft"));
        assertEquals(HardSoftLongScore.of(-1000L, -200L), ScoreUtils.parseScore(HardSoftLongScore.class, "-1000hard/-200soft"));
        assertEquals(HardSoftDoubleScore.of(-1000.0, -200.0), ScoreUtils.parseScore(HardSoftDoubleScore.class, "-1000hard/-200soft"));
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("-1000"), new BigDecimal("-200")),
                ScoreUtils.parseScore(HardSoftBigDecimalScore.class, "-1000hard/-200soft"));
        assertEquals(HardMediumSoftScore.of(-1000, -200, -30), ScoreUtils.parseScore(HardMediumSoftScore.class, "-1000hard/-200medium/-30soft"));
        assertEquals(HardMediumSoftLongScore.of(-1000L, -200L, -30L), ScoreUtils.parseScore(HardMediumSoftLongScore.class, "-1000hard/-200medium/-30soft"));
        assertEquals(BendableScore.of(new int[] {-1000}, new int[]{-200, -30}), ScoreUtils.parseScore(BendableScore.class, "[-1000]hard/[-200/-30]soft"));
        assertEquals(BendableLongScore.of(new long[] {-1000L}, new long[]{-200L, -30L}), ScoreUtils.parseScore(BendableLongScore.class, "[-1000]hard/[-200/-30]soft"));
        assertEquals(BendableBigDecimalScore.of(new BigDecimal[] {new BigDecimal("-1000")}, new BigDecimal[]{new BigDecimal("-200"), new BigDecimal("-30")}),
                ScoreUtils.parseScore(BendableBigDecimalScore.class, "[-1000]hard/[-200/-30]soft"));
    }

}
