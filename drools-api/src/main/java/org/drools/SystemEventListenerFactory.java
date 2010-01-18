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
    private static SystemEventListenerService service;

    /**
     * Set the SystemEventListener
     * 
     * @param listener
     */
    public static void setSystemEventListener(SystemEventListener listener) {
        getSystemEventListenerService().setSystemEventListener( listener );
    }

    /**
     * Get the SystemEventListener
     * @return
     */
    public static SystemEventListener getSystemEventListener() {
        return getSystemEventListenerService().getSystemEventListener();
    }

    private static synchronized void setSystemEventListenerService(SystemEventListenerService service) {
        SystemEventListenerFactory.service = service;
    }

    private static synchronized SystemEventListenerService getSystemEventListenerService() {
        if ( service == null ) {
            loadService();
        }
        return service;
    }

    private static void loadService() {
        setSystemEventListenerService( ServiceRegistryImpl.getInstance().get( SystemEventListenerService.class ) );
    }
}
