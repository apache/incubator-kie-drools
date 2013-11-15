/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.executor.impl.runtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.InternalRuntimeManager;

/**
 * Registry for active <code>RuntimeManagers</code> that can be used from within executor service.
 *
 */
public class RuntimeManagerRegistry {

    private Map<String, RuntimeManager> registry = new ConcurrentHashMap<String, RuntimeManager>();
    
    private static RuntimeManagerRegistry instance;
    
    protected RuntimeManagerRegistry() {
        
    }
    
    /**
     * Returns singleton instance of the registry
     * @return
     */
    public static RuntimeManagerRegistry get() {
        if (instance == null) {
            instance = new RuntimeManagerRegistry();
        }
        
        return instance;
    }
    
    /**
     * Adds new <code>RuntimeManager</code> that shall be available for the executor
     * @param id - id of the RuntimeManager
     * @param manager - RuntimeManager instance
     */
    public void addRuntimeManager(String id, RuntimeManager manager) {
        RuntimeManager cachedManager = registry.get(id);
        // allow to register/override if there is no runtime manager or runtime manager is closed
    	if (cachedManager != null && !((InternalRuntimeManager)cachedManager).isClosed()) {
            throw new IllegalStateException("RuntimeManager with id " + id + " is already registered");
        }
        
        registry.put(id, manager);
    }
    
    /**
     * Remove <code>RuntimeManager</code> identified by given id
     * @param id - id of the RuntimeManager
     */
    public void remove(String id) {
        registry.remove(id);
    }
    
    /**
     * Returns RuntimeManager instance if registered, otherwise null
     * @param id - id of the RuntimeManager
     * @return registered instance of RuntimeManager or null
     */
    public RuntimeManager getRuntimeManager(String id) {
        return registry.get(id);
    }
}
