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

package org.drools.planner.benchmark.statistic.memoryuse;

import java.util.ArrayList;
import java.util.List;

import org.drools.planner.core.solver.AbstractStepScope;
import org.drools.planner.core.solver.event.SolverPhaseLifecycleListenerAdapter;

public class MemoryUseStatisticListener extends SolverPhaseLifecycleListenerAdapter {

    private long timeMillisThresholdInterval;
    private long nextTimeMillisThreshold;

    private List<MemoryUseStatisticPoint> statisticPointList = new ArrayList<MemoryUseStatisticPoint>();

    public MemoryUseStatisticListener() {
        this(1000L);
    }

    public MemoryUseStatisticListener(long timeMillisThresholdInterval) {
        if (timeMillisThresholdInterval <= 0L) {
            throw new IllegalArgumentException("The timeMillisThresholdInterval (" + timeMillisThresholdInterval
                    + ") must be bigger than 0.");
        }
        this.timeMillisThresholdInterval = timeMillisThresholdInterval;
        nextTimeMillisThreshold = timeMillisThresholdInterval;
    }

    public List<MemoryUseStatisticPoint> getStatisticPointList() {
        return statisticPointList;
    }

    @Override
    public void stepTaken(AbstractStepScope stepScope) {
        long timeMillisSpend = stepScope.getSolverPhaseScope().calculateTimeMillisSpend();
        if (timeMillisSpend >= nextTimeMillisThreshold) {

            statisticPointList.add(new MemoryUseStatisticPoint(timeMillisSpend, MemoryUseMeasurement.create()));

            nextTimeMillisThreshold += timeMillisThresholdInterval;
            if (nextTimeMillisThreshold < timeMillisSpend) {
                nextTimeMillisThreshold = timeMillisSpend;
            }
        }
    }
    
    // TODO registerForSolver

}
