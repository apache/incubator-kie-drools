package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableDemand;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonListInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelector;
import org.optaplanner.core.impl.heuristic.selector.IterableSelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.solver.random.RandomUtils;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * Selects destinations for list variable change moves. The destination specifies a future position in a list variable,
 * expressed as an {@link ElementRef}, where a moved element or subList can be inserted.
 * <p>
 * Destination completeness is achieved by using both entity and value child selectors.
 * When an entity <em>A</em> is selected, the destination becomes <em>A[0]</em>.
 * When a value <em>x</em> is selected, its current position <em>A[i]</em> is determined using inverse and index supplies and
 * the destination becomes <em>A[i + 1]</em>.
 * <p>
 * Fairness in random selection is achieved by first deciding between entity and value selector with a probability that is
 * proportional to the entity/value ratio. The child entity and value selectors are assumed to be fair.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class ElementDestinationSelector<Solution_> extends AbstractSelector<Solution_>
        implements IterableSelector<Solution_, ElementRef> {

    private final ListVariableDescriptor<Solution_> listVariableDescriptor;
    private final EntitySelector<Solution_> entitySelector;
    private final EntityIndependentValueSelector<Solution_> valueSelector;
    private final boolean randomSelection;

    private SingletonInverseVariableSupply inverseVariableSupply;
    private IndexVariableSupply indexVariableSupply;

    public ElementDestinationSelector(
            ListVariableDescriptor<Solution_> listVariableDescriptor,
            EntitySelector<Solution_> entitySelector,
            EntityIndependentValueSelector<Solution_> valueSelector,
            boolean randomSelection) {
        this.listVariableDescriptor = listVariableDescriptor;
        this.entitySelector = entitySelector;
        this.valueSelector = valueSelector;
        this.randomSelection = randomSelection;

        phaseLifecycleSupport.addEventListener(entitySelector);
        phaseLifecycleSupport.addEventListener(valueSelector);
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        super.solvingStarted(solverScope);
        SupplyManager supplyManager = solverScope.getScoreDirector().getSupplyManager();
        inverseVariableSupply = supplyManager.demand(new SingletonListInverseVariableDemand<>(listVariableDescriptor));
        indexVariableSupply = supplyManager.demand(new IndexVariableDemand<>(listVariableDescriptor));
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        super.solvingEnded(solverScope);
        inverseVariableSupply = null;
        indexVariableSupply = null;
    }

    @Override
    public long getSize() {
        if (entitySelector.getSize() == 0) {
            return 0;
        }
        return entitySelector.getSize() + valueSelector.getSize();
    }

    @Override
    public Iterator<ElementRef> iterator() {
        if (randomSelection) {
            long totalSize = Math.addExact(entitySelector.getSize(), valueSelector.getSize());
            Iterator<Object> entityIterator = entitySelector.iterator();
            Iterator<Object> valueIterator = valueSelector.iterator();

            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    // The valueSelector's hasNext() is insignificant. The next random destination exists if and only if
                    // there is a next entity.
                    return entityIterator.hasNext();
                }

                @Override
                public ElementRef next() {
                    long size = entitySelector.getSize();
                    if (RandomUtils.nextLong(workingRandom, totalSize) < size) {
                        return ElementRef.of(entityIterator.next(), 0);
                    }
                    Object value = valueIterator.next();
                    return ElementRef.of(
                            inverseVariableSupply.getInverseSingleton(value),
                            indexVariableSupply.getIndex(value) + 1);
                }
            };
        } else {
            if (entitySelector.getSize() == 0) {
                return Collections.emptyIterator();
            }
            return Stream.concat(
                    StreamSupport.stream(entitySelector.spliterator(), false)
                            .map(entity -> ElementRef.of(entity, 0)),
                    StreamSupport.stream(valueSelector.spliterator(), false)
                            .map(value -> ElementRef.of(
                                    inverseVariableSupply.getInverseSingleton(value),
                                    indexVariableSupply.getIndex(value) + 1)))
                    .iterator();
        }
    }

    @Override
    public boolean isCountable() {
        return entitySelector.isCountable() && valueSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return randomSelection || entitySelector.isNeverEnding() || valueSelector.isNeverEnding();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelector + ", " + valueSelector + ")";
    }
}
