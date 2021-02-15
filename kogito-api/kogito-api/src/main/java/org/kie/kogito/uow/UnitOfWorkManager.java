/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.uow;

import org.kie.kogito.event.EventManager;

/**
 * Manager that controls and give access to UnitOfWork.
 * 
 * Main entry point for application usage to gain control about
 * the execution and grouping of work.
 *
 */
public interface UnitOfWorkManager {

    /**
     * Returns current unit of work for this execution context (usually thread).
     * 
     * @return current unit of work
     */
    UnitOfWork currentUnitOfWork();
    
    /**
     * Returns new not started UnitOfWork that is associated with the manager
     * to manage it's life cycle. 
     * 
     * @return new, not started unit of work
     */
    UnitOfWork newUnitOfWork();
    
    /**
     * Returns instance of the event manager configured for this unit of work manager
     * @return event manager instance
     */
    EventManager eventManager();
}
