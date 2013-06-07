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

import java.util.List;
import java.util.Map;

/**
 * Allows do define custom producers for know EventListeners. Intention of this is that there might be several 
 * implementations that might provide different listener instance based on the context they are executed in. 
 * <br/>
 * It will be invoked by RegisterableItemsFactory implementation (especially InjectableRegisterableItemsFactory 
 * in CDI world) for every KieSession. Recommendation is to always produce new instances to avoid unexpected 
 * results.
 *
 * @param <T> type of the event listener - ProcessEventListener, AgendaEventListener, WorkingMemoryEventListener
 */
public interface EventListenerProducer<T> {

    /**
     * Returns list of instances for given (T) type of listeners
     * <br/>
     * Parameters that might be given are as follows:
     * <ul>
     *  <li>ksession</li>
     *  <li>taskService</li>
     *  <li>runtimeManager</li>
     * </ul>
     * @param identifier - identifier of the owner - usually RuntimeManager that allows the producer to filter out
     * and provide valid instances for given owner
     * @param params - owner might provide some parameters, usually KieSession, TaskService, RuntimeManager instances
     * @return list of listener instances (recommendation is to always return new instances when this method is invoked)
     */
    List<T> getEventListeners(String identifier, Map<String, Object>  params);
}
