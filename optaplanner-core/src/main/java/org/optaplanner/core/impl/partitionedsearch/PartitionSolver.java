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

package org.optaplanner.core.impl.partitionedsearch;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.solver.AbstractSolver;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.Termination;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class PartitionSolver<Solution_> extends AbstractSolver<Solution_> {

    protected final SolverScope<Solution_> solverScope;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public PartitionSolver(BestSolutionRecaller<Solution_> bestSolutionRecaller, Termination termination,
            List<Phase<Solution_>> phaseList, SolverScope<Solution_> solverScope) {
        super(bestSolutionRecaller, termination, phaseList);
        this.solverScope = solverScope;
    }

    // ************************************************************************
    // Complex getters
    // ************************************************************************

    @Override
    public boolean isSolving() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean terminateEarly() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTerminateEarly() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addProblemFactChange(ProblemFactChange<Solution_> problemFactChange) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addProblemFactChanges(List<ProblemFactChange<Solution_>> problemFactChanges) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEveryProblemFactChangeProcessed() {
        throw new UnsupportedOperationException();
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public Solution_ solve(Solution_ problem) {
        solverScope.initializeYielding();
        try {
            solverScope.setBestSolution(problem);
            solvingStarted(solverScope);
            runPhases(solverScope);
            solvingEnded(solverScope);
            return solverScope.getBestSolution();
        } finally {
            solverScope.destroyYielding();
        }
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        solverScope.setWorkingSolutionFromBestSolution();
        super.solvingStarted(solverScope);
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        solverScope.getScoreDirector().close();
        // TODO log?
    }

    public long getScoreCalculationCount() {
        return solverScope.getScoreCalculationCount();
    }

}
