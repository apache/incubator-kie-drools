package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.kie.builder.impl.event.KieServicesEventListerner;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

public interface InternalKieServices extends KieServices {
    void registerListener(KieServicesEventListerner listener);

    /**
     * Clear the containerId reference from the internal registry hold by the KieServices.
     * Epsecially helpful to avoid leaking reference on container dispose(), to inadvertently keep a reference in the internal registry which would never be GC.
     */
	void clearRefToContainerId(String containerId, KieContainer containerRef);
}
