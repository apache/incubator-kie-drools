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

import java.io.File;

import org.drools.planner.core.Solver;

/**
 * TODO is this the correct package? Statistics can maybe be used outside the benchmarker
 * @author Geoffrey De Smet
 */
public interface SolverStatistic {

    void addListener(Solver solver, String configName);

    void removeListener(Solver solver, String configName);

    CharSequence writeStatistic(File solverStatisticFilesDirectory, String baseName);

}
