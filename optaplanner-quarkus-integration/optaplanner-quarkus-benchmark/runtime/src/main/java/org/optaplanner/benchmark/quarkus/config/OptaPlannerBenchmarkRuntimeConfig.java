/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.quarkus.config;

import org.optaplanner.quarkus.config.TerminationRuntimeConfig;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "optaplanner.benchmark", phase = ConfigPhase.RUN_TIME)
public class OptaPlannerBenchmarkRuntimeConfig {
    public static final String DEFAULT_BENCHMARK_RESULT_DIRECTORY = "target/benchmarks";

    /**
     * Where the benchmark results are written to. Defaults to
     * {@link DEFAULT_BENCHMARK_RESULT_DIRECTORY}.
     */
    @ConfigItem(defaultValue = DEFAULT_BENCHMARK_RESULT_DIRECTORY)
    public String resultDirectory;

    /**
     * Termination configuration for the solvers run in the benchmark.
     */
    @ConfigItem(name = "solver.termination")
    public TerminationRuntimeConfig termination;
}
