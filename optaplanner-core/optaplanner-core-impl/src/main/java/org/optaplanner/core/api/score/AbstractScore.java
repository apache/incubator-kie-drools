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

package org.optaplanner.core.api.score;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.function.Predicate;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.ScoreUtil;

/**
 * Abstract superclass for {@link Score}.
 * <p>
 * Subclasses must be immutable.
 *
 * @param <Score_> the actual score type
 * @see Score
 * @see HardSoftScore
 * @deprecated Implement {@link Score} instead.
 */
@Deprecated(forRemoval = true)
public abstract class AbstractScore<Score_ extends AbstractScore<Score_>> implements Score<Score_>,
        Serializable {

    protected static final String INIT_LABEL = ScoreUtil.INIT_LABEL;

    protected static String[] parseScoreTokens(Class<? extends AbstractScore<?>> scoreClass,
            String scoreString, String... levelSuffixes) {
        return ScoreUtil.parseScoreTokens(scoreClass, scoreString, levelSuffixes);
    }

    protected static int parseInitScore(Class<? extends AbstractScore<?>> scoreClass,
            String scoreString, String initScoreString) {
        return ScoreUtil.parseInitScore(scoreClass, scoreString, initScoreString);
    }

    protected static int parseLevelAsInt(Class<? extends AbstractScore<?>> scoreClass,
            String scoreString, String levelString) {
        return ScoreUtil.parseLevelAsInt(scoreClass, scoreString, levelString);
    }

    protected static long parseLevelAsLong(Class<? extends AbstractScore<?>> scoreClass,
            String scoreString, String levelString) {
        return ScoreUtil.parseLevelAsLong(scoreClass, scoreString, levelString);
    }

    protected static BigDecimal parseLevelAsBigDecimal(Class<? extends AbstractScore<?>> scoreClass,
            String scoreString, String levelString) {
        return ScoreUtil.parseLevelAsBigDecimal(scoreClass, scoreString, levelString);
    }

    protected static String buildScorePattern(boolean bendable, String... levelSuffixes) {
        return ScoreUtil.buildScorePattern(bendable, levelSuffixes);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    protected final int initScore;

    /**
     * @param initScore see {@link Score#initScore()}
     */
    protected AbstractScore(int initScore) {
        this.initScore = initScore;
        // The initScore can be positive during statistical calculations.
    }

    @Override
    public int initScore() {
        return initScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    protected String getInitPrefix() {
        return ScoreUtil.getInitPrefix(initScore);
    }

    protected String buildShortString(Predicate<Number> notZero, String... levelLabels) {
        return ScoreUtil.buildShortString(this, notZero, levelLabels);
    }

}
