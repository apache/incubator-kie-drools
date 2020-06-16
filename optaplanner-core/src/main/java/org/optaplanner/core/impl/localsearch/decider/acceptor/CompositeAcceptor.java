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

package org.optaplanner.core.impl.localsearch.decider.acceptor;

import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * Combines several acceptors into one.
 * Does a logical AND over the accepted status of its acceptors.
 * For example: combine planning entity and planning value tabu to do tabu on both.
 */
public class CompositeAcceptor extends AbstractAcceptor {

    protected final List<Acceptor> acceptorList;

    public CompositeAcceptor(List<Acceptor> acceptorList) {
        this.acceptorList = acceptorList;
    }

    public CompositeAcceptor(Acceptor... acceptors) {
        this(Arrays.asList(acceptors));
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solvingStarted(SolverScope solverScope) {
        for (Acceptor acceptor : acceptorList) {
            acceptor.solvingStarted(solverScope);
        }
    }

    @Override
    public void phaseStarted(LocalSearchPhaseScope phaseScope) {
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

    @Override
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
    public void phaseEnded(LocalSearchPhaseScope phaseScope) {
        for (Acceptor acceptor : acceptorList) {
            acceptor.phaseEnded(phaseScope);
        }
    }

    @Override
    public void solvingEnded(SolverScope solverScope) {
        for (Acceptor acceptor : acceptorList) {
            acceptor.solvingEnded(solverScope);
        }
    }

}
