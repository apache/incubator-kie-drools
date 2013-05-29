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

import org.kie.api.runtime.process.WorkItemHandler;

/**
 * Allows to provide custom implementations to deliver WorkItem name and WorkItemHandler instance pairs
 * for the runtime.
 * <br/>
 * It will be invoked by RegisterableItemsFactory implementation (especially InjectableRegisterableItemsFactory 
 * in CDI world) for every KieSession. Recommendation is to always produce new instances to avoid unexpected 
 * results. 
 *
 */
public interface WorkItemHandlerProducer {

    /**
     * Returns map of (key = work item name, value work item handler instance) of work items 
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
    Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params);
}
