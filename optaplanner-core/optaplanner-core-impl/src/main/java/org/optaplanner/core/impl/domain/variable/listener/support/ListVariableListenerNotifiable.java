package org.optaplanner.core.impl.domain.variable.listener.support;

import java.util.Collection;

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
        extends AbstractNotifiable<Solution_, ListVariableListener<Solution_, Object, Object>> {

    ListVariableListenerNotifiable(
            ScoreDirector<Solution_> scoreDirector,
            ListVariableListener<Solution_, Object, Object> variableListener,
            Collection<Notification<Solution_, ? super ListVariableListener<Solution_, Object, Object>>> notificationQueue,
            int globalOrder) {
        super(scoreDirector, variableListener, notificationQueue, globalOrder);
    }

    public void notifyBefore(ListVariableNotification<Solution_> notification) {
        triggerBefore(notification);
    }

    public void notifyAfter(ListVariableNotification<Solution_> notification) {
        storeForLater(notification);
    }
}
