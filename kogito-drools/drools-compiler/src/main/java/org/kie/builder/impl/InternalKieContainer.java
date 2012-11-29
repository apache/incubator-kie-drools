package org.kie.builder.impl;

import org.kie.builder.KieContainer;
import org.kie.builder.KieJar;

public interface InternalKieContainer extends KieContainer {
    void updateKieJar(KieJar kieJar);
}
