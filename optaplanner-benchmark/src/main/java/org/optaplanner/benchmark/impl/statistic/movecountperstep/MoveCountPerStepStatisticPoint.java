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

package org.optaplanner.benchmark.impl.statistic.movecountperstep;

import org.optaplanner.benchmark.impl.statistic.StatisticPoint;

public class MoveCountPerStepStatisticPoint extends StatisticPoint {

    private final long timeMillisSpent;
    private final long acceptedMoveCount;
    private final long selectedMoveCount;

    public MoveCountPerStepStatisticPoint(long timeMillisSpent, long acceptedMoveCount, long selectedMoveCount) {
        this.timeMillisSpent = timeMillisSpent;
        this.acceptedMoveCount = acceptedMoveCount;
        this.selectedMoveCount = selectedMoveCount;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public long getAcceptedMoveCount() {
        return acceptedMoveCount;
    }

    public long getSelectedMoveCount() {
        return selectedMoveCount;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithLongs(timeMillisSpent, acceptedMoveCount, selectedMoveCount);
    }

}
