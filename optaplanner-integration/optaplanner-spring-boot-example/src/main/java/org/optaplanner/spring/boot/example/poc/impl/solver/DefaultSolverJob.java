/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.spring.boot.example.poc.impl.solver;

import java.util.UUID;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.spring.boot.example.poc.api.solver.SolverJob;
import org.optaplanner.spring.boot.example.poc.api.solver.SolverStatus;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <ProblemId_> the ID type of a submitted problem, such as {@link Long} or {@link UUID}.
 */
public class DefaultSolverJob<Solution_, ProblemId_> implements SolverJob<Solution_, ProblemId_> {

    private final ProblemId_ problemId;
    private final Solver<Solution_> solver;

    public DefaultSolverJob(ProblemId_ problemId, Solver<Solution_> solver) {
        this.problemId = problemId;
        this.solver = solver;
    }

    @Override
    public ProblemId_ getProblemId() {
        return problemId;
    }

    @Override
    public SolverStatus getSolverStatus() {
        // TODO FIX ME
        return SolverStatus.SOLVING_ACTIVE;
    }

    // TODO Future features
//    @Override
//    public void reloadProblem(Supplier<Solution_> problemSupplier) {
//        throw new UnsupportedOperationException("The solver is still solving and reloadProblem() is not yet supported.");
//    }

    // TODO Future features
//    @Override
//    public void addProblemFactChange(ProblemFactChange<Solution_> problemFactChange) {
//        solver.addProblemFactChange(problemFactChange);
//    }

    @Override
    public void terminateEarly() {
        solver.terminateEarly();
    }

}
