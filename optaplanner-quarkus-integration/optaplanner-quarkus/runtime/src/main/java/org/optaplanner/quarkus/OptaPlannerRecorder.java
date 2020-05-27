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

import java.util.function.Supplier;

import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;

import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class OptaPlannerRecorder {

    public Supplier<SolverConfig> solverConfigSupplier(final SolverConfig solverConfig) {
        return new Supplier<SolverConfig>() {
            @Override
            public SolverConfig get() {
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
