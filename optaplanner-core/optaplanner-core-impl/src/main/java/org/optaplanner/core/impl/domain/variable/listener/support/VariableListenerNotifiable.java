package org.optaplanner.core.impl.domain.variable.listener.support;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * A notifiable specialized to receive {@link BasicVariableNotification}s and trigger them on a given {@link VariableListener}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
final class VariableListenerNotifiable<Solution_> extends AbstractNotifiable<Solution_, VariableListener<Solution_, Object>> {

    VariableListenerNotifiable(
            ScoreDirector<Solution_> scoreDirector,
            VariableListener<Solution_, Object> variableListener,
            int globalOrder) {
        super(scoreDirector, variableListener, globalOrder);
    }
}
