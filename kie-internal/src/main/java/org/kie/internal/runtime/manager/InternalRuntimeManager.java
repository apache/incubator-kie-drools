/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.internal.runtime.manager;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.conf.DeploymentDescriptor;

/**
 * Extension to stable API of RuntimeManager that provides additional capabilities
 * that eventually will get propagated to main interface.
 *
 */
public interface InternalRuntimeManager extends RuntimeManager {

    /**
     * Validates if given <code>KieSession</code> is eligible to be used with given context.
     * @param ksession instance of <code>KieSession</code>
     * @param context instance of <code>Context</code>
     * @throws IllegalStateException in case validation fails
     */
    void validate(KieSession ksession, Context<?> context) throws IllegalStateException;

    /**
     * Returns the actual environment used by the <code>RuntimeManager</code>
     * @return
     */
    RuntimeEnvironment getEnvironment();

    /**
     * Indicates if the runtime manager is closed.
     * @return true if runtime manager is closed (close method was invoked on it) otherwise false
     */
    boolean isClosed();

    /**
     * Returns current deployment descriptor for this instance of RuntimeManager, might be null if
     * descriptors are not used
     * @return
     */
    DeploymentDescriptor getDeploymentDescriptor();

    /**
     * Sets deployment descriptor for this instance of RuntimeManager
     * @param descriptor
     */
    void setDeploymentDescriptor(DeploymentDescriptor descriptor);

    /**
     * Sets Security Manager to be used by this instance of RuntimeManager
     * @param securityManager
     */
    void setSecurityManager(SecurityManager securityManager);

    /**
     * Sets CacheManager to be used for manager scoped cacheable items
     * @param cacheManager
     */
    void setCacheManager(CacheManager cacheManager);

    /**
     * Retrieves instance of cache manager
     * @return
     */
    CacheManager getCacheManager();

    /**
     * Returns KieContainer associated with this runtime manager if any
     * @return
     */
    KieContainer getKieContainer();

    /**
     * Sets KieContainer to be associated with this runtime manager
     * @param kieContainer
     */
    void setKieContainer(KieContainer kieContainer);
    
    /**
     * Activates this runtime manager by making sure it will be available for execution. 
     * This is default when creating new instance unless it was already marked as deactivated. 
     */
    void activate();
    
    /**
     * Deactivates this runtime manager by making it only available for already running instances.
     */
    void deactivate();
}
