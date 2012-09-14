/*
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

package org.drools.planner.benchmark.core.statistic;

import java.io.File;
import java.util.List;

import org.drools.planner.benchmark.core.DefaultPlannerBenchmark;
import org.drools.planner.benchmark.core.ProblemBenchmark;
import org.drools.planner.core.Solver;

/**
 * 1 statistic of {@link ProblemBenchmark}
 */
public interface ProblemStatistic {

    /**
     * @return never null
     */
    ProblemStatisticType getProblemStatisticType();

    /**
     * @return never null
     */
    String getAnchorId();

    /**
     * This method is thread-safe.
     * @return never null
     */
    SingleStatistic createSingleStatistic();

    void writeStatistic();

    /**
     * @return never null, relative to the {@link DefaultPlannerBenchmark#benchmarkReportDirectory}
     * (not {@link ProblemBenchmark#problemReportDirectory})
     */
    String getCsvFilePath();

    /**
     * @return never null
     */
    List<String> getWarningList();

}
