package org.drools;

public class KnowledgeBaseFactory {
    private static KnowledgeBaseProvider provider;
    
    public static void setKnowledgeBaseProvider(KnowledgeBaseProvider provider) {
        KnowledgeBaseFactory.provider = provider;
    }
    
    public static KnowledgeBase newKnowledgeBase() {
        return provider.newKnowledgeBase();
    }
}
