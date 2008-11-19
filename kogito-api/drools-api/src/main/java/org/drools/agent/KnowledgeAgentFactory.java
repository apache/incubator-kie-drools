package org.drools.agent;

import java.util.Properties;

import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseProvider;
import org.drools.ProviderInitializationException;

public class KnowledgeAgentFactory {
    private static KnowledgeAgentProvider provider;
    
    public synchronized static void setKnowledgeAgentProvider(KnowledgeAgentProvider provider) {
        KnowledgeAgentFactory.provider = provider;
    }
    
    public static KnowledgeAgent newKnowledgeAgent(String name,
                                                   Properties config) {
        return newKnowledgeAgent( name,
                                  config,
                                  null,
                                  null );
    }

    /**
     * This allows an optional listener to be passed in.
     * The default one prints some stuff out to System.err only when really needed.
     */
    public static KnowledgeAgent newKnowledgeAgent(String name,
                                                   Properties config,
                                                   KnowledgeEventListener listener,
                                                   KnowledgeBaseConfiguration ruleBaseConf) {
       
        return getKnowledgeAgentProvider().newKnowledgeAgent(name, config, listener, ruleBaseConf);  
    }
    
    private static synchronized KnowledgeAgentProvider getKnowledgeAgentProvider() {
        if ( provider == null ) {
            loadProvider();
        } 
        return provider;
    }
    
    private static void loadProvider() {    
        try {
            // we didn't find anything in properties so lets try and us reflection
            Class<KnowledgeAgentProvider> cls = ( Class<KnowledgeAgentProvider> ) Class.forName( "org.drools.agent.KnowledgeAgentProviderImpl" );
            setKnowledgeAgentProvider( cls.newInstance() );
        } catch ( Exception e ) {
            throw new ProviderInitializationException( "Provider org.drools.agent.KnowledgeAgentProvider could not be set." );
        }  
    }
}
