package org.kie.builder.impl;

import org.kie.builder.KieBaseModel;
import org.kie.builder.KieJar;
import org.kie.runtime.KieBase;

import java.util.Collection;

public interface InternalKieJar extends KieJar {

    void addKieBase(String kBaseName, KieBase kBase);
    void removeKieBase(String kBaseName);

    Collection<String> getKieBaseNames();

    KieBase getKieBase(String kBaseName);

    KieBaseModel getKieBaseForSession(String kSessionName);
}
