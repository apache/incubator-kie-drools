package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class ListAssignMove<Solution_> extends AbstractMove<Solution_> {

    private final ListVariableDescriptor<Solution_> variableDescriptor;
    private final Object planningValue;
    private final Object destinationEntity;
    private final int destinationIndex;

    public ListAssignMove(
            ListVariableDescriptor<Solution_> variableDescriptor,
            Object planningValue,
            Object destinationEntity, int destinationIndex) {
        this.variableDescriptor = variableDescriptor;
        this.planningValue = planningValue;
        this.destinationEntity = destinationEntity;
        this.destinationIndex = destinationIndex;
    }

    public Object getDestinationEntity() {
        return destinationEntity;
    }

    public int getDestinationIndex() {
        return destinationIndex;
    }

    public Object getMovedValue() {
        return planningValue;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        return true;
    }

    @Override
    public ListUnassignMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        return new ListUnassignMove<>(variableDescriptor, destinationEntity, destinationIndex);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        InnerScoreDirector<Solution_, ?> innerScoreDirector = (InnerScoreDirector<Solution_, ?>) scoreDirector;
        // Add planningValue to destinationEntity's list variable (at destinationIndex).
        innerScoreDirector.beforeElementAdded(variableDescriptor, destinationEntity, destinationIndex);
        variableDescriptor.addElement(destinationEntity, destinationIndex, planningValue);
        innerScoreDirector.afterElementAdded(variableDescriptor, destinationEntity, destinationIndex);
    }

    @Override
    public ListAssignMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        return new ListAssignMove<>(
                variableDescriptor,
                destinationScoreDirector.lookUpWorkingObject(planningValue),
                destinationScoreDirector.lookUpWorkingObject(destinationEntity),
                destinationIndex);
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    @Override
    public String getSimpleMoveTypeDescription() {
        return getClass().getSimpleName() + "(" + variableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ListAssignMove<?> other = (ListAssignMove<?>) o;
        return destinationIndex == other.destinationIndex
                && Objects.equals(variableDescriptor, other.variableDescriptor)
                && Objects.equals(planningValue, other.planningValue)
                && Objects.equals(destinationEntity, other.destinationEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableDescriptor, planningValue, destinationEntity, destinationIndex);
    }

    @Override
    public String toString() {
        return String.format("%s {null -> %s[%d]}",
                getMovedValue(), destinationEntity, destinationIndex);
    }
}
