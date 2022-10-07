package org.optaplanner.core.impl.domain.variable.listener.support;

import org.optaplanner.core.api.domain.variable.ListVariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

final class ElementUnassignedNotification<Solution_> implements ListVariableNotification<Solution_> {

    private final Object element;

    ElementUnassignedNotification(Object element) {
        this.element = element;
    }

    @Override
    public void triggerBefore(ListVariableListener<Solution_, Object, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        throw new UnsupportedOperationException("ListVariableListeners do not listen for this event.");
    }

    @Override
    public void triggerAfter(ListVariableListener<Solution_, Object, Object> variableListener,
            ScoreDirector<Solution_> scoreDirector) {
        variableListener.afterListVariableElementUnassigned(scoreDirector, element);
    }

    @Override
    public String toString() {
        return "ElementUnassigned(" + element + ")";
    }
}
