/**
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

package org.drools.planner.benchmark.statistic;

import org.drools.planner.core.event.BestSolutionChangedEvent;
import org.drools.planner.core.score.Score;

/**
 * @author Geoffrey De Smet
 */
public class BestScoreStatisticPoint {

    private long timeMillisSpend;
    private Score score;

    public BestScoreStatisticPoint(BestSolutionChangedEvent event) {
        timeMillisSpend = event.getTimeMillisSpend();
        score = event.getNewBestSolution().getScore();
    }

    public long getTimeMillisSpend() {
        return timeMillisSpend;
    }

    public Score getScore() {
        return score;
    }

}
