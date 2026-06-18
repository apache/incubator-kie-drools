/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.solver.termination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

/**
 * Abstract superclass that combines multiple {@link Termination}s.
 *
 * @see AndCompositeTermination
 * @see OrCompositeTermination
 */
public abstract class AbstractCompositeTermination<Solution_> extends AbstractTermination<Solution_> {

    protected final List<Termination<Solution_>> terminationList;

    protected AbstractCompositeTermination(List<Termination<Solution_>> terminationList) {
        this.terminationList = terminationList;
    }

    public AbstractCompositeTermination(Termination<Solution_>... terminations) {
        this(Arrays.asList(terminations));
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        for (Termination<Solution_> termination : terminationList) {
            termination.solvingStarted(solverScope);
        }
    }

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        for (Termination<Solution_> termination : terminationList) {
            termination.phaseStarted(phaseScope);
        }
    }

    @Override
    public void stepStarted(AbstractStepScope<Solution_> stepScope) {
        for (Termination<Solution_> termination : terminationList) {
            termination.stepStarted(stepScope);
        }
    }

    @Override
    public void stepEnded(AbstractStepScope<Solution_> stepScope) {
        for (Termination<Solution_> termination : terminationList) {
            termination.stepEnded(stepScope);
        }
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        for (Termination<Solution_> termination : terminationList) {
            termination.phaseEnded(phaseScope);
        }
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        for (Termination<Solution_> termination : terminationList) {
            termination.solvingEnded(solverScope);
        }
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    protected List<Termination<Solution_>> createChildThreadTerminationList(SolverScope<Solution_> solverScope,
            ChildThreadType childThreadType) {
        List<Termination<Solution_>> childThreadTerminationList = new ArrayList<>(terminationList.size());
        for (Termination<Solution_> termination : terminationList) {
            childThreadTerminationList.add(termination.createChildThreadTermination(solverScope, childThreadType));
        }
        return childThreadTerminationList;
    }

}
