package org.drools.builder;

public class KnowledgeBuilderFactory {
    private static KnowledgeBuilderProvider provider;
    
    public static void setKnowledgeBuilderProvider(KnowledgeBuilderProvider provider) {
        KnowledgeBuilderFactory.provider = provider;
    }
    
    public static KnowledgeBuilder newKnowledgeBuilder() {
        return provider.newKnowledgeBuilder();
    }
}
