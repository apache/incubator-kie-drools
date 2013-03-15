package org.drools.compiler.cdi.test;

import org.kie.KnowledgeBase;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKnowledgeSession;

public interface KProjectTestClass {
    public KnowledgeBase getKBase1();

    public KnowledgeBase getKBase2();

    public KnowledgeBase getKBase3();

    public StatelessKnowledgeSession getKBase1KSession1();

    public StatefulKnowledgeSession getKBase1KSession2();

    public StatefulKnowledgeSession getKBase2KSession3();

    public StatelessKnowledgeSession getKBase3KSession4();
}
