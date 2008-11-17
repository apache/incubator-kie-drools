package org.drools;


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
    
    public static KnowledgeBase newKnowledgeBase(KnowledgeBaseConfiguration conf) {
        if ( provider == null ) {
            loadProvider();
        }        
        return provider.newKnowledgeBase(conf);        
    }
    
    public static KnowledgeBaseConfiguration newKnowledgeBaseConfiguration() {
        if ( provider == null ) {
            loadProvider();
        }        
        return provider.newKnowledgeBaseConfiguration();        
    }
    
	private static void loadProvider() {
        try {
            // we didn't find anything in properties so lets try and us reflection
            Class<KnowledgeBaseProvider> cls = ( Class<KnowledgeBaseProvider> ) Class.forName( "org.drools.impl.KnowledgeBaseProviderImpl" );
            setKnowledgeBaseProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.impl.KnowledgeBaseProviderImpl could not be set." );
        }
        
//        try {
//            ChainedProperties properties = new ChainedProperties( "drools-providers.conf" );
//            String className = properties.getProperty( "KnowledgeSessionProvider", null );
//            if ( className != null && className.trim().length() > 0 ) {
//                Class<KnowledgeBaseProvider> cls = ( Class<KnowledgeBaseProvider> ) Class.forName( className );
//                setKnowledgeBaseProvider( cls.newInstance() );
//            }
//        } catch ( Exception e1 ) {
//
//        }
    }    
}
