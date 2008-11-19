package org.drools;

import java.util.Properties;


public class KnowledgeBaseFactory {
    private static KnowledgeBaseProvider provider;
    
    public static KnowledgeBase newKnowledgeBase() {      
        return getsetKnowledgeBaseProvider().newKnowledgeBase();
    }
    
    public static KnowledgeBase newKnowledgeBase(KnowledgeBaseConfiguration conf) {
   
        return getsetKnowledgeBaseProvider().newKnowledgeBase(conf);        
    }
    
    public static KnowledgeBaseConfiguration newKnowledgBaseConfiguration() {
        return getsetKnowledgeBaseProvider().newKnowledgeBaseConfiguration();
    }
    
    public static KnowledgeBaseConfiguration newKnowledgBaseConfiguration(Properties properties, ClassLoader classLoader) {
        return getsetKnowledgeBaseProvider().newKnowledgeBaseConfiguration( properties, classLoader );
    }    
    
    public static void setKnowledgeBaseProvider(KnowledgeBaseProvider provider) {
        KnowledgeBaseFactory.provider = provider;
    }    
    
    public static synchronized KnowledgeBaseProvider getsetKnowledgeBaseProvider() {
        if ( provider == null ) {
            loadProvider();
        }     
        return provider;
    }
    
	private static void loadProvider() {
        try {
            // we didn't find anything in properties so lets try and us reflection
            Class<KnowledgeBaseProvider> cls = ( Class<KnowledgeBaseProvider> ) Class.forName( "org.drools.impl.KnowledgeBaseProviderImpl" );
            setKnowledgeBaseProvider( cls.newInstance() );
        } catch ( Exception e ) {
            throw new ProviderInitializationException( "Provider org.drools.impl.KnowledgeBaseProviderImpl could not be set." );
        }        
    }    
}
