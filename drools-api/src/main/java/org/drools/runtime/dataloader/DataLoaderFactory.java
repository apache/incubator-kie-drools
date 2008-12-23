package org.drools.runtime.dataloader;

import org.drools.ProviderInitializationException;
import org.drools.definition.pipeline.Receiver;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;

public class DataLoaderFactory {
    private static volatile DataLoaderProvider provider;

    public static StatefulKnowledgeSessionDataLoader newStatefulKnowledgeSessionDataLoader(StatefulKnowledgeSession ksession,
                                                                                           Receiver pipeline) {
        return getDataLoaderProvider().newStatefulKnowledgeSessionDataLoader( ksession, pipeline );
    }

    public static StatelessKnowledgeSessionDataLoader newStatelessKnowledgeSessionDataLoader(StatelessKnowledgeSession ksession,
                                                                                             Receiver pipeline) {
        return getDataLoaderProvider().newStatelessKnowledgeSessionDataLoader( ksession, pipeline );
    }
    
    private static synchronized void setDataLoaderProvider(DataLoaderProvider provider) {
        DataLoaderFactory.provider = provider;
    }

    private static synchronized DataLoaderProvider getDataLoaderProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return DataLoaderFactory.provider;
    }

    private static void loadProvider() {
        try {
            Class<DataLoaderProvider> cls = (Class<DataLoaderProvider>) Class.forName( "org.drools.runtime.dataloader.impl.DataLoaderProviderImpl" );
            setDataLoaderProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.runtime.dataloader.impl.DataLoaderProviderImpl could not be set.",
                                                       e2 );
        }
    }    
}
