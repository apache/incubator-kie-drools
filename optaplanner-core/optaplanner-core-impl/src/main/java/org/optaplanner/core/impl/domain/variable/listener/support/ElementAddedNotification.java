package org.optaplanner.core.impl.domain.variable.listener.support;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.ListVariableListener;

final class ElementAddedNotification<Solution_> extends AbstractNotification implements ListVariableNotification<Solution_> {

    private final int index;

    ElementAddedNotification(Object entity, int index) {
        super(entity);
        this.index = index;
    }

    @Override
    public void triggerBefore(ListVariableListener<Solution_, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.beforeListVariableElementAdded(scoreDirector, entity, index);
    }

    @Override
    public void triggerAfter(ListVariableListener<Solution_, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.afterListVariableElementAdded(scoreDirector, entity, index);
    }

    @Override
    public String toString() {
        return "ElementAdded(" + entity + "[" + index + "])";
    }
}
