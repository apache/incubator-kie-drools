/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.domain.variable.AbstractVariableListener;

/**
 * A notifiable’s purpose is to execute variable listener methods. This interface is the most
 * generalized form of a notifiable. It covers variable listener methods that are executed immediately
 * ({@link AbstractVariableListener#resetWorkingSolution} and {@link AbstractVariableListener#close}.
 * </p>
 * Specialized notifiables use {@link Notification}s to record planing variable changes and defer triggering of "after" methods
 * so that dependent variable listeners can be executed in the correct order.
 */
public interface Notifiable {

    /**
     * Notify the variable listener about working solution reset.
     */
    void resetWorkingSolution();

    /**
     * Trigger all queued notifications.
     */
    void triggerAllNotifications();

    /**
     * Close the variable listener.
     */
    void closeVariableListener();
}
