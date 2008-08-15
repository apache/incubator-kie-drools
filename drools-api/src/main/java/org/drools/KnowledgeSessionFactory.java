package org.drools;

public class KnowledgeSessionFactory {
    private static KnowledgeSessionProvider provider;
    
    public static void setKnowledgeSessionProvider(KnowledgeSessionProvider provider) {
        KnowledgeSessionFactory.provider = provider;
    }
    
    public static StatefulKnowledgeSession newStatefulKnowledgeSession() {
        return provider.newStatefulKnowledgeSession();
    }
}
