package org.optaplanner.core.impl.domain.variable.listener.support;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.variable.ListVariableListener;

/**
 * A notifiable specialized to receive {@link ListVariableNotification}s and trigger them on a given
 * {@link ListVariableListener}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
final class ListVariableListenerNotifiable<Solution_>
        extends AbstractNotifiable<Solution_, ListVariableListener<Solution_, Object>> {

    ListVariableListenerNotifiable(
            ScoreDirector<Solution_> scoreDirector,
            ListVariableListener<Solution_, Object> variableListener,
            int globalOrder) {
        super(scoreDirector, variableListener, globalOrder);
    }
}
