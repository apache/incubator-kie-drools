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

package org.drools.planner.core.localsearch.decider.acceptor.tabu;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.MoveScope;
import org.drools.planner.core.localsearch.decider.acceptor.AbstractAcceptor;
import org.drools.planner.core.localsearch.decider.acceptor.Acceptor;

/**
 * Abstract superclass for all Tabu Acceptors.
 * @see Acceptor
 */
public abstract class AbstractTabuAcceptor extends AbstractAcceptor {

    protected int tabuSize = -1;
    protected int partialTabuSize = 0;
    protected boolean aspirationEnabled = true;

    protected boolean assertTabuHashCodeCorrectness = false;

    protected Map<Object, Integer> tabuToStepIndexMap;
    protected List<Object> tabuSequenceList;

    public int getTabuSize() {
        return tabuSize;
    }

    public void setTabuSize(int tabuSize) {
        this.tabuSize = tabuSize;
    }

    public void setPartialTabuSize(int partialTabuSize) {
        this.partialTabuSize = partialTabuSize;
    }

    public void setAspirationEnabled(boolean aspirationEnabled) {
        this.aspirationEnabled = aspirationEnabled;
    }

    public void setAssertTabuHashCodeCorrectness(boolean assertTabuHashCodeCorrectness) {
        this.assertTabuHashCodeCorrectness = assertTabuHashCodeCorrectness;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        validateConfiguration();
        tabuToStepIndexMap = new HashMap<Object, Integer>(tabuSize + partialTabuSize);
        tabuSequenceList = new LinkedList<Object>();
    }

    private void validateConfiguration() {
        if (tabuSize < 0) {
            throw new IllegalArgumentException("The tabuSize (" + tabuSize
                    + ") cannot be negative.");
        }
        if (partialTabuSize < 0) {
            throw new IllegalArgumentException("The partialTabuSize (" + partialTabuSize
                    + ") cannot be negative.");
        }
        if (tabuSize + partialTabuSize == 0) {
            throw new IllegalArgumentException("The sum of tabuSize and partialTabuSize should be at least 1.");
        }
    }

    public boolean isAccepted(MoveScope moveScope) {
        Collection<? extends Object> checkingTabus = findTabu(moveScope);
        int maximumTabuStepIndex = -1;
        for (Object checkingTabu : checkingTabus) {
            Integer tabuStepIndexInteger = tabuToStepIndexMap.get(checkingTabu);
            if (tabuStepIndexInteger != null) {
                maximumTabuStepIndex = Math.max(tabuStepIndexInteger, maximumTabuStepIndex);
            }
            if (assertTabuHashCodeCorrectness) {
                for (Object tabu : tabuSequenceList) {
                    if (tabu.equals(checkingTabu)) {
                        if (tabu.hashCode() != checkingTabu.hashCode()) {
                            throw new IllegalStateException("HashCode violation: tabu (" + tabu + ") and checkingTabu ("
                                    + checkingTabu + ") are equal but have a different hashCode.");
                        }
                        if (tabuStepIndexInteger == null) {
                            throw new IllegalStateException("HashCode violation: the hashCode of tabu (" + tabu
                                    + ") probably changed since it was inserted in the tabu Map or Set.");
                        }
                    }
                }
            }
        }
        if (maximumTabuStepIndex < 0) {
            // The move isn't tabu at all
            return true;
        }
        if (aspirationEnabled) {
            // Doesn't use the deciderScoreComparator because shifting penalties don't apply
            if (moveScope.getScore().compareTo(
                    moveScope.getLocalSearchStepScope().getLocalSearchSolverPhaseScope().getBestScore()) > 0) {
                logger.trace("        Proposed move ({}) is tabu, but is accepted anyway due to aspiration.",
                        moveScope.getMove());
                return true;
            }
        }
        int tabuStepCount = moveScope.getLocalSearchStepScope().getStepIndex() - maximumTabuStepIndex; // at least 1
        if (tabuStepCount <= tabuSize) {
            logger.trace("        Proposed move ({}) is tabu and is therefore not accepted.", moveScope.getMove());
            return false;
        }
        double acceptChance = calculatePartialTabuAcceptChance(tabuStepCount - tabuSize);
        boolean accepted = moveScope.getWorkingRandom().nextDouble() < acceptChance;
        if (accepted) {
            logger.trace("        Proposed move ({}) is partially tabu with acceptChance ({}) and is accepted.",
                    moveScope.getMove(), acceptChance);
        } else {
            logger.trace("        Proposed move ({}) is partially tabu with acceptChance ({}) and is not accepted.",
                    moveScope.getMove(), acceptChance);
        }
        return accepted;
    }

    /**
     * @param partialTabuStepCount 0 < partialTabuStepCount <= partialTabuSize
     * @return 0.0 < acceptChance < 1.0
     */
    protected double calculatePartialTabuAcceptChance(int partialTabuStepCount) {
        // The + 1's are because acceptChance should not be 0.0 or 1.0
        // when (partialTabuStepCount == 0) or (partialTabuStepCount + 1 == partialTabuSize)
        return ((double) (partialTabuSize - partialTabuStepCount)) / ((double) (partialTabuSize + 1));
    }

    @Override
    public void stepTaken(LocalSearchStepScope localSearchStepScope) {
        int maximumTabuListSize = tabuSize + partialTabuSize; // is at least 1
        int tabuStepIndex = localSearchStepScope.getStepIndex();
        // Remove the oldest tabu(s)
        for (Iterator<Object> it = tabuSequenceList.iterator(); it.hasNext();) {
            Object oldTabu = it.next();
            Integer oldTabuStepIndexInteger = tabuToStepIndexMap.get(oldTabu);
            int oldTabuStepCount = tabuStepIndex - oldTabuStepIndexInteger; // at least 1
            if (oldTabuStepCount < maximumTabuListSize) {
                break;
            }
            it.remove();
            tabuToStepIndexMap.remove(oldTabu);
        }
        // Add the new tabu(s)
        Collection<? extends Object> tabus = findNewTabu(localSearchStepScope);
        for (Object tabu : tabus) {
            // Push tabu to the end of the line
            if (tabuToStepIndexMap.containsKey(tabu)) {
                tabuToStepIndexMap.remove(tabu);
                tabuSequenceList.remove(tabu);
            }
            tabuToStepIndexMap.put(tabu, tabuStepIndex);
            tabuSequenceList.add(tabu);
        }
    }

    protected abstract Collection<? extends Object> findTabu(MoveScope moveScope);

    protected abstract Collection<? extends Object> findNewTabu(LocalSearchStepScope localSearchStepScope);

}
