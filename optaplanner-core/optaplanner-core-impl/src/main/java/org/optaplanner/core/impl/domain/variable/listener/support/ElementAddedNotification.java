package org.optaplanner.core.impl.domain.variable.listener.support;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.ListVariableListener;

final class ElementAddedNotification<Solution_> extends AbstractNotification implements ListVariableNotification<Solution_> {

    ElementAddedNotification(Object entity, int index) {
        super(entity, index);
    }

    @Override
    public void triggerBefore(ListVariableListener<Solution_, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.beforeElementAdded(scoreDirector, entity, index);
    }

    @Override
    public void triggerAfter(ListVariableListener<Solution_, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.afterElementAdded(scoreDirector, entity, index);
    }

    @Override
    public String toString() {
        return "ElementAdded(" + entity + "[" + index + "])";
    }
}
