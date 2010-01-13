package org.drools.compiler;

import java.io.InputStream;
import java.io.Reader;

import org.drools.ProviderInitializationException;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.osgi.compiler.Activator;

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
        try {
            // we didn't find anything in properties so lets try and us reflection
            //Class<DecisionTableProvider> cls = ( Class<DecisionTableProvider> ) Class.forName( "org.drools.decisiontable.DecisionTableProviderImpl" );            
            setDecisionTableProvider( ( DecisionTableProvider ) Activator.bc.getService( Activator.bc.getServiceReference( DecisionTableProvider.class.getName() ) ) );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.decisiontable.DecisionTableProviderImpl could not be set.", e2);
        }
    }       
}
