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

import org.optaplanner.core.config.solver.SolverManagerConfig;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

/**
 * During build time, this is translated into OptaPlanner's {@link SolverManagerConfig}.
 */
@ConfigGroup
public class SolverManagerBuildTimeConfig {

    /**
     * The number of solvers that run in parallel. This directly influences CPU consumption.
     * Defaults to {@value SolverManagerConfig#PARALLEL_SOLVER_COUNT_AUTO}.
     * Other options include a number or formula based on the available processor count.
     */
    @ConfigItem
    Optional<String> parallelSolverCount;

}
