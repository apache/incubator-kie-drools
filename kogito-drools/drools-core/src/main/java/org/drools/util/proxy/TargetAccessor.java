package org.drools.util.proxy;

/**
 * All proxies implement this to allow access to the underlying object.
 * @author Michael Neale
 */
public interface TargetAccessor {

    /**
     * This will return the target pojo that has been proxied.
     */
    Object getTarget();
    
}
