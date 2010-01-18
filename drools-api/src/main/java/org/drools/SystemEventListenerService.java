package org.drools;

/**
 * KnowledgeBaseFactoryService is used by the KnowledgeBaseFacotry to "provide" it's concrete implementation.
 * 
 * This class is not considered stable and may change, the user is protected from this change by using 
 * the KnowledgeBaseFactory api, which is considered stable.
 *
 */
public interface SystemEventListenerService {

    /**
     * Set the SystemEventListener
     * 
     * @param listener
     */
    void setSystemEventListener(SystemEventListener listener);

    /**
     * Get the SystemEventListener
     * @return
     */
    SystemEventListener getSystemEventListener();

}
