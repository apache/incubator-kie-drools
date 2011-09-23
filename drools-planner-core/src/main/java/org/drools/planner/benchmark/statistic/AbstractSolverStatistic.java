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

package org.drools.planner.benchmark.statistic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSolverStatistic implements SolverStatistic {

    protected SolverStatisticType solverStatisticType;

    protected AbstractSolverStatistic(SolverStatisticType solverStatisticType) {
        this.solverStatisticType = solverStatisticType;
    }

    public CharSequence writeStatistic(File solverStatisticFilesDirectory, String baseName) {
        StringBuilder htmlFragment = new StringBuilder();
        htmlFragment.append("  <h3>").append(solverStatisticType.toString()).append("</h3>\n");
        htmlFragment.append(writeCsvStatistic(solverStatisticFilesDirectory, baseName));
        htmlFragment.append(writeGraphStatistic(solverStatisticFilesDirectory, baseName));
        return htmlFragment;
    }

    protected abstract CharSequence writeCsvStatistic(File solverStatisticFilesDirectory, String baseName);

    protected abstract CharSequence writeGraphStatistic(File solverStatisticFilesDirectory, String baseName);

    protected abstract class AbstractSolverStatisticScvLine implements Comparable<AbstractSolverStatisticScvLine> {

        protected long timeMillisSpend;

        public AbstractSolverStatisticScvLine(long timeMillisSpend) {
            this.timeMillisSpend = timeMillisSpend;
        }

        public long getTimeMillisSpend() {
            return timeMillisSpend;
        }

        public int compareTo(AbstractSolverStatisticScvLine other) {
            return timeMillisSpend < other.timeMillisSpend ? -1 : (timeMillisSpend > other.timeMillisSpend ? 1 : 0);
        }

    }

}
