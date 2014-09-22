/*
 * Copyright 2010 JBoss Inc
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

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScoreHolder;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class SimpleScoreDefinition extends AbstractScoreDefinition<SimpleScore> {

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public int getLevelsSize() {
        return 1;
    }

    public Class<SimpleScore> getScoreClass() {
        return SimpleScore.class;
    }

    public SimpleScore parseScore(String scoreString) {
        return SimpleScore.parseScore(scoreString);
    }

    public SimpleScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new SimpleScoreHolder(constraintMatchEnabled);
    }

    public SimpleScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend, SimpleScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return SimpleScore.valueOf(
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getScore() : Integer.MAX_VALUE);
    }

    public SimpleScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend, SimpleScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return SimpleScore.valueOf(
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_UP ? score.getScore() : Integer.MIN_VALUE);
    }

}
