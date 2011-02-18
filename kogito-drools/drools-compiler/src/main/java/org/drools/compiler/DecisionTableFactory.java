package org.drools.compiler;

import java.io.InputStream;
import java.io.Reader;

import org.drools.SystemEventListenerService;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.osgi.compiler.Activator;
import org.drools.util.ServiceRegistryImpl;

public class DecisionTableFactory {
    private static DecisionTableProvider provider;
    

    
    public static String loadFromInputStream(InputStream is, DecisionTableConfiguration configuration) {

        return getDecisionTableProvider().loadFromInputStream( is, configuration );
    }
    
    public static synchronized void setDecisionTableProvider(DecisionTableProvider provider) {
        DecisionTableFactory.provider = provider;
    }
    
    public static synchronized DecisionTableProvider getDecisionTableProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }
    
    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( DecisionTableProvider.class,  "org.drools.decisiontable.DecisionTableProviderImpl" );
        setDecisionTableProvider(ServiceRegistryImpl.getInstance().get( DecisionTableProvider.class ) );
    }
}
