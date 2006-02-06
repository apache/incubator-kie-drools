package org.drools.util.proxy;

/** 
 * Shadow proxies provide this 
 * so that a field can be updated if a change is detected.
 * 
 * @author Michael Neale
 *
 */
public interface ShadowUpdater {

    public void refreshShadowCopies();
    
}
