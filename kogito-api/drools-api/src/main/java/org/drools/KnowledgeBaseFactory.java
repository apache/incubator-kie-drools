package org.drools;

import org.drools.util.ChainedProperties;

public class KnowledgeBaseFactory {
    private static KnowledgeBaseProvider provider;
    
    public static void setKnowledgeBaseProvider(KnowledgeBaseProvider provider) {
        KnowledgeBaseFactory.provider = provider;
    }
    
    public static KnowledgeBase newKnowledgeBase() {
        if ( provider == null ) {
            loadProvider();
        }        
        return provider.newKnowledgeBase();
    }
    
    @SuppressWarnings("unchecked")
	private static void loadProvider() {
        try {
            ChainedProperties properties = new ChainedProperties( "drools-providers.conf" );
            String className = properties.getProperty( "KnowledgeSessionProvider", null );
            if ( className != null && className.trim().length() > 0 ) {
                Class<KnowledgeBaseProvider> cls = ( Class<KnowledgeBaseProvider> ) Class.forName( className );
                setKnowledgeBaseProvider( cls.newInstance() );
            }
        } catch ( Exception e1 ) {
            try {
                // we didn't find anything in properties so lets try and us reflection
                Class<KnowledgeBaseProvider> cls = ( Class<KnowledgeBaseProvider> ) Class.forName( "org.drools.KnowledgeBaseProviderImpl" );
                setKnowledgeBaseProvider( cls.newInstance() );
            } catch ( Exception e2 ) {
                throw new ProviderInitializationException( "Provider was not set and the Factory was unable to load a provider from properties, nor could reflection find org.drools.KnowledgeBaseProviderImpl." );
            }
        }
    }    
}
