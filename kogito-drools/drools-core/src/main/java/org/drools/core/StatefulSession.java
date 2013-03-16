/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core;

import java.util.List;

import org.drools.core.spi.AgendaFilter;

/**
 * A stateful session represents a working memory which keeps state
 * between invocations (accumulating facts/knowledge).
 *
 * Caution should be used when using the async methods (take note of the javadocs for specific methods).
 *
 * IMPORTANT: Remember to call dispose() when finished with this session.
 */
public interface StatefulSession
    extends
    WorkingMemory {

    /**
     * Forces the workingMemory to be disconnected from the rulebase (not calling this may cause resource leaks).
     */
    void dispose();

    List getRuleBaseUpdateListeners();
    
    /**
     * Keeps firing activations until a halt is called. If in a given moment,
     * there is no activation to fire, it will wait for an activation to be
     * added to an active agenda group or rule flow group.
     * 
     * @throws IllegalStateException
     *             if this method is called when running in sequential mode
     */
    public void fireUntilHalt();

    /**
     * Keeps firing activations until a halt is called. If in a given moment,
     * there is no activation to fire, it will wait for an activation to be
     * added to an active agenda group or rule flow group.
     * 
     * @param agendaFilter
     *            filters the activations that may fire
     * 
     * @throws IllegalStateException
     *             if this method is called when running in sequential mode
     */
    public void fireUntilHalt(final AgendaFilter agendaFilter);
    
}
