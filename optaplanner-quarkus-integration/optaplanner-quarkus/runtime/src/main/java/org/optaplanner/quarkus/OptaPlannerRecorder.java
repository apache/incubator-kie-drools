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

package org.optaplanner.quarkus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.quarkus.config.OptaPlannerRuntimeConfig;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class OptaPlannerRecorder {

    private final RuntimeValue<OptaPlannerRuntimeConfig> optaPlannerRuntimeConfig;

    public OptaPlannerRecorder(RuntimeValue<OptaPlannerRuntimeConfig> optaPlannerRuntimeConfig) {
        this.optaPlannerRuntimeConfig = optaPlannerRuntimeConfig;
    }

    public Supplier<SolverConfig> solverConfigSupplier(final SolverConfig solverConfig,
            Map<String, RuntimeValue<MemberAccessor>> generatedGizmoMemberAccessorMap,
            Map<String, RuntimeValue<SolutionCloner>> generatedGizmoSolutionClonerMap) {
        return () -> {
            updateSolverConfigWithRuntimeProperties(solverConfig, optaPlannerRuntimeConfig.getValue());
            Map<String, MemberAccessor> memberAccessorMap = new HashMap<>();
            Map<String, SolutionCloner> solutionClonerMap = new HashMap<>();
            generatedGizmoMemberAccessorMap
                    .forEach((className, runtimeValue) -> memberAccessorMap.put(className, runtimeValue.getValue()));
            generatedGizmoSolutionClonerMap
                    .forEach((className, runtimeValue) -> solutionClonerMap.put(className, runtimeValue.getValue()));

            solverConfig.setGizmoMemberAccessorMap(memberAccessorMap);
            solverConfig.setGizmoSolutionClonerMap(solutionClonerMap);
            return solverConfig;
        };
    }

    public Supplier<SolverManagerConfig> solverManagerConfig(final SolverManagerConfig solverManagerConfig) {
        return () -> {
            updateSolverManagerConfigWithRuntimeProperties(solverManagerConfig, optaPlannerRuntimeConfig.getValue());
            return solverManagerConfig;
        };
    }

    private static void updateSolverConfigWithRuntimeProperties(SolverConfig solverConfig,
            OptaPlannerRuntimeConfig optaPlannerRunTimeConfig) {
        TerminationConfig terminationConfig = solverConfig.getTerminationConfig();
        if (terminationConfig == null) {
            terminationConfig = new TerminationConfig();
            solverConfig.setTerminationConfig(terminationConfig);
        }
        optaPlannerRunTimeConfig.solver().termination().spentLimit().ifPresent(terminationConfig::setSpentLimit);
        optaPlannerRunTimeConfig.solver().termination().unimprovedSpentLimit()
                .ifPresent(terminationConfig::setUnimprovedSpentLimit);
        optaPlannerRunTimeConfig.solver().termination().bestScoreLimit().ifPresent(terminationConfig::setBestScoreLimit);
        optaPlannerRunTimeConfig.solver().moveThreadCount().ifPresent(solverConfig::setMoveThreadCount);
    }

    private static void updateSolverManagerConfigWithRuntimeProperties(SolverManagerConfig solverManagerConfig,
            OptaPlannerRuntimeConfig optaPlannerRunTimeConfig) {
        optaPlannerRunTimeConfig.solverManager().parallelSolverCount().ifPresent(solverManagerConfig::setParallelSolverCount);
    }

}
