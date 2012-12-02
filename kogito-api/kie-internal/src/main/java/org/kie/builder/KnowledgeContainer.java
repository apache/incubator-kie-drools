package org.kie.builder;

import org.kie.KBaseUnit;
import org.kie.KnowledgeBase;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKnowledgeSession;

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
