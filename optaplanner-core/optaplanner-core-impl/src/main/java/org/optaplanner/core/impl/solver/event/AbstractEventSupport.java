/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.solver.event;

import java.util.EventListener;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class AbstractEventSupport<E extends EventListener> {

    protected Set<E> eventListenerSet = new CopyOnWriteArraySet<>();

    public void addEventListener(E eventListener) {
        eventListenerSet.add(eventListener);
    }

    public void removeEventListener(E eventListener) {
        eventListenerSet.remove(eventListener);
    }

}
