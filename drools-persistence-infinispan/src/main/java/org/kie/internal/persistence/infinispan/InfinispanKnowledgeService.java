package org.kie.internal.persistence.infinispan;

import org.kie.api.KieBase;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class InfinispanKnowledgeService {
    private static KieStoreServices provider;

    public static StatefulKnowledgeSession newStatefulKnowledgeSession(KieBase kbase,
                                                                       KieSessionConfiguration configuration,
                                                                       Environment environment) {
        return (StatefulKnowledgeSession)getInfinispanKnowledgeServiceProvider().newKieSession(kbase,
                configuration,
                environment);
    }

    public static StatefulKnowledgeSession loadStatefulKnowledgeSession(int id,
                                                                        KieBase kbase,
                                                                        KieSessionConfiguration configuration,
                                                                        Environment environment) {
        return (StatefulKnowledgeSession)getInfinispanKnowledgeServiceProvider().loadKieSession(id,
                kbase,
                configuration,
                environment);
    }

    private static synchronized void setInfinispanKnowledgeServiceProvider(KieStoreServices provider) {
        InfinispanKnowledgeService.provider = provider;
    }

    private static synchronized KieStoreServices getInfinispanKnowledgeServiceProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    @SuppressWarnings("unchecked")
    private static void loadProvider() {
        try {
            // we didn't find anything in properties so lets try and us reflection
            Class<KieStoreServices> cls = (Class<KieStoreServices>) Class.forName( "org.drools.persistence.infinispan.KnowledgeStoreServiceImpl" );
            setInfinispanKnowledgeServiceProvider( cls.newInstance() );
        } catch ( Exception e ) {
            throw new RuntimeException( "Provider org.drools.persistence.infinispan.KnowledgeStoreServiceImpl could not be set.",
                                        e );
        }
    }


}
