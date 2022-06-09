package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class ListUnassignMove<Solution_> extends AbstractMove<Solution_> {

    private final ListVariableDescriptor<Solution_> variableDescriptor;
    private final Object sourceEntity;
    private final int sourceIndex;

    public ListUnassignMove(
            ListVariableDescriptor<Solution_> variableDescriptor,
            Object sourceEntity, int sourceIndex) {
        this.variableDescriptor = variableDescriptor;
        this.sourceEntity = sourceEntity;
        this.sourceIndex = sourceIndex;
    }

    private Object getMovedValue() {
        return variableDescriptor.getElement(sourceEntity, sourceIndex);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        return true;
    }

    @Override
    public AbstractMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        // The unassign move only serves as an undo move of the assign move. It is never being undone.
        throw new UnsupportedOperationException("Undoing an unassign move is unsupported.");
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        InnerScoreDirector<Solution_, ?> innerScoreDirector = (InnerScoreDirector<Solution_, ?>) scoreDirector;
        // Remove a planning value from sourceEntity's list variable (at sourceIndex).
        innerScoreDirector.beforeElementRemoved(variableDescriptor, sourceEntity, sourceIndex);
        variableDescriptor.removeElement(sourceEntity, sourceIndex);
        innerScoreDirector.afterElementRemoved(variableDescriptor, sourceEntity, sourceIndex);
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
        ListUnassignMove<?> other = (ListUnassignMove<?>) o;
        return sourceIndex == other.sourceIndex
                && Objects.equals(variableDescriptor, other.variableDescriptor)
                && Objects.equals(sourceEntity, other.sourceEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableDescriptor, sourceEntity, sourceIndex);
    }

    @Override
    public String toString() {
        return String.format("%s {%s[%d] -> null}",
                getMovedValue(), sourceEntity, sourceIndex);
    }
}
