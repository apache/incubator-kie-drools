package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.kie.builder.impl.event.KieServicesEventListerner;
import org.kie.api.KieServices;

public interface InternalKieServices extends KieServices {
    void registerListener(KieServicesEventListerner listener);
}
