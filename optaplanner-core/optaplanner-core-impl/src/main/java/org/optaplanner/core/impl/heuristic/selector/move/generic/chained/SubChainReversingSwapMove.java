package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
public class SubChainReversingSwapMove<Solution_> extends AbstractMove<Solution_> {

    private final GenuineVariableDescriptor<Solution_> variableDescriptor;

    protected final SubChain leftSubChain;
    protected final Object leftTrailingLastEntity;
    protected final SubChain rightSubChain;
    protected final Object rightTrailingLastEntity;

    public SubChainReversingSwapMove(GenuineVariableDescriptor<Solution_> variableDescriptor,
            SingletonInverseVariableSupply inverseVariableSupply,
            SubChain leftSubChain, SubChain rightSubChain) {
        this.variableDescriptor = variableDescriptor;
        this.leftSubChain = leftSubChain;
        leftTrailingLastEntity = inverseVariableSupply.getInverseSingleton(leftSubChain.getLastEntity());
        this.rightSubChain = rightSubChain;
        rightTrailingLastEntity = inverseVariableSupply.getInverseSingleton(rightSubChain.getLastEntity());
    }

    public SubChainReversingSwapMove(GenuineVariableDescriptor<Solution_> variableDescriptor,
            SubChain leftSubChain, Object leftTrailingLastEntity,
            SubChain rightSubChain, Object rightTrailingLastEntity) {
        this.variableDescriptor = variableDescriptor;
        this.leftSubChain = leftSubChain;
        this.rightSubChain = rightSubChain;
        this.leftTrailingLastEntity = leftTrailingLastEntity;
        this.rightTrailingLastEntity = rightTrailingLastEntity;
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
        // Because leftFirstEntity and rightFirstEntity are unequal, chained guarantees their values are unequal too.
        return !SubChainSwapMove.containsAnyOf(rightSubChain, leftSubChain);
    }

    @Override
    public SubChainReversingSwapMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        return new SubChainReversingSwapMove<>(variableDescriptor,
                rightSubChain.reverse(), leftTrailingLastEntity,
                leftSubChain.reverse(), rightTrailingLastEntity);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        Object leftFirstEntity = leftSubChain.getFirstEntity();
        Object leftFirstValue = variableDescriptor.getValue(leftFirstEntity);
        Object leftLastEntity = leftSubChain.getLastEntity();
        Object rightFirstEntity = rightSubChain.getFirstEntity();
        Object rightFirstValue = variableDescriptor.getValue(rightFirstEntity);
        Object rightLastEntity = rightSubChain.getLastEntity();
        Object leftLastEntityValue = variableDescriptor.getValue(leftLastEntity);
        Object rightLastEntityValue = variableDescriptor.getValue(rightLastEntity);
        // Change the entities
        InnerScoreDirector<Solution_, ?> innerScoreDirector = (InnerScoreDirector<Solution_, ?>) scoreDirector;
        if (leftLastEntity != rightFirstValue) {
            innerScoreDirector.changeVariableFacade(variableDescriptor, leftLastEntity, rightFirstValue);
        }
        if (rightLastEntity != leftFirstValue) {
            innerScoreDirector.changeVariableFacade(variableDescriptor, rightLastEntity, leftFirstValue);
        }
        // Reverse the chains
        reverseChain(innerScoreDirector, leftLastEntity, leftLastEntityValue, leftFirstEntity);
        reverseChain(innerScoreDirector, rightLastEntity, rightLastEntityValue, rightFirstEntity);
        // Reroute the new chains
        if (leftTrailingLastEntity != null) {
            if (leftTrailingLastEntity != rightFirstEntity) {
                innerScoreDirector.changeVariableFacade(variableDescriptor, leftTrailingLastEntity, rightFirstEntity);
            } else {
                innerScoreDirector.changeVariableFacade(variableDescriptor, leftLastEntity, rightFirstEntity);
            }
        }
        if (rightTrailingLastEntity != null) {
            if (rightTrailingLastEntity != leftFirstEntity) {
                innerScoreDirector.changeVariableFacade(variableDescriptor, rightTrailingLastEntity, leftFirstEntity);
            } else {
                innerScoreDirector.changeVariableFacade(variableDescriptor, rightLastEntity, leftFirstEntity);
            }
        }
    }

    private void reverseChain(InnerScoreDirector<Solution_, ?> scoreDirector, Object entity, Object previous,
            Object toEntity) {
        while (entity != toEntity) {
            Object value = variableDescriptor.getValue(previous);
            scoreDirector.changeVariableFacade(variableDescriptor, previous, entity);
            entity = previous;
            previous = value;
        }
    }

    @Override
    public SubChainReversingSwapMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        return new SubChainReversingSwapMove<>(variableDescriptor,
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
        final SubChainReversingSwapMove<?> other = (SubChainReversingSwapMove<?>) o;
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
        return leftSubChain.toDottedString() + " {" + oldLeftValue + "} <-reversing-> "
                + rightSubChain.toDottedString() + " {" + oldRightValue + "}";
    }

}
