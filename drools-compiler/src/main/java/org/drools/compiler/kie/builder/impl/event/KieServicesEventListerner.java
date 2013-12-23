package org.drools.compiler.kie.builder.impl.event;

public interface KieServicesEventListerner {
    void onKieModuleDiscovered(KieModuleDiscovered event);
}
