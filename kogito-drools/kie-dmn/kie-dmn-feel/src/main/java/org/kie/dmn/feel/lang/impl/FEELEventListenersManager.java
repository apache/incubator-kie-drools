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

package org.kie.dmn.feel.lang.impl;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;
import org.kie.dmn.feel.runtime.events.SyntaxErrorEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class FEELEventListenersManager {

    private Set<FEELEventListener> listeners = new HashSet<>(  );

    /**
     * Registers a new event listener into this FEEL instance.
     * The event listeners are notified about signitificative
     * events during compilation or evaluation of expressions.
     *
     * @param listener the listener to register
     */
    public void addListener( FEELEventListener listener ) {
        this.listeners.add( listener );
    }

    /**
     * Removes a listener from the list of event listeners.
     *
     * @param listener the listener to remove
     */
    public void removeListener( FEELEventListener listener ) {
        this.listeners.remove( listener );
    }

    /**
     * Retrieves the set of registered event listeners
     *
     * @return the set of listeners
     */
    public Set<FEELEventListener> getListeners() {
        return this.listeners;
    }

    public boolean hasListeners() {
        return !this.listeners.isEmpty();
    }

    public void notifyListeners(FEELEvent event) {
        this.listeners.forEach( l -> {
            try {
                l.onEvent( event );
            } catch( Throwable t ) {
                // nothing to do
            }
        } );
    }
    
    public static void notifyListeners(FEELEventListenersManager eventsManager, Supplier<FEELEvent> event) {
        if( eventsManager != null && eventsManager.hasListeners() ) {
            eventsManager.notifyListeners(event.get());
        }
    }
}
