/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.solver.termination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * Abstract superclass that combines multiple {@link Termination}s.
 * @see AndCompositeTermination
 * @see OrCompositeTermination
 */
public abstract class AbstractCompositeTermination extends AbstractTermination {

    protected final List<Termination> terminationList;

    protected AbstractCompositeTermination(List<Termination> terminationList) {
        this.terminationList = terminationList;
    }

    public AbstractCompositeTermination(Termination... terminations) {
        this(Arrays.asList(terminations));
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        for (Termination termination : terminationList) {
            termination.solvingStarted(solverScope);
        }
    }

    @Override
    public void phaseStarted(AbstractPhaseScope phaseScope) {
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
    public void phaseEnded(AbstractPhaseScope phaseScope) {
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

    // ************************************************************************
    // Other methods
    // ************************************************************************

    protected List<Termination> createChildThreadTerminationList(DefaultSolverScope solverScope, ChildThreadType childThreadType) {
        List<Termination> childThreadTerminationList = new ArrayList<>(terminationList.size());
        for (Termination termination : terminationList) {
            childThreadTerminationList.add(termination.createChildThreadTermination(solverScope, childThreadType));
        }
        return childThreadTerminationList;
    }

}
