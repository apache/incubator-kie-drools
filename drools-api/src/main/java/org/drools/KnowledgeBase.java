package org.drools;

import org.drools.knowledge.definitions.KnowledgePackage;

public interface KnowledgeBase {
    void addKnowledgePackage(KnowledgePackage knowledgePackage);
}
