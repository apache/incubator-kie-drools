/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.domain.variable.listener.support;

import java.util.ArrayDeque;
import java.util.Collection;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;

final class VariableListenerNotifiable<Solution_> {

    private final ScoreDirector<Solution_> scoreDirector;
    private final VariableListener<Solution_, Object> variableListener;
    private final int globalOrder;

    private final Collection<VariableListenerNotification> notificationQueue;

    VariableListenerNotifiable(
            ScoreDirector<Solution_> scoreDirector,
            VariableListener<Solution_, ?> variableListener,
            int globalOrder) {
        this.scoreDirector = scoreDirector;
        this.variableListener = (VariableListener<Solution_, Object>) variableListener;
        this.globalOrder = globalOrder;
        if (variableListener.requiresUniqueEntityEvents()) {
            notificationQueue = new SmallScalingOrderedSet<>();
        } else {
            notificationQueue = new ArrayDeque<>();
        }
    }

    void resetWorkingSolution() {
        variableListener.resetWorkingSolution(scoreDirector);
    }

    void closeVariableListener() {
        variableListener.close();
    }

    void addNotification(VariableListenerNotification notification) {
        if (notificationQueue.add(notification)) {
            notification.triggerBefore(variableListener, scoreDirector);
        }
    }

    void triggerAllNotifications() {
        int notifiedCount = 0;
        for (VariableListenerNotification notification : notificationQueue) {
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
