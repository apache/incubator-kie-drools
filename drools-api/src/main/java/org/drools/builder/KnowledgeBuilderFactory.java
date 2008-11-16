package org.drools.builder;

import java.util.Properties;

import org.drools.ProviderInitializationException;

public class KnowledgeBuilderFactory {
    private static volatile KnowledgeBuilderProvider provider;
    
    public static void setKnowledgeBuilderProvider(KnowledgeBuilderProvider provider) {
        KnowledgeBuilderFactory.provider = provider;
    }
    
    public static KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider.newKnowledgeBuilderConfiguration();        
    }
    
    public static KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader classLoader) {
        if ( provider == null ) {
            loadProvider();
        }
        return provider.newKnowledgeBuilderConfiguration( properties, 
                                                          classLoader );         
    }
    
    public static DecisionTableConfiguration newDecisionTableConfiguration() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider.newDecisionTableConfiguration();        
    }
    
    public static KnowledgeBuilder newKnowledgeBuilder() {
    	if ( provider == null ) {
    		loadProvider();
    	}
        return provider.newKnowledgeBuilder();
    }
    
    public static KnowledgeBuilder newKnowledgeBuilder(KnowledgeBuilderConfiguration conf) {
        if ( provider == null ) {
            loadProvider();
        }
        return provider.newKnowledgeBuilder(conf);        
    }
    
	private static void loadProvider() {
        try {
            Class<KnowledgeBuilderProvider> cls = ( Class<KnowledgeBuilderProvider> ) Class.forName( "org.drools.builder.impl.KnowledgeBuilderProviderImpl" );
            setKnowledgeBuilderProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.builder.impl.KnowledgeBuilderProviderImpl could not be set.", e2 );
        }
        
//        try {
//            ChainedProperties properties = new ChainedProperties( "drools-providers.conf" );
//            String className = properties.getProperty( "KnowledgeBuilderProvider", null );
//            if ( className != null && className.trim().length() > 0 ) {
//                Class<KnowledgeBuilderProvider> cls = ( Class<KnowledgeBuilderProvider> ) Class.forName( className );
//                setKnowledgeBuilderProvider( cls.newInstance() );
//            }
//        } catch ( Exception e1 ) {
//        }
    }    
}
