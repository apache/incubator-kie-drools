package org.optaplanner.core.impl.domain.variable.listener.support;

import org.optaplanner.core.api.domain.variable.AbstractVariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

final class EntityAddedNotification<Solution_> extends AbstractNotification implements EntityNotification<Solution_> {

    EntityAddedNotification(Object entity) {
        super(entity);
    }

    @Override
    public void triggerBefore(AbstractVariableListener<Solution_, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.beforeEntityAdded(scoreDirector, entity);
    }

    @Override
    public void triggerAfter(AbstractVariableListener<Solution_, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.afterEntityAdded(scoreDirector, entity);
    }

    @Override
    public String toString() {
        return "EntityAdded(" + entity + ")";
    }
}
