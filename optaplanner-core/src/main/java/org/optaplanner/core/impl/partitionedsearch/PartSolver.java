/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.solver.AbstractSolver;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.solver.termination.Termination;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class PartSolver<Solution_> extends AbstractSolver<Solution_> {

    protected final Termination termination;
    protected final BestSolutionRecaller<Solution_> bestSolutionRecaller;
    protected final List<Phase<Solution_>> phaseList;

    protected final DefaultSolverScope<Solution_> solverScope;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public PartSolver(Termination termination, BestSolutionRecaller<Solution_> bestSolutionRecaller,
            List<Phase<Solution_>> phaseList, DefaultSolverScope<Solution_> solverScope) {
        this.termination = termination;
        this.bestSolutionRecaller = bestSolutionRecaller;
        bestSolutionRecaller.setSolverEventSupport(solverEventSupport);
        this.phaseList = phaseList;
        for (Phase<Solution_> phase : phaseList) {
            phase.setSolverPhaseLifecycleSupport(phaseLifecycleSupport);
        }
        this.solverScope = solverScope;
    }

    @Override
    public Solution_ solve(Solution_ part) {
        solverScope.setBestSolution(part);
        solverScope.setWorkingSolutionFromBestSolution();
        solvingStarted(solverScope);
        for (Phase<Solution_> phase : phaseList) {
            phase.solve(solverScope);
        }
        solvingEnded(solverScope);
        solverScope.endingNow();
        solverScope.getScoreDirector().dispose();
        // TODO log?
        return solverScope.getBestSolution();
    }

    public void solvingStarted(DefaultSolverScope<Solution_> solverScope) {
        bestSolutionRecaller.solvingStarted(solverScope);
        phaseLifecycleSupport.fireSolvingStarted(solverScope);
        for (Phase<Solution_> phase : phaseList) {
            phase.solvingStarted(solverScope);
        }
    }

    public void solvingEnded(DefaultSolverScope<Solution_> solverScope) {
        for (Phase<Solution_> phase : phaseList) {
            phase.solvingEnded(solverScope);
        }
        phaseLifecycleSupport.fireSolvingEnded(solverScope);
        bestSolutionRecaller.solvingEnded(solverScope);
    }



    // TODO remove these

    @Override
    public Solution_ getBestSolution() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Score getBestScore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getTimeMillisSpent() {
        throw new UnsupportedOperationException();
    }

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
    public boolean isEveryProblemFactChangeProcessed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScoreDirectorFactory<Solution_> getScoreDirectorFactory() {
        throw new UnsupportedOperationException();
    }
}
