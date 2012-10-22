package org.drools.builder;

import org.drools.KBaseUnit;
import org.drools.KnowledgeBase;
import org.drools.runtime.StatefulKnowledgeSession;

import java.io.File;
import java.util.List;

public interface KnowledgeJarBuilder {

    File buildKJar(File rootFolder, File outputFolder, String jarName);

    List<KBaseUnit> getKBaseUnits();

    KBaseUnit getKBaseUnit(String kBaseName);

    KnowledgeBase getKnowledgeBase(String kBaseName);

    StatefulKnowledgeSession getStatefulKnowlegeSession(String kSessionName);
}
