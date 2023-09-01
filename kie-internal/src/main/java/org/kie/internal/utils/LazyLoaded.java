package org.kie.internal.utils;

/**
 * Provides support for lazy load of content of given data object
 * e.g. process variable or case file data
 *
 * @param <T> type of service that is responsible for loading content
 */
public interface LazyLoaded<T> {

    /**
     * Should be set after object construction (usually in marshaling strategies) so whenever is needed 
     * content can be loaded via this service
     * @param service service implementation capable of loading the content
     */
    void setLoadService(T service);
    
    /**
     * Loads the actual content based on other attribute of the instance
     * using load service if given.
     */
    void load();
}
