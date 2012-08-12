package org.drools.kproject;

import org.drools.KnowledgeBase;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;

public interface KProjectTestClass {
    public KnowledgeBase getKBase1();
    public KnowledgeBase getKBase2();
    
    public StatelessKnowledgeSession getKBase1Ksession1();
    
    public StatefulKnowledgeSession getKBase1Ksession2();
    
    public StatefulKnowledgeSession getKBase2Ksession3();
}
