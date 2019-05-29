/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScoreHolder;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class SimpleScoreDefinition extends AbstractScoreDefinition<SimpleScore> {

    public SimpleScoreDefinition() {
        super(new String[]{"score"});
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public int getLevelsSize() {
        return 1;
    }

    @Override
    public Class<SimpleScore> getScoreClass() {
        return SimpleScore.class;
    }

    @Override
    public SimpleScore getZeroScore() {
        return SimpleScore.ZERO;
    }

    @Override
    public SimpleScore parseScore(String scoreString) {
        return SimpleScore.parseScore(scoreString);
    }

    @Override
    public SimpleScore fromLevelNumbers(int initScore, Number[] levelNumbers) {
        if (levelNumbers.length != getLevelsSize()) {
            throw new IllegalStateException("The levelNumbers (" + Arrays.toString(levelNumbers)
                    + ")'s length (" + levelNumbers.length + ") must equal the levelSize (" + getLevelsSize() + ").");
        }
        return SimpleScore.ofUninitialized(initScore, (Integer) levelNumbers[0]);
    }

    @Override
    public SimpleScoreInliner buildScoreInliner(boolean constraintMatchEnabled) {
        return new SimpleScoreInliner(constraintMatchEnabled);
    }

    @Override
    public SimpleScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new SimpleScoreHolder(constraintMatchEnabled);
    }

    @Override
    public SimpleScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend, SimpleScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return SimpleScore.ofUninitialized(0,
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getScore() : Integer.MAX_VALUE);
    }

    @Override
    public SimpleScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend, SimpleScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return SimpleScore.ofUninitialized(0,
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_UP ? score.getScore() : Integer.MIN_VALUE);
    }

}
