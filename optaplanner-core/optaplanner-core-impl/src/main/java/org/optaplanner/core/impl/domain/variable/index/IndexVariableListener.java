package org.optaplanner.core.impl.domain.variable.index;

import java.util.List;
import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.ListVariableListener;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class IndexVariableListener<Solution_> implements ListVariableListener<Solution_, Object>, IndexVariableSupply {

    protected final IndexShadowVariableDescriptor<Solution_> shadowVariableDescriptor;
    protected final ListVariableDescriptor<Solution_> sourceVariableDescriptor;

    public IndexVariableListener(
            IndexShadowVariableDescriptor<Solution_> shadowVariableDescriptor,
            ListVariableDescriptor<Solution_> sourceVariableDescriptor) {
        this.shadowVariableDescriptor = shadowVariableDescriptor;
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    @Override
    public void beforeEntityAdded(ScoreDirector<Solution_> scoreDirector, Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<Solution_> scoreDirector, Object entity) {
        updateIndexes((InnerScoreDirector<Solution_, ?>) scoreDirector, entity, 0);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<Solution_> scoreDirector, Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<Solution_> scoreDirector, Object entity) {
        InnerScoreDirector<Solution_, ?> innerScoreDirector = (InnerScoreDirector<Solution_, ?>) scoreDirector;
        List<Object> listVariable = sourceVariableDescriptor.getListVariable(entity);
        for (Object element : listVariable) {
            innerScoreDirector.beforeVariableChanged(shadowVariableDescriptor, element);
            shadowVariableDescriptor.setValue(element, null);
            innerScoreDirector.afterVariableChanged(shadowVariableDescriptor, element);
        }
    }

    @Override
    public void beforeElementAdded(ScoreDirector<Solution_> scoreDirector, Object entity, int index) {
        // Do nothing
    }

    @Override
    public void afterElementAdded(ScoreDirector<Solution_> scoreDirector, Object entity, int index) {
        updateIndexes((InnerScoreDirector<Solution_, ?>) scoreDirector, entity, index);
    }

    @Override
    public void beforeElementRemoved(ScoreDirector<Solution_> scoreDirector, Object entity, int index) {
        InnerScoreDirector<Solution_, ?> innerScoreDirector = (InnerScoreDirector<Solution_, ?>) scoreDirector;
        Object element = sourceVariableDescriptor.getElement(entity, index);
        innerScoreDirector.beforeVariableChanged(shadowVariableDescriptor, element);
        shadowVariableDescriptor.setValue(element, null);
        innerScoreDirector.afterVariableChanged(shadowVariableDescriptor, element);
    }

    @Override
    public void afterElementRemoved(ScoreDirector<Solution_> scoreDirector, Object entity, int index) {
        updateIndexes((InnerScoreDirector<Solution_, ?>) scoreDirector, entity, index);
    }

    @Override
    public void beforeElementMoved(ScoreDirector<Solution_> scoreDirector,
            Object sourceEntity, int sourceIndex,
            Object destinationEntity, int destinationIndex) {
        // Do nothing
    }

    @Override
    public void afterElementMoved(ScoreDirector<Solution_> scoreDirector,
            Object sourceEntity, int sourceIndex,
            Object destinationEntity, int destinationIndex) {
        InnerScoreDirector<Solution_, ?> innerScoreDirector = (InnerScoreDirector<Solution_, ?>) scoreDirector;
        updateIndexes(innerScoreDirector, sourceEntity, sourceIndex);
        updateIndexes(innerScoreDirector, destinationEntity, destinationIndex);
    }

    private void updateIndexes(InnerScoreDirector<Solution_, ?> scoreDirector, Object entity, int startIndex) {
        List<Object> listVariable = sourceVariableDescriptor.getListVariable(entity);
        for (int i = startIndex; i < listVariable.size(); i++) {
            Object element = listVariable.get(i);
            Integer oldIndex = shadowVariableDescriptor.getValue(element);
            if (!Objects.equals(oldIndex, i)) {
                scoreDirector.beforeVariableChanged(shadowVariableDescriptor, element);
                shadowVariableDescriptor.setValue(element, i);
                scoreDirector.afterVariableChanged(shadowVariableDescriptor, element);
            } else if (i != startIndex) {
                // It is possible to quit early when an element with the expected index is encountered
                // and **if it's not the moved element**. For example, when X is moved from Ann[3] to Beth[3],
                // we need to start updating Beth's elements at index 3 where X already has the expected index, but quitting
                // there would be incorrect because all the elements after X need their indexes incremented.
                return;
            }
        }
    }

    @Override
    public Integer getIndex(Object planningValue) {
        return shadowVariableDescriptor.getValue(planningValue);
    }
}
