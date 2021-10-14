/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.event.process;

import java.util.Collection;

/**
 * A manager for process related events.
 */
public interface ProcessEventManager {

    /**
     * Add a process event listener.
     *
     * @param listener the listener to add.
     */
    public void addEventListener(ProcessEventListener listener);

    /**
     * Remove a process event listener.
     *
     * @param listener the listener to remove
     */
    public void removeEventListener(ProcessEventListener listener);

    /**
     * Returns all event listeners.
     *
     * @return listeners the listeners
     */
    public Collection<ProcessEventListener> getProcessEventListeners();

}
