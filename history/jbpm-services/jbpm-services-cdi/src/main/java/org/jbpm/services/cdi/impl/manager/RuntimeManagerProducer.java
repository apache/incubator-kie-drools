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
package org.jbpm.services.cdi.impl.manager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.cdi.qualifier.PerProcessInstance;
import org.kie.internal.runtime.manager.cdi.qualifier.PerRequest;
import org.kie.internal.runtime.manager.cdi.qualifier.Singleton;

/**
 * Producer method used to build instances of <code>RuntimeManager</code> automatically by CDI container.
 * It contains dedicated producer methods for all supported manager types:
 * <ul>
 *  <li>singleton</li>
 *  <li>per request</li>
 *  <li>per process instance</li>
 * </ul>
 * It requires two bean injected that are required for successful instantiation of <code>RuntimeManager</code>:
 * <ul>
 *  <li><code>RuntimeEnvironment</code></li>
 *  <li><code>RuntimeManagerFactory</code></li>
 * </ul>
 * 
 * Injections are controlled with qualifiers so same should be used at injection points that should receive them
 * 
 * @see Singleton
 * @see PerRequest
 * @see PerProcessInstance
 */
@ApplicationScoped
@SuppressWarnings("serial")
public class RuntimeManagerProducer {

    @Inject
    @Any
    private Instance<RuntimeEnvironment> environmentInstance;
    @Inject
    private RuntimeManagerFactory runtimeManagerFactory;
    
    @Produces
    @Singleton
    public RuntimeManager newSingletonRuntimeManager() {
        
        RuntimeEnvironment environment = environmentInstance.select(new AnnotationLiteral<Singleton>(){}).get();
        
        return runtimeManagerFactory.newSingletonRuntimeManager(environment);
    }
    
    @Produces
    @PerRequest
    public RuntimeManager newPerRequestRuntimeManager() {
        RuntimeEnvironment environment = environmentInstance.select(new AnnotationLiteral<PerRequest>(){}).get();
        
        return runtimeManagerFactory.newPerRequestRuntimeManager(environment);
    }
    
    @Produces
    @PerProcessInstance
    public RuntimeManager newPerProcessInstanceRuntimeManager() {
        RuntimeEnvironment environment = environmentInstance.select(new AnnotationLiteral<PerProcessInstance>(){}).get();
        
        return runtimeManagerFactory.newPerProcessInstanceRuntimeManager(environment);
    }
}
