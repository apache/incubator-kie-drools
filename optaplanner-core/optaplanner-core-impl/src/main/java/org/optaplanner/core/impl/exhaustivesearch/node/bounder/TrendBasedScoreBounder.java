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

package org.optaplanner.core.impl.exhaustivesearch.node.bounder;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class TrendBasedScoreBounder implements ScoreBounder {

    protected final ScoreDefinition scoreDefinition;
    protected final InitializingScoreTrend initializingScoreTrend;

    public TrendBasedScoreBounder(InnerScoreDirectorFactory scoreDirectorFactory) {
        scoreDefinition = scoreDirectorFactory.getScoreDefinition();
        initializingScoreTrend = scoreDirectorFactory.getInitializingScoreTrend();
    }

    @Override
    public Score calculateOptimisticBound(ScoreDirector scoreDirector, Score score) {
        return scoreDefinition.buildOptimisticBound(initializingScoreTrend, score);
    }

    @Override
    public Score calculatePessimisticBound(ScoreDirector scoreDirector, Score score) {
        return scoreDefinition.buildPessimisticBound(initializingScoreTrend, score);
    }

}
