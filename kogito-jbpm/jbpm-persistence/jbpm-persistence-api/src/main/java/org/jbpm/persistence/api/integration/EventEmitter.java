/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.persistence.api.integration;

import java.util.Collection;

/**
 * Emitter is the main integration point with external systems.
 * It will be called first to get implementation of the <code>EventCollection</code>
 * next depending on the PersistenceEventManager implementation will provide the 
 * actual events on three phases:
 * 
 * <ul>
 *  <li>deliver - is the first phase that gives complete view of events before persistence is completed</li>
 *  <li>apply - is kind of confirmation that persistence is completed and events can be safely transferred</li>
 *  <li>drop - is kind of rejection that persistence failed and events should be discarded</li>
 * </ul>
 *
 */
public interface EventEmitter {

    /**
     * Invoked just before persistence layer is completed - e.g. beforeCompletion in JTA transaction
     * @param data complete view of events
     */
    void deliver(Collection<InstanceView<?>> data);
    
    /**
     * Indicates that given collection of events can be safely transferred to external systems
     * @param data complete view of events
     */
    void apply(Collection<InstanceView<?>> data);
    
    /**
     * Indicates that given collection of events should be discarded
     * @param data complete view of events
     */
    void drop(Collection<InstanceView<?>> data);
    
    /**
     * Returns new instance of EventCollection to be used while collecting events
     * @return instance of EventCollection implementation
     */
    EventCollection newCollection();
    
    /**
     * Closes any resources used by emitter to interact with external system
     */
    void close();
}
