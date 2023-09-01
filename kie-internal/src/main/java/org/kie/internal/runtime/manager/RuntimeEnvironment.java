package org.kie.internal.runtime.manager;


/**
 * Extension of <code>org.kie.api.runtime.manager.RuntimeEnvironment</code> that contains internal methods
 */
public interface RuntimeEnvironment extends org.kie.api.runtime.manager.RuntimeEnvironment {

    /**
     * Delivers concrete implementation of <code>Mapper</code> that provides access to mapping between contexts and
     * ksession identifiers for tracking purposes.
     * @return
     */
    Mapper getMapper();

}
