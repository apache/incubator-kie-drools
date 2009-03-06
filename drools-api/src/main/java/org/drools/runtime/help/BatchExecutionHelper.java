package org.drools.runtime.help;

import org.drools.ProviderInitializationException;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeBuilderProvider;
import org.milyn.Smooks;

import com.thoughtworks.xstream.XStream;

public class BatchExecutionHelper {
    private static volatile BatchExecutionHelperProvider provider;
    
    public static XStream newXStreamMarshaller() {
        return getBatchExecutionHelperProvider().newXStreamMarshaller();
    }
    
    private static synchronized void setBatchExecutionHelperProvider(BatchExecutionHelperProvider provider) {
        BatchExecutionHelper.provider = provider;
    }

    private static synchronized BatchExecutionHelperProvider getBatchExecutionHelperProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        try {
            Class<BatchExecutionHelperProvider> cls = (Class<BatchExecutionHelperProvider>) Class.forName( "org.drools.runtime.help.impl.BatchMessageHelperProviderImpl" );
            setBatchExecutionHelperProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.runtime.help.impl.BatchMessageHelperProviderImpl could not be set.",
                                                       e2 );
        }
    }    
}
