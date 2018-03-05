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

import java.io.Serializable;
import java.util.Collection;

/**
 * EventCollection is responsible for efficient mechanism to keep events provided to it.
 * It's usually a short lived object and thus should not use any persistent storage.
 * 
 * It's up to concrete implementation how to react to different type of events:
 * <ul>
 *  <li>add</li>
 *  <li>update</li>
 *  <li>remove</li>
 * </ul>
 * For instance one implementation will simply collect all type of events (reducing by duplicates) while
 * another will filter out to keep track of only active items 
 */
public interface EventCollection extends Serializable {
    
    /**
     * Invoked when new instance is created
     * @param event view of the instance in compact way
     */
    void add(InstanceView<?> event);
  
    /**
     * Invoked when instance is updated
     * @param event view of the instance in compact way
     */
    void update(InstanceView<?> item);

    /**
     * Invoked when instance is removed
     * @param event view of the instance in compact way
     */
    void remove(InstanceView<?> item);
    
    /**
     * Returns all collected events.
     * @return events
     */
    Collection<InstanceView<?>> getEvents();

}
