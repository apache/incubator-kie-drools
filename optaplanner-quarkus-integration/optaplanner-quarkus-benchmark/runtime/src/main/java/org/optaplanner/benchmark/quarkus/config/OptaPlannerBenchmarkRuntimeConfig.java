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

package org.optaplanner.benchmark.quarkus.config;

import org.optaplanner.quarkus.config.TerminationRuntimeConfig;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "quarkus.optaplanner.benchmark")
public interface OptaPlannerBenchmarkRuntimeConfig {
    public static final String DEFAULT_BENCHMARK_RESULT_DIRECTORY = "target/benchmarks";

    /**
     * Where the benchmark results are written to. Defaults to
     * {@link DEFAULT_BENCHMARK_RESULT_DIRECTORY}.
     */
    @WithDefault(DEFAULT_BENCHMARK_RESULT_DIRECTORY)
    String resultDirectory();

    /**
     * Termination configuration for the solvers run in the benchmark.
     */
    @WithName("solver.termination")
    TerminationRuntimeConfig termination();
}
