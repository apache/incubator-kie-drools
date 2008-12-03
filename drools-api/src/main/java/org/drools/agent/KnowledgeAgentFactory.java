package org.drools.agent;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.ProviderInitializationException;


public class KnowledgeAgentFactory {
    private static KnowledgeAgentProvider provider;

    public static KnowledgeAgent newKnowledgeAgent(String name,
                                                   KnowledgeBase kbase) {
        return getKnowledgeAgentProvider().newKnowledgeAgent( name, kbase );
    }
    
    public static KnowledgeAgent newKnowledgeAgent(String name,
                                                   KnowledgeBase kbase,
                                                   KnowledgeAgentConfiguration configuration) {
        return getKnowledgeAgentProvider().newKnowledgeAgent( name, kbase, configuration );
    }

    public static KnowledgeAgent newKnowledgeAgent(String name,
                                                   KnowledgeBase kbase,
                                                   KnowledgeAgentConfiguration configuration,
                                                   KnowledgeAgentEventListener listener) {

        return getKnowledgeAgentProvider().newKnowledgeAgent( name, kbase, configuration, listener );
    }

    private static synchronized void setKnowledgeAgentProvider(KnowledgeAgentProvider provider) {
        KnowledgeAgentFactory.provider = provider;
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
            Class<KnowledgeAgentProvider> cls = (Class<KnowledgeAgentProvider>) Class.forName( "org.drools.agent.impl.KnowledgeAgentProviderImpl" );
            setKnowledgeAgentProvider( cls.newInstance() );
        } catch ( Exception e ) {
            throw new ProviderInitializationException( "Provider org.drools.agent.KnowledgeAgentProvider could not be set." );
        }
    }
}
