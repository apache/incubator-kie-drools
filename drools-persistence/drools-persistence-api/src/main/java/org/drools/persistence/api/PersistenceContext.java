/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.persistence.api;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;

public interface PersistenceContext {

    /**
     * This method persists the entity. If pessimistic locking is being used, the method will also immediately lock the entity
     * and return a reference to the locked entity. 
     * @param sessionInfo The {@link SessionInfo} instance representing the state of the {@link KieSession}
     * @return sessionInfo a reference to the persisted {@link SessionInfo} instance.
     */
    PersistentSession persist(PersistentSession session);

    public PersistentSession findSession(Long id);

    void remove(PersistentSession sessionInfo);
    
    boolean isOpen();

    void joinTransaction();

    void close();
    
    PersistentWorkItem persist(PersistentWorkItem workItem);

    PersistentWorkItem findWorkItem(Long id);

    void remove(PersistentWorkItem workItem);

    /**
     * This method pessimistically locks the {@link WorkItemInfo} instance
     * @param sessionInfo The persistent representation of a {@link WorkItem}
     */
    void lock(PersistentWorkItem workItem);
    
    PersistentWorkItem merge(PersistentWorkItem workItem);

}
