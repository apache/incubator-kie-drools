package org.drools.benchmark.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * @author Peter Lin
 *
 */
public class SubTransaction extends Security {

    private static final long serialVersionUID = 1L;
	
	protected String[]  transactionSet = null;
    protected ArrayList<PropertyChangeListener> listeners      = new ArrayList<PropertyChangeListener>();

    public SubTransaction() {
        super();
    }

    public void setTransactionSet(String[] ids) {
        if ( ids != this.transactionSet ) {
            String[] old = this.transactionSet;
            this.transactionSet = ids;
            this.notifyListener( "transactionSet",
                                 old,
                                 this.transactionSet );
        }
    }

    public String[] getTransactionSet() {
        return this.transactionSet;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.add( listener );
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.remove( listener );
    }

    protected void notifyListener(String field,
                                  Object oldValue,
                                  Object newValue) {
        if ( listeners == null || listeners.size() == 0 ) {
            return;
        } else {
            PropertyChangeEvent event = new PropertyChangeEvent( this,
                                                                 field,
                                                                 oldValue,
                                                                 newValue );

            for ( int i = 0; i < listeners.size(); i++ ) {
                ((java.beans.PropertyChangeListener) listeners.get( i )).propertyChange( event );
            }
        }

    }
}
