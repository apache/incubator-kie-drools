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
