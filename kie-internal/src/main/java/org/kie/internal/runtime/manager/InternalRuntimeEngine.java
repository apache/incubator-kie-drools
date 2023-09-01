package org.kie.internal.runtime.manager;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;

/**
 * Extension to stable API of RuntimeEngine that provides additional capabilities.
 *
 */
public interface InternalRuntimeEngine extends RuntimeEngine {

    /**
     * Returns internal kie session.
     * @return kie session already created
     */
    KieSession internalGetKieSession();
}
