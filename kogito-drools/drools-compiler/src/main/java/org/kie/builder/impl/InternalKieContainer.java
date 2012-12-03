package org.kie.builder.impl;

import org.kie.builder.KieContainer;
import org.kie.builder.KieModule;

public interface InternalKieContainer extends KieContainer {
    void updateKieJar(KieModule kieJar);
}
