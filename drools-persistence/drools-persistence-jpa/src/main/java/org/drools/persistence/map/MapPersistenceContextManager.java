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

package org.drools.persistence.map;

import org.drools.persistence.api.PersistenceContext;
import org.drools.persistence.api.PersistenceContextManager;

public class MapPersistenceContextManager
    implements
    PersistenceContextManager {

    private PersistenceContext persistenceContext;
    
    public MapPersistenceContextManager(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }
    
    public PersistenceContext getApplicationScopedPersistenceContext() {
        return persistenceContext;
    }

    public PersistenceContext getCommandScopedPersistenceContext() {
        return persistenceContext;
    }

    public void beginCommandScopedEntityManager() {
    }

    public void endCommandScopedEntityManager() {
    }

    public void dispose() {
        persistenceContext.close();
    }

    public void clearPersistenceContext() {

    }

}
