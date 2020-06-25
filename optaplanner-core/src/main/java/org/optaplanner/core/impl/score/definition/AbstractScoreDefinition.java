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

package org.optaplanner.core.impl.score.definition;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;

/**
 * Abstract superclass for {@link ScoreDefinition}.
 *
 * @see ScoreDefinition
 * @see HardSoftScoreDefinition
 */
public abstract class AbstractScoreDefinition<S extends Score<S>> implements ScoreDefinition<S> {

    private final String[] levelLabels;

    protected static int sanitize(int number) {
        return number == 0 ? 1 : number;
    }

    protected static long sanitize(long number) {
        return number == 0L ? 1L : number;
    }

    protected static BigDecimal sanitize(BigDecimal number) {
        return number.signum() == 0 ? BigDecimal.ONE : number;
    }

    protected static int divide(int dividend, int divisor) {
        return (int) Math.floor(divide(dividend, (double) divisor));
    }

    protected static long divide(long dividend, long divisor) {
        return (long) Math.floor(divide(dividend, (double) divisor));
    }

    protected static double divide(double dividend, double divisor) {
        return dividend / divisor;
    }

    protected static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        return dividend.divide(divisor, dividend.scale() - divisor.scale(), RoundingMode.FLOOR);
    }

    /**
     * @param levelLabels never null, as defined by {@link ScoreDefinition#getLevelLabels()}
     */
    public AbstractScoreDefinition(String[] levelLabels) {
        this.levelLabels = levelLabels;
    }

    @Override
    public String getInitLabel() {
        return "init score";
    }

    @Override
    public int getLevelsSize() {
        return levelLabels.length;
    }

    @Override
    public String[] getLevelLabels() {
        return levelLabels;
    }

    @Override
    public String formatScore(S score) {
        return score.toString();
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score score) {
        return Objects.equals(score.getClass(), getScoreClass());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
