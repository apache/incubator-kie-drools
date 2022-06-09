package org.optaplanner.core.impl.domain.variable.listener.support;

import org.optaplanner.core.api.domain.variable.AbstractVariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

final class EntityRemovedNotification<Solution_> extends AbstractNotification implements EntityNotification<Solution_> {

    EntityRemovedNotification(Object entity) {
        super(entity);
    }

    @Override
    public void triggerBefore(AbstractVariableListener<Solution_, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.beforeEntityRemoved(scoreDirector, entity);
    }

    @Override
    public void triggerAfter(AbstractVariableListener<Solution_, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.afterEntityRemoved(scoreDirector, entity);
    }

    @Override
    public String toString() {
        return "EntityRemoved(" + entity + ")";
    }
}
