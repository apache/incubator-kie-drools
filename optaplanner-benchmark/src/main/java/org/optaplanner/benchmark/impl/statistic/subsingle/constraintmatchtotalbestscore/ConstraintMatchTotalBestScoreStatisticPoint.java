/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.statistic.subsingle.constraintmatchtotalbestscore;

import org.optaplanner.benchmark.impl.statistic.StatisticPoint;
import org.optaplanner.core.api.score.Score;

public class ConstraintMatchTotalBestScoreStatisticPoint extends StatisticPoint {

    private final long timeMillisSpent;
    private final String constraintPackage;
    private final String constraintName;
    private final int constraintMatchCount;
    private final Score scoreTotal;

    public ConstraintMatchTotalBestScoreStatisticPoint(long timeMillisSpent,
            String constraintPackage, String constraintName,
            int constraintMatchCount, Score scoreTotal) {
        this.timeMillisSpent = timeMillisSpent;
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        this.constraintMatchCount = constraintMatchCount;
        this.scoreTotal = scoreTotal;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public String getConstraintPackage() {
        return constraintPackage;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public int getConstraintMatchCount() {
        return constraintMatchCount;
    }

    public Score getScoreTotal() {
        return scoreTotal;
    }

    public String getConstraintId() {
        return constraintPackage + "/" + constraintName;
    }

    @Override
    public String toCsvLine() {
        return buildCsvLineWithStrings(timeMillisSpent, constraintPackage, constraintName,
                Integer.toString(constraintMatchCount), scoreTotal.toString());
    }

}
