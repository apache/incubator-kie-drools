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

package org.drools.planner.benchmark.core.statistic;

import java.io.File;

public abstract class AbstractProblemStatistic implements ProblemStatistic {

    protected ProblemStatisticType problemStatisticType;

    protected AbstractProblemStatistic(ProblemStatisticType problemStatisticType) {
        this.problemStatisticType = problemStatisticType;
    }

    public CharSequence writeStatistic(File statisticDirectory, String baseName) {
        StringBuilder htmlFragment = new StringBuilder();
        htmlFragment.append("  <h3>").append(problemStatisticType.toString()).append("</h3>\n");
        htmlFragment.append(writeCsvStatistic(statisticDirectory, baseName));
        htmlFragment.append(writeGraphStatistic(statisticDirectory, baseName));
        return htmlFragment;
    }

    protected abstract CharSequence writeCsvStatistic(File statisticDirectory, String baseName);

    protected abstract CharSequence writeGraphStatistic(File statisticDirectory, String baseName);

    protected static abstract class AbstractProblemStatisticScvLine implements Comparable<AbstractProblemStatisticScvLine> {

        protected long timeMillisSpend;

        public AbstractProblemStatisticScvLine(long timeMillisSpend) {
            this.timeMillisSpend = timeMillisSpend;
        }

        public long getTimeMillisSpend() {
            return timeMillisSpend;
        }

        public int compareTo(AbstractProblemStatisticScvLine other) {
            return timeMillisSpend < other.timeMillisSpend ? -1 : (timeMillisSpend > other.timeMillisSpend ? 1 : 0);
        }

    }

}
