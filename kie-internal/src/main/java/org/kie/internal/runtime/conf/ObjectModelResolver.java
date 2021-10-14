/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.runtime.conf;

import java.util.Map;

/**
 * Resolves <code>ObjectModel</code> to actual instances based on underlying mechanism
 * such as reflection, Spring, CDI, MVEL and more.
 *
 */
public interface ObjectModelResolver {

    /**
     * Creates new instance from the given <code>ObjectMode</code>
     * @param model object model that defines the instance
     * @param cl class loader that have access to the classes
     * @param contextParams provides some contextual params that are referenced by name and already created
     * such as RuntimeManager, RuntimeEngine, KieSession, TaskService
     * @return
     */
    Object getInstance(ObjectModel model, ClassLoader cl, Map<String, Object> contextParams);

    /**
     * Accepts if the given <code>resolverId</code> is matching this resolver identifier.
     * @param resolverId identifier of the resolver
     * @return
     */
    boolean accept(String resolverId);
}
