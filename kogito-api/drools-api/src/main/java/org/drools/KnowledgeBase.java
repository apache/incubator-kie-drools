package org.drools;

import java.util.Collection;

import org.drools.definition.KnowledgePackage;
import org.drools.event.knowledgebase.KnowledgeBaseEventManager;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;

public interface KnowledgeBase extends KnowledgeBaseEventManager {    
    
    void addKnowledgePackages(Collection<KnowledgePackage> knowledgePackage);
    
    Collection<KnowledgePackage> getKnowledgePackages();    
    
    void removeKnowledgePackage(String packageName);

    void removeRule(String packageName,
                    String ruleName);

    StatefulKnowledgeSession newStatefulSession(KnowledgeSessionConfiguration conf);
    
    StatefulKnowledgeSession newStatefulKnowledgeSession();
    
    
}
