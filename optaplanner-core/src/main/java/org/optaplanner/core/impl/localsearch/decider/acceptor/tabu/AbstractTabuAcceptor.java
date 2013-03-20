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

package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

/**
 * Abstract superclass for all Tabu Acceptors.
 * @see Acceptor
 */
public abstract class AbstractTabuAcceptor extends AbstractAcceptor {

    protected int tabuSize = -1;
    protected int fadingTabuSize = 0;
    protected boolean aspirationEnabled = true;

    protected boolean assertTabuHashCodeCorrectness = false;

    protected Map<Object, Integer> tabuToStepIndexMap;
    protected List<Object> tabuSequenceList;

    public void setTabuSize(int tabuSize) {
        this.tabuSize = tabuSize;
    }

    public void setFadingTabuSize(int fadingTabuSize) {
        this.fadingTabuSize = fadingTabuSize;
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
    public void phaseStarted(LocalSearchSolverPhaseScope phaseScope) {
        super.phaseStarted(phaseScope);
        validate();
        tabuToStepIndexMap = new HashMap<Object, Integer>(tabuSize + fadingTabuSize);
        tabuSequenceList = new LinkedList<Object>();
    }

    private void validate() {
        if (tabuSize < 0) {
            throw new IllegalArgumentException("The tabuSize (" + tabuSize
                    + ") cannot be negative.");
        }
        if (fadingTabuSize < 0) {
            throw new IllegalArgumentException("The fadingTabuSize (" + fadingTabuSize
                    + ") cannot be negative.");
        }
        if (tabuSize + fadingTabuSize == 0) {
            throw new IllegalArgumentException("The sum of tabuSize and fadingTabuSize should be at least 1.");
        }
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope phaseScope) {
        super.phaseEnded(phaseScope);
        tabuToStepIndexMap = null;
        tabuSequenceList = null;
    }

    public boolean isAccepted(LocalSearchMoveScope moveScope) {
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
                    moveScope.getStepScope().getPhaseScope().getBestScore()) > 0) {
                logger.trace("        Proposed move ({}) is tabu, but is accepted anyway due to aspiration.",
                        moveScope.getMove());
                return true;
            }
        }
        int tabuStepCount = moveScope.getStepScope().getStepIndex() - maximumTabuStepIndex; // at least 1
        if (tabuStepCount <= tabuSize) {
            logger.trace("        Proposed move ({}) is tabu and is therefore not accepted.", moveScope.getMove());
            return false;
        }
        double acceptChance = calculateFadingTabuAcceptChance(tabuStepCount - tabuSize);
        boolean accepted = moveScope.getWorkingRandom().nextDouble() < acceptChance;
        if (accepted) {
            logger.trace("        Proposed move ({}) is fading tabu with acceptChance ({}) and is accepted.",
                    moveScope.getMove(), acceptChance);
        } else {
            logger.trace("        Proposed move ({}) is fading tabu with acceptChance ({}) and is not accepted.",
                    moveScope.getMove(), acceptChance);
        }
        return accepted;
    }

    /**
     * @param fadingTabuStepCount 0 < fadingTabuStepCount <= fadingTabuSize
     * @return 0.0 < acceptChance < 1.0
     */
    protected double calculateFadingTabuAcceptChance(int fadingTabuStepCount) {
        // The + 1's are because acceptChance should not be 0.0 or 1.0
        // when (fadingTabuStepCount == 0) or (fadingTabuStepCount + 1 == fadingTabuSize)
        return ((double) (fadingTabuSize - fadingTabuStepCount)) / ((double) (fadingTabuSize + 1));
    }

    @Override
    public void stepEnded(LocalSearchStepScope stepScope) {
        int maximumTabuListSize = tabuSize + fadingTabuSize; // is at least 1
        int tabuStepIndex = stepScope.getStepIndex();
        // Remove the oldest tabu(s)
        for (Iterator<Object> it = tabuSequenceList.iterator(); it.hasNext();) {
            Object oldTabu = it.next();
            Integer oldTabuStepIndexInteger = tabuToStepIndexMap.get(oldTabu);
            if (oldTabuStepIndexInteger == null) {
                throw new IllegalStateException("HashCode violation: the hashCode of tabu (" + oldTabu
                        + ") probably changed since it was inserted in the tabu Map or Set.");
            }
            int oldTabuStepCount = tabuStepIndex - oldTabuStepIndexInteger; // at least 1
            if (oldTabuStepCount < maximumTabuListSize) {
                break;
            }
            it.remove();
            tabuToStepIndexMap.remove(oldTabu);
        }
        // Add the new tabu(s)
        Collection<? extends Object> tabus = findNewTabu(stepScope);
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

    protected abstract Collection<? extends Object> findTabu(LocalSearchMoveScope moveScope);

    protected abstract Collection<? extends Object> findNewTabu(LocalSearchStepScope stepScope);

}
