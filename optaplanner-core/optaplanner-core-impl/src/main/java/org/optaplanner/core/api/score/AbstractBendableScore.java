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

import java.util.function.Predicate;

import org.optaplanner.core.impl.score.ScoreUtil;

/**
 * Abstract superclass for bendable {@link Score} types.
 * <p>
 * Subclasses must be immutable.
 *
 * @deprecated Implement {@link IBendableScore} instead.
 */
@Deprecated(forRemoval = true)
public abstract class AbstractBendableScore<Score_ extends AbstractBendableScore<Score_>>
        extends AbstractScore<Score_>
        implements IBendableScore<Score_> {

    protected static final String HARD_LABEL = ScoreUtil.HARD_LABEL;
    protected static final String SOFT_LABEL = ScoreUtil.SOFT_LABEL;
    protected static final String[] LEVEL_SUFFIXES = ScoreUtil.LEVEL_SUFFIXES;

    protected static String[][] parseBendableScoreTokens(Class<? extends AbstractBendableScore<?>> scoreClass,
            String scoreString) {
        return ScoreUtil.parseBendableScoreTokens(scoreClass, scoreString);
    }

    /**
     * @param initScore see {@link Score#initScore()}
     */
    protected AbstractBendableScore(int initScore) {
        super(initScore);
    }

    protected String buildBendableShortString(Predicate<Number> notZero) {
        return ScoreUtil.buildBendableShortString(this, notZero);
    }

}
