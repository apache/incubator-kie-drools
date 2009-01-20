package org.drools;

/**
 * This factory allows you to set the SystemEventListener that will be used by various components of Drools, such
 * as the KnowledgeAgent, ResourceChangeNotifier and ResourceChangeListener.
 * 
 * The default SystemEventListener
 *
 */
public class SystemEventListenerFactory {
    private static SystemEventListenerProvider provider;

    /**
     * Set the SystemEventListener
     * 
     * @param listener
     */
    public static void setSystemEventListener(SystemEventListener listener) {
        getSystemEventListenerProvider().setSystemEventListener( listener );
    }

    /**
     * Get the SystemEventListener
     * @return
     */
    public static SystemEventListener getSystemEventListener() {
        return getSystemEventListenerProvider().getSystemEventListener();
    }

    private static synchronized void setSystemEventListenerProvider(SystemEventListenerProvider provider) {
        SystemEventListenerFactory.provider = provider;
    }

    private static synchronized SystemEventListenerProvider getSystemEventListenerProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        try {
            // we didn't find anything in properties so lets try and us reflection
            Class<SystemEventListenerProvider> cls = (Class<SystemEventListenerProvider>) Class.forName( "org.drools.impl.SystemEventListenerProviderImpl" );
            setSystemEventListenerProvider( cls.newInstance() );
        } catch ( Exception e ) {
            throw new ProviderInitializationException( "Provider org.drools.impl.SystemEventListenerProviderImpl could not be set.",
                                                       e );
        }
    }
}
