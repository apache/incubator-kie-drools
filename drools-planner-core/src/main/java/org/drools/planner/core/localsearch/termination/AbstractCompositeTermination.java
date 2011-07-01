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

import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.solver.AbstractStepScope;

/**
 * Abstract superclass for CompositeTermination classes that combine multiple Terminations.
 */
public abstract class AbstractCompositeTermination extends AbstractTermination implements Termination {

    protected List<Termination> terminationList;

    public void setTerminationList(List<Termination> terminationList) {
        this.terminationList = terminationList;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        for (Termination termination : terminationList) {
            termination.phaseStarted(solverPhaseScope);
        }
    }

    @Override
    public void beforeDeciding(AbstractStepScope stepScope) {
        for (Termination termination : terminationList) {
            termination.beforeDeciding(stepScope);
        }
    }

    @Override
    public void stepDecided(AbstractStepScope stepScope) {
        for (Termination termination : terminationList) {
            termination.stepDecided(stepScope);
        }
    }

    @Override
    public void stepTaken(AbstractStepScope stepScope) {
        for (Termination termination : terminationList) {
            termination.stepTaken(stepScope);
        }
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        for (Termination termination : terminationList) {
            termination.phaseEnded(solverPhaseScope);
        }
    }

}
