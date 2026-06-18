/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.score.buildin;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;

class SimpleBigDecimalScoreDefinitionTest {

    @Test
    void getZeroScore() {
        SimpleBigDecimalScore score = new SimpleBigDecimalScoreDefinition().getZeroScore();
        assertThat(score).isEqualTo(SimpleBigDecimalScore.ZERO);
    }

    @Test
    void getSoftestOneScore() {
        SimpleBigDecimalScore score = new SimpleBigDecimalScoreDefinition().getOneSoftestScore();
        assertThat(score).isEqualTo(SimpleBigDecimalScore.ONE);
    }

    @Test
    void getLevelsSize() {
        assertThat(new SimpleBigDecimalScoreDefinition().getLevelsSize()).isEqualTo(1);
    }

    @Test
    void getLevelLabels() {
        assertThat(new SimpleBigDecimalScoreDefinition().getLevelLabels()).containsExactly("score");
    }

    // Optimistic and pessimistic bounds are currently not supported for this score definition

    @Test
    void divideBySanitizedDivisor() {
        SimpleBigDecimalScoreDefinition scoreDefinition = new SimpleBigDecimalScoreDefinition();
        SimpleBigDecimalScore dividend = scoreDefinition.fromLevelNumbers(2, new Number[] { BigDecimal.TEN });
        SimpleBigDecimalScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        SimpleBigDecimalScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        SimpleBigDecimalScore tenDivisor = scoreDefinition.fromLevelNumbers(10, new Number[] { BigDecimal.TEN });
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.fromLevelNumbers(0, new Number[] { BigDecimal.ONE }));
    }

}
