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

import java.math.BigDecimal;
import java.util.Arrays;

import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class HardMediumSoftBigDecimalScoreDefinition extends AbstractScoreDefinition<HardMediumSoftBigDecimalScore> {

    public HardMediumSoftBigDecimalScoreDefinition() {
        super(new String[] { "hard score", "medium score", "soft score" });
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public int getLevelsSize() {
        return 3;
    }

    @Override
    public int getFeasibleLevelsSize() {
        return 1;
    }

    @Override
    public Class<HardMediumSoftBigDecimalScore> getScoreClass() {
        return HardMediumSoftBigDecimalScore.class;
    }

    @Override
    public HardMediumSoftBigDecimalScore getZeroScore() {
        return HardMediumSoftBigDecimalScore.ZERO;
    }

    @Override
    public HardMediumSoftBigDecimalScore getOneSoftestScore() {
        return HardMediumSoftBigDecimalScore.ONE_SOFT;
    }

    @Override
    public HardMediumSoftBigDecimalScore parseScore(String scoreString) {
        return HardMediumSoftBigDecimalScore.parseScore(scoreString);
    }

    @Override
    public HardMediumSoftBigDecimalScore fromLevelNumbers(int initScore, Number[] levelNumbers) {
        if (levelNumbers.length != getLevelsSize()) {
            throw new IllegalStateException("The levelNumbers (" + Arrays.toString(levelNumbers)
                    + ")'s length (" + levelNumbers.length + ") must equal the levelSize (" + getLevelsSize() + ").");
        }
        return HardMediumSoftBigDecimalScore.ofUninitialized(initScore, (BigDecimal) levelNumbers[0],
                (BigDecimal) levelNumbers[1], (BigDecimal) levelNumbers[2]);
    }

    @Override
    public HardMediumSoftBigDecimalScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend,
            HardMediumSoftBigDecimalScore score) {
        // TODO https://issues.redhat.com/browse/PLANNER-232
        throw new UnsupportedOperationException("PLANNER-232: BigDecimalScore does not support bounds" +
                " because a BigDecimal cannot represent infinity.");
    }

    @Override
    public HardMediumSoftBigDecimalScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend,
            HardMediumSoftBigDecimalScore score) {
        // TODO https://issues.redhat.com/browse/PLANNER-232
        throw new UnsupportedOperationException("PLANNER-232: BigDecimalScore does not support bounds" +
                " because a BigDecimal cannot represent infinity.");
    }

    @Override
    public HardMediumSoftBigDecimalScore divideBySanitizedDivisor(HardMediumSoftBigDecimalScore dividend,
            HardMediumSoftBigDecimalScore divisor) {
        int dividendInitScore = dividend.initScore();
        int divisorInitScore = sanitize(divisor.initScore());
        BigDecimal dividendHardScore = dividend.hardScore();
        BigDecimal divisorHardScore = sanitize(divisor.hardScore());
        BigDecimal dividendMediumScore = dividend.mediumScore();
        BigDecimal divisorMediumScore = sanitize(divisor.mediumScore());
        BigDecimal dividendSoftScore = dividend.softScore();
        BigDecimal divisorSoftScore = sanitize(divisor.softScore());
        return fromLevelNumbers(
                divide(dividendInitScore, divisorInitScore),
                new Number[] {
                        divide(dividendHardScore, divisorHardScore),
                        divide(dividendMediumScore, divisorMediumScore),
                        divide(dividendSoftScore, divisorSoftScore)
                });
    }

    @Override
    public Class<?> getNumericType() {
        return BigDecimal.class;
    }
}
