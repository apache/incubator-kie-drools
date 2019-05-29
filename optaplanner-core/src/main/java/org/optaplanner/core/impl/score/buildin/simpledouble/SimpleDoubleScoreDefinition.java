/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.buildin.simpledouble;

import java.util.Arrays;

import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScore;
import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScoreHolder;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class SimpleDoubleScoreDefinition extends AbstractScoreDefinition<SimpleDoubleScore> {

    public SimpleDoubleScoreDefinition() {
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
    public Class<SimpleDoubleScore> getScoreClass() {
        return SimpleDoubleScore.class;
    }

    @Override
    public SimpleDoubleScore getZeroScore() {
        return SimpleDoubleScore.ZERO;
    }

    @Override
    public SimpleDoubleScore parseScore(String scoreString) {
        return SimpleDoubleScore.parseScore(scoreString);
    }

    @Override
    public SimpleDoubleScore fromLevelNumbers(int initScore, Number[] levelNumbers) {
        if (levelNumbers.length != getLevelsSize()) {
            throw new IllegalStateException("The levelNumbers (" + Arrays.toString(levelNumbers)
                    + ")'s length (" + levelNumbers.length + ") must equal the levelSize (" + getLevelsSize() + ").");
        }
        return SimpleDoubleScore.ofUninitialized(initScore, (Double) levelNumbers[0]);
    }

    @Override
    public ScoreInliner<SimpleDoubleScore> buildScoreInliner(boolean constraintMatchEnabled) {
        throw new IllegalStateException("ConstraintStreams don't support a " + SimpleDoubleScore.class.getSimpleName()
                + ") because it is error prone.");
    }


    @Override
    public SimpleDoubleScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new SimpleDoubleScoreHolder(constraintMatchEnabled);
    }

    @Override
    public SimpleDoubleScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend, SimpleDoubleScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return SimpleDoubleScore.ofUninitialized(0,
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getScore() : Double.POSITIVE_INFINITY);
    }

    @Override
    public SimpleDoubleScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend, SimpleDoubleScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return SimpleDoubleScore.ofUninitialized(0,
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_UP ? score.getScore() : Double.NEGATIVE_INFINITY);
    }

}
