/*
 * Copyright 2015 JBoss Inc
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

package org.drools.persistence;

import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;

public interface PersistenceContext {

    /**
     * This method persists the entity. If pessimistic locking is being used, the method will also immediately lock the entity
     * and return a reference to the locked entity. 
     * @param sessionInfo The {@link SessionInfo} instance representing the state of the {@link KieSession}
     * @return sessionInfo a reference to the persisted {@link SessionInfo} instance.
     */
    SessionInfo persist(SessionInfo sessionInfo);

    public SessionInfo findSessionInfo(Long id);

    void remove(SessionInfo sessionInfo);
    
    boolean isOpen();

    void joinTransaction();

    void close();
    
    WorkItemInfo persist(WorkItemInfo workItemInfo);

    WorkItemInfo findWorkItemInfo(Long id);

    void remove(WorkItemInfo workItemInfo);

    /**
     * This method pessimistically locks the {@link WorkItemInfo} instance
     * @param sessionInfo The persistent representation of a {@link WorkItem}
     */
    void lock(WorkItemInfo workItemInfo);
    
    WorkItemInfo merge(WorkItemInfo workItemInfo);

}
