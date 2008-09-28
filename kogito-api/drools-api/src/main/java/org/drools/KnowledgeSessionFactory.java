package org.drools;

import org.drools.util.ChainedProperties;

public class KnowledgeSessionFactory {
    private static KnowledgeSessionProvider provider;
    
    public static void setKnowledgeSessionProvider(KnowledgeSessionProvider provider) {
        KnowledgeSessionFactory.provider = provider;
    }
    
    public static StatefulKnowledgeSession newStatefulKnowledgeSession() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider.newStatefulKnowledgeSession();
    }
    
    	
	private static void loadProvider() {
        try {
            ChainedProperties properties = new ChainedProperties( "drools-providers.conf" );
            String className = properties.getProperty( "KnowledgeSessionProvider", null );
            if ( className != null && className.trim().length() > 0 ) {
                Class<KnowledgeSessionProvider> cls = ( Class<KnowledgeSessionProvider> ) Class.forName( className );
                setKnowledgeSessionProvider( cls.newInstance() );
            }
        } catch ( Exception e1 ) {
            try {
                // we didn't find anything in properties so lets try and us reflection
                Class<KnowledgeSessionProvider> cls = ( Class<KnowledgeSessionProvider> ) Class.forName( "org.drools.KnowledgeSessionProviderImpl" );
                setKnowledgeSessionProvider( cls.newInstance() );
            } catch ( Exception e2 ) {
                throw new ProviderInitializationException( "Provider was not set and the Factory was unable to load a provider from properties, nor could reflection find org.drools.KnowledgeSessionProviderImpl." );
            }
        }
    }
}
