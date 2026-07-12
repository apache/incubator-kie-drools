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

package org.optaplanner.core.impl.exhaustivesearch.node.bounder;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class TrendBasedScoreBounder implements ScoreBounder {

    protected final ScoreDefinition scoreDefinition;
    protected final InitializingScoreTrend initializingScoreTrend;

    public TrendBasedScoreBounder(ScoreDefinition scoreDefinition, InitializingScoreTrend initializingScoreTrend) {
        this.scoreDefinition = scoreDefinition;
        this.initializingScoreTrend = initializingScoreTrend;
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
