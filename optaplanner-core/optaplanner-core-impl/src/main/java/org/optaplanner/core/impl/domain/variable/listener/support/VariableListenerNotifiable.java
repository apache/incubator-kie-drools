/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.domain.variable.listener.support;

import java.util.Collection;

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
            Collection<Notification<Solution_, ? super VariableListener<Solution_, Object>>> notificationQueue,
            int globalOrder) {
        super(scoreDirector, variableListener, notificationQueue, globalOrder);
    }

    public void notifyBefore(BasicVariableNotification<Solution_> notification) {
        if (storeForLater(notification)) {
            triggerBefore(notification);
        }
    }
}
