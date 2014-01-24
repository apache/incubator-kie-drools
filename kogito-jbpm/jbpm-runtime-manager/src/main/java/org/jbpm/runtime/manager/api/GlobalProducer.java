/*
 * Copyright 2013 JBoss Inc
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
package org.jbpm.runtime.manager.api;

import java.util.Map;

/**
 * Allows to provide global instances for <code>KieSession</code> to be registered on time when <code>RuntimeEngine</code>
 * is created.
 * <br/>
 * It will be invoked by RegisterableItemsFactory implementation (especially InjectableRegisterableItemsFactory 
 * in CDI world) for every KieSession.
 *
 */
public interface GlobalProducer {

    /**
     * Returns map of (key = global name, value global instance) of globals 
     * to be registered on KieSession
     * <br/>
     * Parameters that might be given are as follows:
     * <ul>
     *  <li>ksession</li>
     *  <li>taskService</li>
     *  <li>runtimeManager</li>
     * </ul>
     * 
     * @param identifier - identifier of the owner - usually RuntimeManager that allows the producer to filter out
     * and provide valid instances for given owner
     * @param params - owner might provide some parameters, usually KieSession, TaskService, RuntimeManager instances
     * @return map of work item handler instances (recommendation is to always return new instances when this method is invoked)
     */
    Map<String, Object> getGlobals(String identifier, Map<String, Object> params);
}
