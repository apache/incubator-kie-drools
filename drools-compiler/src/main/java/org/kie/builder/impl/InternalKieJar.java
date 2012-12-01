package org.kie.builder.impl;

import org.kie.builder.KieJar;
import org.kie.builder.KieProject;
import org.kie.runtime.KieBase;

public interface InternalKieJar extends KieJar {

    void addKieBase(String kBaseName, KieBase kBase);

    KieBase getKieBase(String kBaseName);

    KieProject getKieProject();
}
