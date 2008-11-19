package org.drools.builder;

import java.util.Properties;

import org.drools.ProviderInitializationException;

public class KnowledgeBuilderFactory {
    private static volatile KnowledgeBuilderProvider provider;
    
    public static KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        return getKnowledgeBuilderProvider().newKnowledgeBuilderConfiguration();        
    }
    
    public static KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader classLoader) {
        return getKnowledgeBuilderProvider().newKnowledgeBuilderConfiguration( properties, 
                                                          classLoader );         
    }
    
    public static DecisionTableConfiguration newDecisionTableConfiguration() {
        return getKnowledgeBuilderProvider().newDecisionTableConfiguration();        
    }
    
    public static KnowledgeBuilder newKnowledgeBuilder() {
        return getKnowledgeBuilderProvider().newKnowledgeBuilder();
    }
    
    public static KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
        return getKnowledgeBuilderProvider().newKnowledgeBuilder(conf);        
    }
    
    public static void setKnowledgeBuilderProvider(KnowledgeBuilderProvider provider) {
        KnowledgeBuilderFactory.provider = provider;
    }    
    
    public static synchronized KnowledgeBuilderProvider getKnowledgeBuilderProvider() {
        if ( provider == null ) {
            loadProvider();
        }     
        return provider;
    }    
    
	private static void loadProvider() {
        try {
            Class<KnowledgeBuilderProvider> cls = ( Class<KnowledgeBuilderProvider> ) Class.forName( "org.drools.builder.impl.KnowledgeBuilderProviderImpl" );
            setKnowledgeBuilderProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.builder.impl.KnowledgeBuilderProviderImpl could not be set.", e2 );
        }
    }    
}
