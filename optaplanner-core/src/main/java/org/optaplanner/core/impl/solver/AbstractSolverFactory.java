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
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.solver.SolverConfig;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class AbstractSolverFactory<Solution_> extends SolverFactory<Solution_> {

    protected final SolverConfigContext solverConfigContext;

    protected SolverConfig solverConfig = null;

    public AbstractSolverFactory(SolverConfigContext solverConfigContext) {
        this.solverConfigContext = solverConfigContext;
    }

    public SolverConfigContext getSolverConfigContext() {
        return solverConfigContext;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public SolverConfig getSolverConfig() {
        if (solverConfig == null) {
            throw new IllegalStateException("The solverConfig (" + solverConfig + ") is null," +
                    " call configure(...) first.");
        }
        return solverConfig;
    }

    @Override
    public Solver<Solution_> buildSolver() {
        if (solverConfig == null) {
            throw new IllegalStateException("The solverConfig (" + solverConfig + ") is null," +
                    " call configure(...) first.");
        }
        return solverConfig.buildSolver(solverConfigContext);
    }

    @Override
    public SolverFactory<Solution_> cloneSolverFactory() {
        if (solverConfig == null) {
            throw new IllegalStateException("The solverConfig (" + solverConfig + ") is null," +
                    " call configure(...) first.");
        }
        SolverConfig solverConfigClone = new SolverConfig(solverConfig);
        return new EmptySolverFactory<>(solverConfigContext, solverConfigClone);
    }

}
