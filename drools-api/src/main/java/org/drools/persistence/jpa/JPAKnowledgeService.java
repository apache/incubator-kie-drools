package org.drools.persistence.jpa;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.KnowledgeBaseProvider;
import org.drools.ProviderInitializationException;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.util.ProviderLocator;

public class JPAKnowledgeService extends ProviderLocator {
    private static JPAKnowledgeServiceProvider provider;

    public static StatefulKnowledgeSession newStatefulKnowledgeSession(KnowledgeBase kbase,
                                                                       KnowledgeSessionConfiguration configuration,
                                                                       Environment environment) {
        return getJPAKnowledgeServiceProvider().newStatefulKnowledgeSession( kbase,
                                                                             configuration,
                                                                             environment );
    }

    public static StatefulKnowledgeSession loadStatefulKnowledgeSession(int id,
                                                                        KnowledgeBase kbase,
                                                                        KnowledgeSessionConfiguration configuration,
                                                                        Environment environment) {
        return getJPAKnowledgeServiceProvider().loadStatefulKnowledgeSession( id,
                                                                              kbase,
                                                                              configuration,
                                                                              environment );
    }

    private static synchronized void setJPAKnowledgeServiceProvider(JPAKnowledgeServiceProvider provider) {
        JPAKnowledgeService.provider = provider;
    }

    private static synchronized JPAKnowledgeServiceProvider getJPAKnowledgeServiceProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    @SuppressWarnings("unchecked")
    private static void loadProvider() {
        try {
            // we didn't find anything in properties so lets try and us reflection
            Class<JPAKnowledgeServiceProvider> cls = (Class<JPAKnowledgeServiceProvider>) Class.forName( "org.drools.persistence.jpa.impl.JPAKnowledgeServiceProviderImpl" );
            setJPAKnowledgeServiceProvider( cls.newInstance() );
        } catch ( Exception e ) {
            throw new ProviderInitializationException( "Provider org.drools.persistence.jpa.impl.JPAKnowledgeServiceProviderImpl could not be set.",
                                                       e );
        }
    }

}
