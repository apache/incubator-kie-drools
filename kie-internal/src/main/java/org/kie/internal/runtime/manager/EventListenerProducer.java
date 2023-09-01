package org.kie.internal.runtime.manager;

import java.util.List;
import java.util.Map;

/**
 * Allows do define custom producers for know EventListeners. Intention of this is that there might be several
 * implementations that might provide different listener instance based on the context they are executed in.
 * <br>
 * It will be invoked by RegisterableItemsFactory implementation (especially InjectableRegisterableItemsFactory
 * in CDI world) for every KieSession. Recommendation is to always produce new instances to avoid unexpected
 * results.
 *
 * @param <T> type of the event listener - ProcessEventListener, AgendaEventListener, WorkingMemoryEventListener
 */
public interface EventListenerProducer<T> {

    /**
     * Returns list of instances for given (T) type of listeners
     * <br>
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
