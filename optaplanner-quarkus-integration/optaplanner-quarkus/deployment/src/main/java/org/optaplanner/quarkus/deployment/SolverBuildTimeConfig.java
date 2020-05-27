/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.quarkus.deployment;

import java.util.Optional;

import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

/**
 * During build time, this is translated into OptaPlanner's {@link SolverConfig}.
 */
@ConfigGroup
public class SolverBuildTimeConfig {

    public static final String DEFAULT_SCORE_DRL_URL = "constraints.drl";

    /**
     * Enable runtime assertions to detect common bugs in your implementation during development.
     * Defaults to {@link EnvironmentMode#REPRODUCIBLE}.
     */
    @ConfigItem
    Optional<EnvironmentMode> environmentMode;
    /**
     * Enable multithreaded solving for a single problem, which increases CPU consumption.
     * Defaults to {@value SolverConfig#MOVE_THREAD_COUNT_NONE}.
     * Other options include {@value SolverConfig#MOVE_THREAD_COUNT_AUTO}, a number
     * or formula based on the available processor count.
     */
    @ConfigItem
    Optional<String> moveThreadCount;

    /**
     * Configuration properties that overwrite OptaPlanner's {@link TerminationConfig}.
     */
    @ConfigItem
    TerminationBuildTimeConfig termination;

}
