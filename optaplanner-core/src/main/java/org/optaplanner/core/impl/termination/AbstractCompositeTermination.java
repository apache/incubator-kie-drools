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

package org.optaplanner.core.impl.termination;

import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * Abstract superclass for CompositeTermination classes that combine multiple Terminations.
 */
public abstract class AbstractCompositeTermination extends AbstractTermination implements Termination {

    protected List<Termination> terminationList;

    public AbstractCompositeTermination() {
    }

    public AbstractCompositeTermination(Termination... terminations) {
        terminationList = Arrays.asList(terminations);
    }

    public void setTerminationList(List<Termination> terminationList) {
        this.terminationList = terminationList;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        for (Termination termination : terminationList) {
            termination.solvingStarted(solverScope);
        }
    }

    @Override
    public void phaseStarted(AbstractSolverPhaseScope phaseScope) {
        for (Termination termination : terminationList) {
            termination.phaseStarted(phaseScope);
        }
    }

    @Override
    public void stepStarted(AbstractStepScope stepScope) {
        for (Termination termination : terminationList) {
            termination.stepStarted(stepScope);
        }
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        for (Termination termination : terminationList) {
            termination.stepEnded(stepScope);
        }
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope phaseScope) {
        for (Termination termination : terminationList) {
            termination.phaseEnded(phaseScope);
        }
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        for (Termination termination : terminationList) {
            termination.solvingEnded(solverScope);
        }
    }

}
