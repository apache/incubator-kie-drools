package org.drools.compiler.compiler;

import java.io.InputStream;

import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.utils.ServiceRegistryImpl;

public class DecisionTableFactory {

    private static final String PROVIDER_CLASS = "org.drools.decisiontable.DecisionTableProviderImpl";

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
        ServiceRegistryImpl.getInstance().addDefault( DecisionTableProvider.class, PROVIDER_CLASS );
        setDecisionTableProvider(ServiceRegistryImpl.getInstance().get( DecisionTableProvider.class ) );
    }

    public static synchronized void loadProvider(ClassLoader cl) {
        if (provider == null) {
            try {
                provider = (DecisionTableProvider)Class.forName(PROVIDER_CLASS, true, cl).newInstance();
            } catch (Exception e) { }
        }
    }
}
