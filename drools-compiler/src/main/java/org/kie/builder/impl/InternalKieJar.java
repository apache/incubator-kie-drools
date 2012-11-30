package org.kie.builder.impl;

import org.kie.builder.KieJar;
import org.kie.runtime.KieBase;

import java.io.File;

public interface InternalKieJar extends KieJar {

    File asFile();

    void addKieBase(String kBaseName, KieBase kBase);

    KieBase getKieBase(String kBaseName);
}
