package org.kie.builder.impl;

import org.kie.builder.KieContainer;
import org.kie.builder.KieScanner;

public interface InternalKieScanner extends KieScanner {
    void setKieContainer(KieContainer kieContainer);
}
