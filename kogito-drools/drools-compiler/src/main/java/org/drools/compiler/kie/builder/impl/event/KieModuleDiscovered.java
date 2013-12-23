package org.drools.compiler.kie.builder.impl.event;

public class KieModuleDiscovered {
    private final String kieModuleUrl;

    public KieModuleDiscovered(String kieModuleUrl) {
        this.kieModuleUrl = kieModuleUrl;
    }

    public String getKieModuleUrl() {
        return kieModuleUrl;
    }

    @Override
    public String toString() {
        return "KieModule discovered: " + kieModuleUrl;
    }
}
