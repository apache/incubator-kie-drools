/**
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

package org.drools;

import java.util.Collection;
import java.util.List;

import org.drools.concurrent.Future;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.AgendaFilter;

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

    /**
     * Insert/Assert an object asynchronously.
     * (return immediately, even while the insertion is taking effect).
     * The returned Future object can be queried to check on the status of the task.
     * You should only use the async methods if you are sure you require a background
     * insertion task to take effect (a new thread may be created).
     * If you are not sure, then you probably don't need to use it !
     */
    Future asyncInsert(Object object);

    Future asyncRetract(FactHandle factHandle);

    Future asyncUpdate(FactHandle factHandle,
                       Object object);

    /**
     * Insert/Assert an array of objects..
     * (return immediately, even while the insertion is taking effect).
     * The returned Future object can be queried to check on the status of the task.
     * You should only use the async methods if you are sure you require a background
     * insertion task to take effect (a new thread may be created).
     * If you are not sure, then you probably don't need to use it !
     */
    Future asyncInsert(Object[] array);

    /**
     * Insert/Assert a collect of objects..
     * (return immediately, even while the insertion is taking effect).
     * The returned Future object can be queried to check on the status of the task.
     * You should only use the async methods if you are sure you require a background
     * insertion task to take effect (a new thread may be created).
     * If you are not sure, then you probably don't need to use it !
     */
    Future asyncInsert(Collection collect);

    /**
     * This will initiate the firing phase (in the background).
     * And return immediately. The returned Future object can be queried
     * to check on the status of the task.
     */
    Future asyncFireAllRules();

    /**
     * This will initiate the firing phase (in the background).
     * And return immediately. The returned Future object can be queried
     * to check on the status of the task.
     */
    Future asyncFireAllRules(AgendaFilter agendaFilter);

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
