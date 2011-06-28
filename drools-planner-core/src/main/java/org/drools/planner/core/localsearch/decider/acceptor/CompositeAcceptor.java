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

package org.drools.planner.core.localsearch.decider.acceptor;

import java.util.List;

import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.MoveScope;

/**
 * Combines several acceptors into one.
 * Multiplies the accept chance of its acceptors.
 * For example: combine solution and property tabu to do tabu on both.
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
    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        for (Acceptor acceptor : acceptorList) {
            acceptor.phaseStarted(localSearchSolverPhaseScope);
        }
    }

    @Override
    public void beforeDeciding(LocalSearchStepScope localSearchStepScope) {
        for (Acceptor acceptor : acceptorList) {
            acceptor.beforeDeciding(localSearchStepScope);
        }
    }

    public double calculateAcceptChance(MoveScope moveScope) {
        double acceptChance = 1.0;
        for (Acceptor acceptor : acceptorList) {
            acceptChance *= acceptor.calculateAcceptChance(moveScope);
        }
        return acceptChance;
    }

    @Override
    public void stepDecided(LocalSearchStepScope localSearchStepScope) {
        for (Acceptor acceptor : acceptorList) {
            acceptor.stepDecided(localSearchStepScope);
        }
    }

    @Override
    public void stepTaken(LocalSearchStepScope localSearchStepScope) {
        for (Acceptor acceptor : acceptorList) {
            acceptor.stepTaken(localSearchStepScope);
        }
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        for (Acceptor acceptor : acceptorList) {
            acceptor.phaseEnded(localSearchSolverPhaseScope);
        }
    }

}
