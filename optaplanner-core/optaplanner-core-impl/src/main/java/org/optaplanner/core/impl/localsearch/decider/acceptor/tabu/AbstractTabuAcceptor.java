package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.optaplanner.core.impl.localsearch.decider.acceptor.AbstractAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size.TabuSizeStrategy;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;

/**
 * Abstract superclass for all Tabu Acceptors.
 *
 * @see Acceptor
 */
public abstract class AbstractTabuAcceptor<Solution_> extends AbstractAcceptor<Solution_> {

    protected final String logIndentation;

    protected TabuSizeStrategy<Solution_> tabuSizeStrategy = null;
    protected TabuSizeStrategy<Solution_> fadingTabuSizeStrategy = null;
    protected boolean aspirationEnabled = true;

    protected boolean assertTabuHashCodeCorrectness = false;

    protected Map<Object, Integer> tabuToStepIndexMap;
    protected Deque<Object> tabuSequenceDeque;

    protected int workingTabuSize = -1;
    protected int workingFadingTabuSize = -1;

    public AbstractTabuAcceptor(String logIndentation) {
        this.logIndentation = logIndentation;
    }

    public void setTabuSizeStrategy(TabuSizeStrategy<Solution_> tabuSizeStrategy) {
        this.tabuSizeStrategy = tabuSizeStrategy;
    }

