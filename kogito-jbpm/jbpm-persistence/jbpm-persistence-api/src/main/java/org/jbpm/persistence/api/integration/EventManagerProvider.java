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

import java.util.Iterator;
import java.util.ServiceLoader;

import org.jbpm.persistence.api.integration.base.TransactionalPersistenceEventManager;

/**
 * Provider of PersistenceEventManager implementation to be used. 
 * It does discovery via ServiceLoader and if none found returns default implementation
 * which is <code>org.jbpm.persistence.api.integration.base.TransactionalPersistenceEventManager</code>
 */
public class EventManagerProvider {

    private PersistenceEventManager eventManager;
    
    
    private EventManagerProvider() {
        ServiceLoader<PersistenceEventManager> found = ServiceLoader.load(PersistenceEventManager.class);
        Iterator<PersistenceEventManager> it = found.iterator();
        if (it.hasNext()) {
            eventManager = it.next();
        } else {
            eventManager = new TransactionalPersistenceEventManager();
        }
    }
    
    private static class LazyHolder {
        static final EventManagerProvider INSTANCE = new EventManagerProvider();
    }

    public static EventManagerProvider getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    public PersistenceEventManager get() {
        return eventManager;
    }
    
    public boolean isActive() {
        return eventManager.isActive();
    }
}
