package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.optaplanner.core.impl.heuristic.selector.move.generic.list.TriangularNumbers.nthTriangle;

import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.AbstractRandomSwapIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.move.generic.GenericMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

public class RandomSubListSwapMoveSelector<Solution_> extends GenericMoveSelector<Solution_> {

    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final EntitySelector<Solution_> entitySelector;
    private final int minimumSubListSize;
    private final int maximumSubListSize;
    private final RandomSubListSelector<Solution_> leftSubListSelector;
    private final RandomSubListSelector<Solution_> rightSubListSelector;
    private final boolean selectReversingMoveToo;

    public RandomSubListSwapMoveSelector(
            ListVariableDescriptor<Solution_> listVariableDescriptor,
            EntitySelector<Solution_> entitySelector,
            EntityIndependentValueSelector<Solution_> leftValueSelector,
            EntityIndependentValueSelector<Solution_> rightValueSelector,
            int minimumSubListSize, int maximumSubListSize,
            boolean selectReversingMoveToo) {
        this.listVariableDescriptor = listVariableDescriptor;
        this.entitySelector = entitySelector;
        this.minimumSubListSize = minimumSubListSize;
        this.maximumSubListSize = maximumSubListSize;
        this.selectReversingMoveToo = selectReversingMoveToo;
        leftSubListSelector = new RandomSubListSelector<>(listVariableDescriptor, entitySelector, leftValueSelector,
                minimumSubListSize, maximumSubListSize);
        rightSubListSelector = new RandomSubListSelector<>(listVariableDescriptor, entitySelector, rightValueSelector,
                minimumSubListSize, maximumSubListSize);
        phaseLifecycleSupport.addEventListener(entitySelector);
        phaseLifecycleSupport.addEventListener(leftValueSelector);
        phaseLifecycleSupport.addEventListener(leftSubListSelector);
        phaseLifecycleSupport.addEventListener(rightSubListSelector);
    }

    @Override
    public Iterator<Move<Solution_>> iterator() {
        return new AbstractRandomSwapIterator<>(leftSubListSelector, rightSubListSelector) {
            @Override
            protected Move<Solution_> newSwapSelection(SubList leftSubSelection, SubList rightSubSelection) {
                boolean reversing = selectReversingMoveToo && workingRandom.nextBoolean();
                return new SubListSwapMove<>(listVariableDescriptor, leftSubSelection, rightSubSelection, reversing);
            }
        };
    }

    @Override
    public boolean isCountable() {
        return true;
    }

    @Override
    public boolean isNeverEnding() {
        return true;
    }

    @Override
    public long getSize() {
        long subListCount = 0;
        for (Object entity : (Iterable<?>) entitySelector::endingIterator) {
            int listSize = listVariableDescriptor.getListSize(entity);
            if (listSize < minimumSubListSize) {
                continue;
            }
            // Add moves with subLists bigger than minimum subList size.
            subListCount += nthTriangle(listSize - minimumSubListSize + 1);
            if (listSize > maximumSubListSize) {
                // Subtract moves with subLists bigger than maximum subList size.
                subListCount -= nthTriangle(listSize - maximumSubListSize);
            }
        }
        return subListCount * subListCount;
    }

    boolean isSelectReversingMoveToo() {
        return selectReversingMoveToo;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + leftSubListSelector + ", " + rightSubListSelector + ")";
    }
}
