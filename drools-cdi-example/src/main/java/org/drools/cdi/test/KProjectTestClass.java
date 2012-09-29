package org.drools.cdi.test;

import org.drools.KnowledgeBase;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;

public interface KProjectTestClass {
    public KnowledgeBase getKBase1();

    public KnowledgeBase getKBase2();

    public KnowledgeBase getKBase3();
    
    public StatelessKnowledgeSession getKBase1KSession1();
    
    public StatefulKnowledgeSession getKBase1KSession2();
    
    public StatefulKnowledgeSession getKBase2KSession3();

    public StatelessKnowledgeSession getKBase3KSession4();
}
