package org.drools.util.proxy;

import java.beans.PropertyChangeListener;

/**
 * This is implemented by the proxy, optionally, to provide
 * automatic notifications of changes to facts.
 * 
 * All proxies implement this, however it is NOT active unless the correct params
 * were set when the proxy was created. This this CAN NOT be used to test
 * if an object has property change support or not.
 * 
 * @author Michael Neale
 */
public interface ChangeListener {

    public void addPropertyChangeListener( PropertyChangeListener l );
    
    public void removePropertyChangeListener( PropertyChangeListener l );
}