    public void setFadingTabuSizeStrategy(TabuSizeStrategy<Solution_> fadingTabuSizeStrategy) {
        this.fadingTabuSizeStrategy = fadingTabuSizeStrategy;
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
    public void phaseStarted(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseStarted(phaseScope);
        LocalSearchStepScope<Solution_> lastCompletedStepScope = phaseScope.getLastCompletedStepScope();
        // Tabu sizes do not change during stepStarted(), because they must be in sync with the tabuSequenceList.size()
        workingTabuSize = tabuSizeStrategy == null ? 0 : tabuSizeStrategy.determineTabuSize(lastCompletedStepScope);
        workingFadingTabuSize = fadingTabuSizeStrategy == null ? 0
                : fadingTabuSizeStrategy.determineTabuSize(lastCompletedStepScope);
        int totalTabuListSize = workingTabuSize + workingFadingTabuSize; // is at least 1
        tabuToStepIndexMap = new HashMap<>(totalTabuListSize);
        tabuSequenceDeque = new ArrayDeque<>();
    }

    @Override
    public void phaseEnded(LocalSearchPhaseScope<Solution_> phaseScope) {
        super.phaseEnded(phaseScope);
        tabuToStepIndexMap = null;
        tabuSequenceDeque = null;
        workingTabuSize = -1;
        workingFadingTabuSize = -1;
    }

    @Override
    public void stepEnded(LocalSearchStepScope<Solution_> stepScope) {
        super.stepEnded(stepScope);
        // Tabu sizes do not change during stepStarted(), because they must be in sync with the tabuSequenceList.size()
        workingTabuSize = tabuSizeStrategy == null ? 0 : tabuSizeStrategy.determineTabuSize(stepScope);
        workingFadingTabuSize = fadingTabuSizeStrategy == null ? 0 : fadingTabuSizeStrategy.determineTabuSize(stepScope);
        adjustTabuList(stepScope.getStepIndex(), findNewTabu(stepScope));
    }

    protected void adjustTabuList(int tabuStepIndex, Collection<? extends Object> tabus) {
        int totalTabuListSize = workingTabuSize + workingFadingTabuSize; // is at least 1
        // Remove the oldest tabu(s)
        for (Iterator<Object> it = tabuSequenceDeque.iterator(); it.hasNext();) {
            Object oldTabu = it.next();
            Integer oldTabuStepIndexInteger = tabuToStepIndexMap.get(oldTabu);
            if (oldTabuStepIndexInteger == null) {
                throw new IllegalStateException("HashCode stability violation: the hashCode() of tabu ("
                        + oldTabu + ") of class (" + oldTabu.getClass()
                        + ") changed during planning, since it was inserted in the tabu Map or Set.");
            }
            int oldTabuStepCount = tabuStepIndex - oldTabuStepIndexInteger; // at least 1
            if (oldTabuStepCount < totalTabuListSize) {
                break;
            }
            it.remove();
            tabuToStepIndexMap.remove(oldTabu);
        }
        // Add the new tabu(s)
        for (Object tabu : tabus) {
            // Push tabu to the end of the line
            if (tabuToStepIndexMap.containsKey(tabu)) {
                tabuToStepIndexMap.remove(tabu);
                tabuSequenceDeque.remove(tabu);
            }
            tabuToStepIndexMap.put(tabu, tabuStepIndex);
            tabuSequenceDeque.add(tabu);
        }
    }

    @Override
    public boolean isAccepted(LocalSearchMoveScope<Solution_> moveScope) {
        int maximumTabuStepIndex = locateMaximumTabStepIndex(moveScope);
        if (maximumTabuStepIndex < 0) {
            // The move isn't tabu at all
            return true;
        }
        if (aspirationEnabled) {
            // Natural comparison because shifting penalties don't apply
            if (moveScope.getScore().compareTo(
                    moveScope.getStepScope().getPhaseScope().getBestScore()) > 0) {
                logger.trace("{}        Proposed move ({}) is tabu, but is accepted anyway due to aspiration.",
                        logIndentation,
                        moveScope.getMove());
                return true;
            }
        }
        int tabuStepCount = moveScope.getStepScope().getStepIndex() - maximumTabuStepIndex; // at least 1
        if (tabuStepCount <= workingTabuSize) {
            logger.trace("{}        Proposed move ({}) is tabu and is therefore not accepted.",
                    logIndentation, moveScope.getMove());
            return false;
        }
        double acceptChance = calculateFadingTabuAcceptChance(tabuStepCount - workingTabuSize);
        boolean accepted = moveScope.getWorkingRandom().nextDouble() < acceptChance;
        if (accepted) {
            logger.trace("{}        Proposed move ({}) is fading tabu with acceptChance ({}) and is accepted.",
                    logIndentation,
                    moveScope.getMove(), acceptChance);
        } else {
            logger.trace("{}        Proposed move ({}) is fading tabu with acceptChance ({}) and is not accepted.",
                    logIndentation,
                    moveScope.getMove(), acceptChance);
        }
        return accepted;
    }

    private int locateMaximumTabStepIndex(LocalSearchMoveScope<Solution_> moveScope) {
        Collection<? extends Object> checkingTabus = findTabu(moveScope);
        int maximumTabuStepIndex = -1;
        for (Object checkingTabu : checkingTabus) {
            Integer tabuStepIndexInteger = tabuToStepIndexMap.get(checkingTabu);
            if (tabuStepIndexInteger != null) {
                maximumTabuStepIndex = Math.max(tabuStepIndexInteger, maximumTabuStepIndex);
            }
            if (assertTabuHashCodeCorrectness) {
                for (Object tabu : tabuSequenceDeque) {
                    // tabu and checkingTabu can be null with a nullable planning variable
                    if (tabu != null && tabu.equals(checkingTabu)) {
                        if (tabu.hashCode() != checkingTabu.hashCode()) {
                            throw new IllegalStateException("HashCode/equals contract violation: tabu (" + tabu
                                    + ") of class (" + tabu.getClass()
                                    + ") and checkingTabu (" + checkingTabu
                                    + ") are equals() but have a different hashCode().");
                        }
                        if (tabuStepIndexInteger == null) {
                            throw new IllegalStateException("HashCode stability violation: the hashCode() of tabu ("
                                    + tabu + ") of class (" + tabu.getClass()
                                    + ") changed during planning, since it was inserted in the tabu Map or Set.");
                        }
                    }
                }
            }
        }
        return maximumTabuStepIndex;
    }

    /**
     * @param fadingTabuStepCount {@code 0 < fadingTabuStepCount <= fadingTabuSize}
     * @return {@code 0.0 < acceptChance < 1.0}
     */
    protected double calculateFadingTabuAcceptChance(int fadingTabuStepCount) {
        // The + 1's are because acceptChance should not be 0.0 or 1.0
        // when (fadingTabuStepCount == 0) or (fadingTabuStepCount + 1 == workingFadingTabuSize)
        return (workingFadingTabuSize - fadingTabuStepCount) / ((double) (workingFadingTabuSize + 1));
    }

    protected abstract Collection<? extends Object> findTabu(LocalSearchMoveScope<Solution_> moveScope);

    protected abstract Collection<? extends Object> findNewTabu(LocalSearchStepScope<Solution_> stepScope);

}
