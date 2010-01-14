package org.drools;

import org.drools.util.internal.ServiceRegistryImpl;

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
        setSystemEventListenerProvider( ServiceRegistryImpl.getInstance().get( SystemEventListenerProvider.class ) );
    }
}
