/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.core.localsearch.termination;

import java.util.List;

import org.drools.planner.core.localsearch.LocalSearchSolver;
import org.drools.planner.core.localsearch.LocalSearchSolverScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;

/**
 * Abstract superclass for CompositeTermination classes that combine multiple Terminations.
 */
public abstract class AbstractCompositeTermination extends AbstractTermination implements Termination {

    protected List<Termination> terminationList;

    public void setTerminationList(List<Termination> terminationList) {
        this.terminationList = terminationList;
    }

    @Override
    public void setLocalSearchSolver(LocalSearchSolver localSearchSolver) {
        super.setLocalSearchSolver(localSearchSolver);
        for (Termination termination : terminationList) {
            termination.setLocalSearchSolver(localSearchSolver);
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solvingStarted(LocalSearchSolverScope localSearchSolverScope) {
        for (Termination termination : terminationList) {
            termination.solvingStarted(localSearchSolverScope);
        }
    }

    @Override
    public void beforeDeciding(LocalSearchStepScope localSearchStepScope) {
        for (Termination termination : terminationList) {
            termination.beforeDeciding(localSearchStepScope);
        }
    }

    @Override
    public void stepDecided(LocalSearchStepScope localSearchStepScope) {
        for (Termination termination : terminationList) {
            termination.stepDecided(localSearchStepScope);
        }
    }

    @Override
    public void stepTaken(LocalSearchStepScope localSearchStepScope) {
        for (Termination termination : terminationList) {
            termination.stepTaken(localSearchStepScope);
        }
    }

    @Override
    public void solvingEnded(LocalSearchSolverScope localSearchSolverScope) {
        for (Termination termination : terminationList) {
            termination.solvingEnded(localSearchSolverScope);
        }
    }

}
