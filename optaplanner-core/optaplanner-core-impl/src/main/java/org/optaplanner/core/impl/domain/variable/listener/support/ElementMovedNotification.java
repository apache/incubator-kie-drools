package org.optaplanner.core.impl.domain.variable.listener.support;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.ListVariableListener;

final class ElementMovedNotification<Solution_> extends AbstractNotification implements ListVariableNotification<Solution_> {

    private final Object destinationEntity;
    private final int destinationIndex;

    ElementMovedNotification(Object sourceEntity, int sourceIndex, Object destinationEntity, int destinationIndex) {
        super(sourceEntity, sourceIndex);
        this.destinationEntity = destinationEntity;
        this.destinationIndex = destinationIndex;
    }

    @Override
    public void triggerBefore(ListVariableListener<Solution_, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.beforeElementMoved(scoreDirector, entity, index, destinationEntity, destinationIndex);
    }

    @Override
    public void triggerAfter(ListVariableListener<Solution_, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.afterElementMoved(scoreDirector, entity, index, destinationEntity, destinationIndex);
    }

    @Override
    public String toString() {
        return "ElementMoved(" + entity + "[" + index + "]->" + destinationEntity + "[" + destinationIndex + "])";
    }
}
