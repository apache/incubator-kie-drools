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

package org.optaplanner.core.impl.solver.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.IdentityHashMap;
import java.util.List;

import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

public abstract class AbstractEventSupport<E extends EventListener> {

    /**
     * {@link EntitySelector} instances may end up here.
     * Each instance added via {@link #addEventListener(EventListener)} must appear here,
     * regardless of whether it is equal to any other instance already present.
     * Likewise {@link #removeEventListener(EventListener)} must remove elements by identity, not equality.
     * <p>
     * This list-based implementation could be called an "identity set", similar to {@link IdentityHashMap}.
     * We can not use {@link IdentityHashMap}, because we require iteration in insertion order.
     */
    private final List<E> eventListenerList = new ArrayList<>();

    public void addEventListener(E eventListener) {
        for (E addedEventListener : eventListenerList) {
            if (addedEventListener == eventListener) {
                throw new IllegalArgumentException(
                        "Event listener (" + eventListener + ") already found in list (" + eventListenerList + ").");
            }
        }
        eventListenerList.add(eventListener);
    }

    public void removeEventListener(E eventListener) {
        if (!eventListenerList.removeIf(e -> e == eventListener)) {
            throw new IllegalArgumentException(
                    "Event listener (" + eventListener + ") not found in list (" + eventListenerList + ").");
        }
    }

    protected Collection<E> getEventListeners() {
        return eventListenerList;
    }

}
