/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.benchmark.statistic.calculatecount;

import java.util.ArrayList;
import java.util.List;

import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.event.LocalSearchSolverLifecycleListenerAdapter;

public class CalculateCountStatisticListener extends LocalSearchSolverLifecycleListenerAdapter {

    private long timeMillisThresholdInterval;
    private long nextTimeMillisThreshold;

    private long lastTimeMillisSpend = 0L;
    private long lastCalculateCount = 0L;

    private List<CalculateCountStatisticPoint> statisticPointList = new ArrayList<CalculateCountStatisticPoint>();

    public CalculateCountStatisticListener() {
        this(1000L);
    }

    public CalculateCountStatisticListener(long timeMillisThresholdInterval) {
        if (timeMillisThresholdInterval <= 0L) {
            throw new IllegalArgumentException("The timeMillisThresholdInterval (" + timeMillisThresholdInterval
                    + ") must be bigger than 0.");
        }
        this.timeMillisThresholdInterval = timeMillisThresholdInterval;
        nextTimeMillisThreshold = timeMillisThresholdInterval;
    }

    public List<CalculateCountStatisticPoint> getStatisticPointList() {
        return statisticPointList;
    }

    @Override
    public void stepTaken(LocalSearchStepScope localSearchStepScope) {
        long timeMillisSpend = localSearchStepScope.getLocalSearchSolverScope().calculateTimeMillisSpend();
        if (timeMillisSpend >= nextTimeMillisThreshold) {
            long timeMillisSpendInterval = timeMillisSpend - lastTimeMillisSpend;
            long calculateCount = localSearchStepScope.getLocalSearchSolverScope().getCalculateCount();
            long calculateCountInterval = calculateCount - lastCalculateCount;
            long averageCalculateCountPerSecond = calculateCountInterval * 1000L / timeMillisSpendInterval;
            statisticPointList.add(new CalculateCountStatisticPoint(timeMillisSpend, averageCalculateCountPerSecond));

            lastTimeMillisSpend = timeMillisSpend;
            lastCalculateCount = calculateCount;
            nextTimeMillisThreshold += timeMillisThresholdInterval;
        }
    }
    
    // TODO registerForSolver

}
