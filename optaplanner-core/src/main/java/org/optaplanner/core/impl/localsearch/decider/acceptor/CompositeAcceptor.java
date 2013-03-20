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

package org.optaplanner.core.impl.localsearch.decider.acceptor;

import java.util.List;

import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * Combines several acceptors into one.
 * Does a logical AND over the accepted status of its acceptors.
 * For example: combine planning entity and planning value tabu to do tabu on both.
 */
public class CompositeAcceptor extends AbstractAcceptor {

    protected List<Acceptor> acceptorList;

    public void setAcceptorList(List<Acceptor> acceptorList) {
        this.acceptorList = acceptorList;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        for (Acceptor acceptor : acceptorList) {
            acceptor.solvingStarted(solverScope);
        }
    }

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope phaseScope) {
        for (Acceptor acceptor : acceptorList) {
            acceptor.phaseStarted(phaseScope);
        }
    }

    @Override
    public void stepStarted(LocalSearchStepScope stepScope) {
        for (Acceptor acceptor : acceptorList) {
            acceptor.stepStarted(stepScope);
        }
    }

    public boolean isAccepted(LocalSearchMoveScope moveScope) {
        for (Acceptor acceptor : acceptorList) {
            boolean accepted = acceptor.isAccepted(moveScope);
            if (!accepted) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void stepEnded(LocalSearchStepScope stepScope) {
        for (Acceptor acceptor : acceptorList) {
            acceptor.stepEnded(stepScope);
        }
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope phaseScope) {
        for (Acceptor acceptor : acceptorList) {
            acceptor.phaseEnded(phaseScope);
        }
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        for (Acceptor acceptor : acceptorList) {
            acceptor.solvingEnded(solverScope);
        }
    }

}
