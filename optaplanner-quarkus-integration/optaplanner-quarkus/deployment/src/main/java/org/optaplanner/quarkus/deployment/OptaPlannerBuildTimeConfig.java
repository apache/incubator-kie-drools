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

import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * During build time, this is translated into OptaPlanner's Config classes.
 */
@ConfigRoot(name = "optaplanner")
public class OptaPlannerBuildTimeConfig {

    public static final String DEFAULT_SOLVER_CONFIG_URL = "solverConfig.xml";

    /**
     * A classpath resource to read the solver configuration XML.
     * Defaults to {@value DEFAULT_SOLVER_CONFIG_URL}.
     * If this property isn't specified, that solverConfig.xml is optional.
     */
    @ConfigItem
    Optional<String> solverConfigXml;

    /**
     * Configuration properties that overwrite OptaPlanner's {@link SolverConfig}.
     */
    @ConfigItem
    SolverBuildTimeConfig solver;
    /**
     * Configuration properties that overwrite OptaPlanner's {@link SolverManagerConfig}.
     */
    @ConfigItem
    SolverManagerBuildTimeConfig solverManager;

}
