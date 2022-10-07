package org.optaplanner.core.impl.domain.variable.listener.support;

import org.optaplanner.core.api.domain.variable.ListVariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

final class ListVariableChangedNotification<Solution_> extends AbstractNotification
        implements ListVariableNotification<Solution_> {

    private final int fromIndex;
    private final int toIndex;

    ListVariableChangedNotification(Object entity, int fromIndex, int toIndex) {
        super(entity);
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    @Override
    public void triggerBefore(ListVariableListener<Solution_, Object, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.beforeListVariableChanged(scoreDirector, entity, fromIndex, toIndex);
    }

    @Override
    public void triggerAfter(ListVariableListener<Solution_, Object, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.afterListVariableChanged(scoreDirector, entity, fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return "ListVariableChangedNotification(" + entity + "[" + fromIndex + ".." + toIndex + "])";
    }
}
