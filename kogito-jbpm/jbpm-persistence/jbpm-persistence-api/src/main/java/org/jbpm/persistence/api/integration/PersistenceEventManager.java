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

/**
 * Main entry point for integration default persistence layer with other systems.
 * Responsible for collecting InstanceView that are then send through the EventEmitter.
 */
public interface PersistenceEventManager {

    /**
     * Invoked by persistence layer when new instance is created 
     * @param item view of the actual instance
     */
    void create(InstanceView<?> item);
    
    /**
     * Invoked by persistence layer when instance is updated 
     * @param item view of the actual instance
     */
    void update(InstanceView<?> item);
    
    /**
     * Invoked by persistence layer when instance is deleted 
     * @param item view of the actual instance
     */
    void delete(InstanceView<?> item);
    
    /**
     * Requests to close the manager and underlying resources e.g. emitter
     */
    void close();
    
    /**
     * Determines if event manager is actually active (there is emitter found)
     */
    boolean isActive();
}
