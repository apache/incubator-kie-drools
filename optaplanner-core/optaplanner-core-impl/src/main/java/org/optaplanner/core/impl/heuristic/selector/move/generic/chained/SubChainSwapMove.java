package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChain;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * This {@link Move} is not cacheable.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class SubChainSwapMove<Solution_> extends AbstractMove<Solution_> {

    protected final GenuineVariableDescriptor<Solution_> variableDescriptor;

    protected final SubChain leftSubChain;
    protected final Object leftTrailingLastEntity;
    protected final SubChain rightSubChain;
    protected final Object rightTrailingLastEntity;

    public SubChainSwapMove(GenuineVariableDescriptor<Solution_> variableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply,
            SubChain leftSubChain, SubChain rightSubChain) {
        this.variableDescriptor = variableDescriptor;
        this.leftSubChain = leftSubChain;
        leftTrailingLastEntity = inverseVariableSupply.getInverseSingleton(leftSubChain.getLastEntity());
        this.rightSubChain = rightSubChain;
        rightTrailingLastEntity = inverseVariableSupply.getInverseSingleton(rightSubChain.getLastEntity());
    }

    public SubChainSwapMove(GenuineVariableDescriptor<Solution_> variableDescriptor,
            SubChain leftSubChain, Object leftTrailingLastEntity, SubChain rightSubChain,
            Object rightTrailingLastEntity) {
        this.variableDescriptor = variableDescriptor;
        this.leftSubChain = leftSubChain;
        this.rightSubChain = rightSubChain;
        this.leftTrailingLastEntity = leftTrailingLastEntity;
        this.rightTrailingLastEntity = rightTrailingLastEntity;
    }

    public String getVariableName() {
        return variableDescriptor.getVariableName();
    }

    public SubChain getLeftSubChain() {
        return leftSubChain;
    }

    public SubChain getRightSubChain() {
        return rightSubChain;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        return !containsAnyOf(rightSubChain, leftSubChain);
    }

    static boolean containsAnyOf(SubChain rightSubChain, SubChain leftSubChain) {
        int leftSubChainSize = leftSubChain.getSize();
        if (leftSubChainSize == 0) {
            return false;
        } else if (leftSubChainSize == 1) { // No optimization possible.
            return rightSubChain.getEntityList().contains(leftSubChain.getFirstEntity());
        }
        /*
         * In order to find an entity in another subchain, we need to do contains() on a List.
         * List.contains() is O(n), the performance gets worse with increasing size.
         * Subchains here can easily have hundreds, thousands of elements.
         * As Set.contains() is O(1), independent of set size, copying the list outperforms the lookup by a lot.
         * Therefore this code converts the List lookup to HashSet lookup, in situations with repeat lookup.
         */
        Set<Object> rightSubChainEntityFastLookupSet = new HashSet<>(rightSubChain.getEntityList());
        for (Object leftEntity : leftSubChain.getEntityList()) {
            if (rightSubChainEntityFastLookupSet.contains(leftEntity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public SubChainSwapMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        return new SubChainSwapMove<>(variableDescriptor,
                rightSubChain, leftTrailingLastEntity,
                leftSubChain, rightTrailingLastEntity);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        Object leftFirstEntity = leftSubChain.getFirstEntity();
        Object leftFirstValue = variableDescriptor.getValue(leftFirstEntity);
        Object leftLastEntity = leftSubChain.getLastEntity();
        Object rightFirstEntity = rightSubChain.getFirstEntity();
        Object rightFirstValue = variableDescriptor.getValue(rightFirstEntity);
        Object rightLastEntity = rightSubChain.getLastEntity();
        // Change the entities
        InnerScoreDirector<Solution_, ?> innerScoreDirector = (InnerScoreDirector<Solution_, ?>) scoreDirector;
        if (leftLastEntity != rightFirstValue) {
            innerScoreDirector.changeVariableFacade(variableDescriptor, leftFirstEntity, rightFirstValue);
        }
        if (rightLastEntity != leftFirstValue) {
            innerScoreDirector.changeVariableFacade(variableDescriptor, rightFirstEntity, leftFirstValue);
        }
        // Reroute the new chains
        if (leftTrailingLastEntity != null) {
            if (leftTrailingLastEntity != rightFirstEntity) {
                innerScoreDirector.changeVariableFacade(variableDescriptor, leftTrailingLastEntity, rightLastEntity);
            } else {
                innerScoreDirector.changeVariableFacade(variableDescriptor, leftFirstEntity, rightLastEntity);
            }
        }
        if (rightTrailingLastEntity != null) {
            if (rightTrailingLastEntity != leftFirstEntity) {
                innerScoreDirector.changeVariableFacade(variableDescriptor, rightTrailingLastEntity, leftLastEntity);
            } else {
                innerScoreDirector.changeVariableFacade(variableDescriptor, rightFirstEntity, leftLastEntity);
            }
        }
    }

    @Override
    public SubChainSwapMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        return new SubChainSwapMove<>(variableDescriptor,
                leftSubChain.rebase(destinationScoreDirector),
                destinationScoreDirector.lookUpWorkingObject(leftTrailingLastEntity),
                rightSubChain.rebase(destinationScoreDirector),
                destinationScoreDirector.lookUpWorkingObject(rightTrailingLastEntity));
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    @Override
    public String getSimpleMoveTypeDescription() {
        return getClass().getSimpleName() + "(" + variableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        List<Object> entities = new ArrayList<>(
                leftSubChain.getSize() + rightSubChain.getSize());
        entities.addAll(leftSubChain.getEntityList());
        entities.addAll(rightSubChain.getEntityList());
        return entities;
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        List<Object> values = new ArrayList<>(2);
        values.add(variableDescriptor.getValue(leftSubChain.getFirstEntity()));
        values.add(variableDescriptor.getValue(rightSubChain.getFirstEntity()));
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SubChainSwapMove<?> other = (SubChainSwapMove<?>) o;
        return Objects.equals(variableDescriptor, other.variableDescriptor) &&
                Objects.equals(leftSubChain, other.leftSubChain) &&
                Objects.equals(rightSubChain, other.rightSubChain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableDescriptor, leftSubChain, rightSubChain);
    }

    @Override
    public String toString() {
        Object oldLeftValue = variableDescriptor.getValue(leftSubChain.getFirstEntity());
        Object oldRightValue = variableDescriptor.getValue(rightSubChain.getFirstEntity());
        return leftSubChain.toDottedString() + " {" + oldLeftValue + "} <-> "
                + rightSubChain.toDottedString() + " {" + oldRightValue + "}";
    }

}
