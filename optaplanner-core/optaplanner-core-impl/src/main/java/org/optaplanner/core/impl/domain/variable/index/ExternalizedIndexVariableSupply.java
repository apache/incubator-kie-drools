package org.optaplanner.core.impl.domain.variable.index;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.optaplanner.core.api.domain.variable.ListVariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.SourcedVariableListener;

/**
 * Alternative to {@link IndexVariableListener}.
 */
public class ExternalizedIndexVariableSupply<Solution_> implements
        SourcedVariableListener<Solution_>,
        ListVariableListener<Solution_, Object, Object>,
        IndexVariableSupply {

    protected final ListVariableDescriptor<Solution_> sourceVariableDescriptor;

    protected Map<Object, Integer> indexMap = null;

    public ExternalizedIndexVariableSupply(ListVariableDescriptor<Solution_> sourceVariableDescriptor) {
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    @Override
    public VariableDescriptor<Solution_> getSourceVariableDescriptor() {
        return sourceVariableDescriptor;
    }

    @Override
    public void resetWorkingSolution(ScoreDirector<Solution_> scoreDirector) {
        indexMap = new IdentityHashMap<>();
        sourceVariableDescriptor.getEntityDescriptor().getSolutionDescriptor()
                .visitAllEntities(scoreDirector.getWorkingSolution(), this::insert);
    }

    @Override
    public void close() {
        indexMap = null;
    }

    @Override
    public void beforeEntityAdded(ScoreDirector<Solution_> scoreDirector, Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<Solution_> scoreDirector, Object entity) {
        insert(entity);
    }

    @Override
    public void afterListVariableElementUnassigned(ScoreDirector<Solution_> scoreDirector, Object element) {
        Integer oldIndex = indexMap.remove(element);
        if (oldIndex == null) {
            throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                    + " because the element (" + element
                    + ") has an oldIndex (" + oldIndex
                    + ") which is null.");
        }
    }

    @Override
    public void beforeListVariableChanged(ScoreDirector<Solution_> scoreDirector, Object entity, int fromIndex, int toIndex) {
        // Do nothing
    }

    @Override
    public void afterListVariableChanged(ScoreDirector<Solution_> scoreDirector, Object entity, int fromIndex, int toIndex) {
        updateIndexes(entity, fromIndex);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<Solution_> scoreDirector, Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<Solution_> scoreDirector, Object entity) {
        // When the entity is removed, its values become unassigned. An unassigned value has no inverse entity and no index.
        retract(entity);
    }

    private void insert(Object entity) {
        List<Object> listVariable = sourceVariableDescriptor.getListVariable(entity);
        int index = 0;
        for (Object element : listVariable) {
            Integer oldIndex = indexMap.put(element, index);
            if (oldIndex != null) {
                throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                        + " because the element (" + element
                        + ") at index (" + index
                        + ") has an oldIndex (" + oldIndex
                        + ") which is not null.");
            }
            index++;
        }
    }

    private void retract(Object entity) {
        List<Object> listVariable = sourceVariableDescriptor.getListVariable(entity);
        int index = 0;
        for (Object element : listVariable) {
            Integer oldIndex = indexMap.remove(element);
            if (!Objects.equals(oldIndex, index)) {
                throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                        + " because the element (" + element
                        + ") at index (" + index
                        + ") has an oldIndex (" + oldIndex
                        + ") which is unexpected.");
            }
            index++;
        }
    }

    private void updateIndexes(Object entity, int startIndex) {
        List<Object> listVariable = sourceVariableDescriptor.getListVariable(entity);
        for (int index = startIndex; index < listVariable.size(); index++) {
            Object element = listVariable.get(index);
            Integer oldIndex = indexMap.put(element, index);
            // The first element is allowed to have a null oldIndex because it might have been just assigned.
            if (oldIndex == null && index != startIndex) {
                throw new IllegalStateException("The supply (" + this + ") is corrupted,"
                        + " because the element (" + element
                        + ") at index (" + index
                        + ") has an oldIndex (" + oldIndex
                        + ") which is null.");
            }
        }
    }

    @Override
    public Integer getIndex(Object element) {
        return indexMap.get(element);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sourceVariableDescriptor.getVariableName() + ")";
    }
}
