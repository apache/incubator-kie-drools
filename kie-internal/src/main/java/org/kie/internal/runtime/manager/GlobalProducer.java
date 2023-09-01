package org.kie.internal.runtime.manager;

import java.util.Map;

/**
 * Allows to provide global instances for <code>KieSession</code> to be registered on time when <code>RuntimeEngine</code>
 * is created.
 * <br>
 * It will be invoked by RegisterableItemsFactory implementation (especially InjectableRegisterableItemsFactory
 * in CDI world) for every KieSession.
 *
 */
public interface GlobalProducer {

    /**
     * Returns map of (key = global name, value global instance) of globals
     * to be registered on KieSession
     * <br>
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
