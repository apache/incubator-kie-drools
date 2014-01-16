/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.benchmark.impl.statistic.stepscore;

import java.util.List;

import org.optaplanner.benchmark.impl.statistic.StatisticPoint;
import org.optaplanner.core.api.score.Score;

public class StepScoreStatisticPoint extends StatisticPoint {

    private final long timeMillisSpend;
    private final Score score;

    public StepScoreStatisticPoint(long timeMillisSpend, Score score) {
        this.timeMillisSpend = timeMillisSpend;
        this.score = score;
    }

    public long getTimeMillisSpend() {
        return timeMillisSpend;
    }

    public Score getScore() {
        return score;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithStrings(timeMillisSpend, score.toString());
    }

}
