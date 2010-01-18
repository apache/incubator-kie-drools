package org.drools;

import org.drools.util.ServiceRegistryImpl;

/**
 * This factory allows you to set the SystemEventListener that will be used by various components of Drools, such
 * as the KnowledgeAgent, ResourceChangeNotifier and ResourceChangeListener.
 * 
 * The default SystemEventListener
 *
 */
public class SystemEventListenerFactory {
    private static SystemEventListenerService provider;

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

    private static synchronized void setSystemEventListenerProvider(SystemEventListenerService provider) {
        SystemEventListenerFactory.provider = provider;
    }

    private static synchronized SystemEventListenerService getSystemEventListenerProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        setSystemEventListenerProvider( ServiceRegistryImpl.getInstance().get( SystemEventListenerService.class ) );
    }
}
