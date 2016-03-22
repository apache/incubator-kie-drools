/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.solver;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.solver.SolverConfig;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class EmptySolverFactory<Solution_> extends AbstractSolverFactory<Solution_> {

    public EmptySolverFactory() {
        this(new SolverConfigContext());
    }

    /**
     * @param solverConfigContext never null
     */
    public EmptySolverFactory(SolverConfigContext solverConfigContext) {
        super(solverConfigContext);
        solverConfig = new SolverConfig();
    }

    /**
     * @param solverConfigContext never null
     * @param solverConfig never null
     */
    protected EmptySolverFactory(SolverConfigContext solverConfigContext, SolverConfig solverConfig) {
        super(solverConfigContext);
        this.solverConfig = solverConfig;
        if (solverConfig == null) {
            throw new IllegalArgumentException("The solverConfig (" + solverConfig + ") cannot be null.");
        }
    }

}
