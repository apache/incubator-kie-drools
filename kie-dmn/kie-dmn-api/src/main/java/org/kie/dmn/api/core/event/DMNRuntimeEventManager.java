/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.api.core.event;

import org.kie.dmn.api.core.DMNRuntime;

import java.util.Set;

/**
 * A runtime event manager interface for DMN
 */
public interface DMNRuntimeEventManager {

    /**
     * Registers a runtime listener
     *
     * @param listener listener to register
     */
    void addListener(DMNRuntimeEventListener listener);

    /**
     * Removes a runtime listener
     *
     * @param listener listener to remove
     */
    void removeListener(DMNRuntimeEventListener listener);

    /**
     * Returns the set of all registered listeners
     *
     * @return set of all registered listeners
     */
    Set<DMNRuntimeEventListener> getListeners();

    /**
     * Returns true if there are registered listeners, false otherwise
     *
     * @return
     */
    boolean hasListeners();

    DMNRuntime getRuntime();

}
