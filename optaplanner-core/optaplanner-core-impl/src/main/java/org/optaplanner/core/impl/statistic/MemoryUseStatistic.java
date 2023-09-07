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

package org.optaplanner.core.impl.statistic;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.solver.DefaultSolver;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;

public class MemoryUseStatistic<Solution_> implements SolverStatistic<Solution_> {

    @Override
    public void unregister(Solver<Solution_> solver) {
        // Intentionally Empty: JVM memory is not bound to a particular solver
    }

    @Override
    public void register(Solver<Solution_> solver) {
        DefaultSolver<Solution_> defaultSolver = (DefaultSolver<Solution_>) solver;
        new JvmMemoryMetrics(defaultSolver.getSolverScope().getMonitoringTags()).bindTo(Metrics.globalRegistry);
    }
}
