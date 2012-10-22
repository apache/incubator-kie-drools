package org.drools;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.runtime.StatefulKnowledgeSession;

public interface KBaseUnit {

    String getKBaseName();

    KnowledgeBase getKnowledgeBase();

    boolean hasErrors();

    KnowledgeBuilderErrors getErrors();

    StatefulKnowledgeSession newStatefulKnowledegSession(String ksessionName);
}
