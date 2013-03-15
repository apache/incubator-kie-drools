package org.drools.core.builder;

import org.drools.KBaseUnit;
import org.drools.KnowledgeBase;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;

import java.io.File;
import java.util.List;

public interface KnowledgeContainer {

    File buildKJar(File rootFolder, File outputFolder, String jarName);

    List<KBaseUnit> getKBaseUnits();

    KBaseUnit getKBaseUnit(String kBaseName);

    KnowledgeBase getKnowledgeBase(String kBaseName);

    StatefulKnowledgeSession getStatefulKnowlegeSession(String kSessionName);

    StatelessKnowledgeSession getStatelessKnowlegeSession(String kSessionName);

    void deploy(File... kJars);
}
