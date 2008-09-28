package org.drools.builder;

import org.drools.KnowledgeSessionProvider;
import org.drools.ProviderInitializationException;
import org.drools.util.ChainedProperties;

public class KnowledgeBuilderFactory {
    private static KnowledgeBuilderProvider provider;
    
    public static void setKnowledgeBuilderProvider(KnowledgeBuilderProvider provider) {
        KnowledgeBuilderFactory.provider = provider;
    }
    
    public static KnowledgeBuilder newKnowledgeBuilder() {
    	if ( provider == null ) {
    		loadProvider();
    	}
        return provider.newKnowledgeBuilder();
    }
    
	private static void loadProvider() {
        try {
            // we didn't find anything in properties so lets try and us reflection
            Class<KnowledgeBuilderProvider> cls = ( Class<KnowledgeBuilderProvider> ) Class.forName( "org.drools.builder.impl.KnowledgeBuilderProviderImpl" );
            setKnowledgeBuilderProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider was not set and the Factory was unable to load a provider from properties, nor could reflection find org.drools.builder.impl.KnowledgeBuilderProviderImpl.", e2 );
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
