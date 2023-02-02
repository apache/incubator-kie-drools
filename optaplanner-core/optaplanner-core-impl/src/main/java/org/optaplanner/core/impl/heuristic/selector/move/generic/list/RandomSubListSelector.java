package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Iterator;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonListInverseVariableDemand;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelector;
import org.optaplanner.core.impl.heuristic.selector.IterableSelector;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class RandomSubListSelector<Solution_> extends AbstractSelector<Solution_>
        implements IterableSelector<Solution_, SubList> {

    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final EntitySelector<Solution_> entitySelector;
    private final EntityIndependentValueSelector<Solution_> valueSelector;
    private final int minimumSubListSize;
    private final int maximumSubListSize;

    private TriangleElementFactory triangleElementFactory;
    private SingletonInverseVariableSupply inverseVariableSupply;

    public RandomSubListSelector(
            ListVariableDescriptor<Solution_> listVariableDescriptor,
            EntitySelector<Solution_> entitySelector,
            EntityIndependentValueSelector<Solution_> valueSelector,
            int minimumSubListSize, int maximumSubListSize) {
        this.listVariableDescriptor = listVariableDescriptor;
        this.entitySelector = entitySelector;
        this.valueSelector = valueSelector;
        if (minimumSubListSize < 1) {
            throw new IllegalArgumentException(
                    "The minimumSubListSize (" + minimumSubListSize + ") must be greater than 0.");
        }
        if (minimumSubListSize > maximumSubListSize) {
            throw new IllegalArgumentException("The minimumSubListSize (" + minimumSubListSize
                    + ") must be less than or equal to the maximumSubListSize (" + maximumSubListSize + ").");
        }
        this.minimumSubListSize = minimumSubListSize;
        this.maximumSubListSize = maximumSubListSize;

        phaseLifecycleSupport.addEventListener(entitySelector);
        phaseLifecycleSupport.addEventListener(valueSelector);
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        super.solvingStarted(solverScope);
        triangleElementFactory = new TriangleElementFactory(minimumSubListSize, maximumSubListSize, workingRandom);
        inverseVariableSupply = solverScope.getScoreDirector().getSupplyManager()
                .demand(new SingletonListInverseVariableDemand<>(listVariableDescriptor));
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        inverseVariableSupply = null;
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
        for (Object entity : ((Iterable<Object>) entitySelector::endingIterator)) {
            int listSize = listVariableDescriptor.getListSize(entity);
            // Add subLists bigger than minimum subList size.
            if (listSize >= minimumSubListSize) {
                subListCount += TriangularNumbers.nthTriangle(listSize - minimumSubListSize + 1);
                // Subtract moves with subLists bigger than maximum subList size.
                if (listSize > maximumSubListSize) {
                    subListCount -= TriangularNumbers.nthTriangle(listSize - maximumSubListSize);
                }
            }
        }
        return subListCount;
    }

    @Override
    public Iterator<SubList> iterator() {
        // TODO make this incremental https://issues.redhat.com/browse/PLANNER-2507
        int biggestListSize = 0;
        for (Object entity : ((Iterable<Object>) entitySelector::endingIterator)) {
            biggestListSize = Math.max(biggestListSize, listVariableDescriptor.getListSize(entity));
        }
        if (biggestListSize < minimumSubListSize) {
            return new UpcomingSelectionIterator<>() {
                @Override
                protected SubList createUpcomingSelection() {
                    return noUpcomingSelection();
                }
            };
        }
        return new RandomSubListIterator(valueSelector.iterator());
    }

    private final class RandomSubListIterator extends UpcomingSelectionIterator<SubList> {

        private final Iterator<Object> valueIterator;

        private RandomSubListIterator(Iterator<Object> valueIterator) {
            this.valueIterator = valueIterator;
        }

        @Override
        protected SubList createUpcomingSelection() {
            Object sourceEntity = null;
            int listSize = 0;

            // TODO What if MIN is 500? We could burn thousands of cycles before we hit a listSize >= 500!
            while (listSize < minimumSubListSize) {
                if (!valueIterator.hasNext()) {
                    throw new IllegalStateException("The valueIterator (" + valueIterator + ") should never end.");
                }
                // Using valueSelector instead of entitySelector is more fair because entities with bigger list variables
                // will be selected more often.
                sourceEntity = inverseVariableSupply.getInverseSingleton(valueIterator.next());
                listSize = listVariableDescriptor.getListSize(sourceEntity);
            }

            TriangleElementFactory.TriangleElement triangleElement = triangleElementFactory.nextElement(listSize);
            int subListLength = listSize - triangleElement.getLevel() + 1;
            int sourceIndex = triangleElement.getIndexOnLevel() - 1;

            return new SubList(sourceEntity, sourceIndex, subListLength);
        }
    }

    public int getMinimumSubListSize() {
        return minimumSubListSize;
    }

    public int getMaximumSubListSize() {
        return maximumSubListSize;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + valueSelector + ")";
    }
}
