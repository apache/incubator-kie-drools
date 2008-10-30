package org.drools;

public interface KnowledgeBaseProvider {

    KnowledgeBase newKnowledgeBase();
    KnowledgeBase newKnowledgeBase(KnowledgeBaseConfiguration conf);
    
}
