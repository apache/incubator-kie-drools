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

package org.optaplanner.benchmark.impl.statistic.movecountperstep;

import java.util.List;

import org.optaplanner.benchmark.impl.statistic.StatisticPoint;

public class MoveCountPerStepStatisticPoint extends StatisticPoint {

    private final long timeMillisSpend;
    private final MoveCountPerStepMeasurement moveCountPerStepMeasurement;

    public MoveCountPerStepStatisticPoint(long timeMillisSpend,
            MoveCountPerStepMeasurement moveCountPerStepMeasurement) {
        this.timeMillisSpend = timeMillisSpend;
        this.moveCountPerStepMeasurement = moveCountPerStepMeasurement;
    }

    public MoveCountPerStepMeasurement getMoveCountPerStepMeasurement() {
        return moveCountPerStepMeasurement;
    }

    public long getTimeMillisSpend() {
        return timeMillisSpend;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithLongs(timeMillisSpend, moveCountPerStepMeasurement.getAcceptedMoveCount(),
                moveCountPerStepMeasurement.getSelectedMoveCount());
    }
   
}
