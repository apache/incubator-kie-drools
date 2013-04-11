package org.jbpm.runtime.manager.api;

import org.kie.internal.runtime.manager.RuntimeManager;

/**
 * This interface is meant to be implemented in components that
 * <ul>
 *  <li>need to use {@link RuntimeManager} instances</li>
 *  <li>do <i>not</i> instantiate or configure the {@link RuntimeManager} instances themselves.</li>
 * </ul>
 * </p>
 * Classes in EE and CDI Container components that <i>do</i> instantiate and configure {@link RuntimeManager} instances should
 * makes sure to inject an instance of this class in order to make the {@link RuntimeManager} instances they create available
 * for other deployments dependent on them.
 * </p>
 * Implementations of this class should <b>never</b> interfere in the life-cycle of the {@link RuntimeManager}.
 * </p>
 * Lastly, implementations of this class should be thread-safe and should be able to be used in any scope.
 */
public interface RuntimeManagerCacheEntryPoint {

    /**
     * Add a new {@link RuntimeManager} instance.
     * 
     * @param domain The name of the domain with which the {@link RuntimeManager} is associated.
     * @param runtimeManager The {@link RuntimeManager} instance.
     */
    public void addDomainRuntimeManager(String domain, RuntimeManager runtimeManager);

    /**
     * Replace a {@link RuntimeManager} instance that has been closed with a new {@link RuntimeManager} instance.
     * 
     * @param domain The name of the domain with which the new {@link RuntimeManager} is associated.
     * @param runtimeManager The new {@link RuntimeManager} instance.
     * @return The {@link RuntimeManager} instance that's being replaced.
     */
    public RuntimeManager replaceDomainRuntimeManager(String domain, RuntimeManager runtimeManager);

    /**
     * Remove a {@link RuntimeManager} instance that. Use this method if the {@link RuntimeManager} is not being replaced, but, for
     * example, is being closed because
     * the domain has been been deleted.
     * 
     * @param domain The name of the domain with which the new {@link RuntimeManager} is associated.
     * @return The {@link RuntimeManager} instance that's being removed
     */
    public RuntimeManager removeDomainRuntimeManager(String domain);
}