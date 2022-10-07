package org.optaplanner.core.impl.domain.variable.listener.support;

import java.util.ArrayDeque;
import java.util.Collection;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.AbstractVariableListener;
import org.optaplanner.core.api.domain.variable.ListVariableListener;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.util.ListBasedScalingOrderedSet;

/**
 * Generic notifiable that receives and triggers {@link Notification}s for a specific variable listener of the type {@code T}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <T> the variable listener type
 */
abstract class AbstractNotifiable<Solution_, T extends AbstractVariableListener<Solution_, Object>>
        implements EntityNotifiable<Solution_> {

    private final ScoreDirector<Solution_> scoreDirector;
    private final T variableListener;
    private final Collection<Notification<Solution_, ? super T>> notificationQueue;
    private final int globalOrder;

    static <Solution_> EntityNotifiable<Solution_> buildNotifiable(
            ScoreDirector<Solution_> scoreDirector,
            AbstractVariableListener<Solution_, Object> variableListener,
            int globalOrder) {
        if (variableListener instanceof ListVariableListener) {
            return new ListVariableListenerNotifiable<>(
                    scoreDirector,
                    ((ListVariableListener<Solution_, Object, Object>) variableListener),
                    new ArrayDeque<>(), globalOrder);
        } else {
            VariableListener<Solution_, Object> basicVariableListener = (VariableListener<Solution_, Object>) variableListener;
            return new VariableListenerNotifiable<>(
                    scoreDirector,
                    basicVariableListener,
                    basicVariableListener.requiresUniqueEntityEvents()
                            ? new ListBasedScalingOrderedSet<>()
                            : new ArrayDeque<>(),
                    globalOrder);
        }
    }

    AbstractNotifiable(ScoreDirector<Solution_> scoreDirector,
            T variableListener,
            Collection<Notification<Solution_, ? super T>> notificationQueue,
            int globalOrder) {
        this.scoreDirector = scoreDirector;
        this.variableListener = variableListener;
        this.notificationQueue = notificationQueue;
        this.globalOrder = globalOrder;
    }

    @Override
    public void notifyBefore(EntityNotification<Solution_> notification) {
        if (notificationQueue.add(notification)) {
            notification.triggerBefore(variableListener, scoreDirector);
        }
    }

    protected boolean storeForLater(Notification<Solution_, T> notification) {
        return notificationQueue.add(notification);
    }

    protected void triggerBefore(Notification<Solution_, T> notification) {
        notification.triggerBefore(variableListener, scoreDirector);
    }

    @Override
    public void resetWorkingSolution() {
        variableListener.resetWorkingSolution(scoreDirector);
    }

    @Override
    public void closeVariableListener() {
        variableListener.close();
    }

    @Override
    public void triggerAllNotifications() {
        int notifiedCount = 0;
        for (Notification<Solution_, ? super T> notification : notificationQueue) {
            notification.triggerAfter(variableListener, scoreDirector);
            notifiedCount++;
        }
        if (notifiedCount != notificationQueue.size()) {
            throw new IllegalStateException("The variableListener (" + variableListener.getClass()
                    + ") has been notified with notifiedCount (" + notifiedCount
                    + ") but after being triggered, its notificationCount (" + notificationQueue.size()
                    + ") is different.\n"
                    + "Maybe that variableListener (" + variableListener.getClass()
                    + ") changed an upstream shadow variable (which is illegal).");
        }
        notificationQueue.clear();
    }

    @Override
    public String toString() {
        return "(" + globalOrder + ") " + variableListener;
    }
}
