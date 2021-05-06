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

package org.optaplanner.quarkus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.quarkus.gizmo.OptaPlannerDroolsInitializer;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class OptaPlannerRecorder {

    public Supplier<SolverConfig> solverConfigSupplier(final SolverConfig solverConfig,
            Map<String, RuntimeValue<MemberAccessor>> generatedGizmoMemberAccessorMap,
            Map<String, RuntimeValue<SolutionCloner>> generatedGizmoSolutionClonerMap,
            RuntimeValue<OptaPlannerDroolsInitializer> droolsInitializer) {
        return new Supplier<SolverConfig>() {
            @Override
            public SolverConfig get() {
                Map<String, MemberAccessor> memberAccessorMap = new HashMap<>();
                Map<String, SolutionCloner> solutionClonerMap = new HashMap<>();
                generatedGizmoMemberAccessorMap
                        .forEach((className, runtimeValue) -> memberAccessorMap.put(className, runtimeValue.getValue()));
                generatedGizmoSolutionClonerMap
                        .forEach((className, runtimeValue) -> solutionClonerMap.put(className, runtimeValue.getValue()));

                solverConfig.setGizmoMemberAccessorMap(memberAccessorMap);
                solverConfig.setGizmoSolutionClonerMap(solutionClonerMap);
                droolsInitializer.getValue().setup(solverConfig.getScoreDirectorFactoryConfig());
                return solverConfig;
            }
        };
    }

    public Supplier<SolverManagerConfig> solverManagerConfig(final SolverManagerConfig solverManagerConfig) {
        return new Supplier<SolverManagerConfig>() {
            @Override
            public SolverManagerConfig get() {
                return solverManagerConfig;
            }
        };
    }

}
