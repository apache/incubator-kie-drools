package com.sample.benchmark.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Peter Lin
 *
 */
public class SubTransaction extends Security {

	protected String[] transactionSet = null;
    protected ArrayList listeners = new ArrayList();

    public SubTransaction() {
		super();
	}

    public void setTransactionSet(String[] ids) {
    	if (ids != this.transactionSet) {
    		String[] old = this.transactionSet;
    		this.transactionSet = ids;
    		this.notifyListener("transactionSet", old, this.transactionSet);
    	}
    }
    
    public String[] getTransactionSet() {
    	return this.transactionSet;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener){
        this.listeners.add(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener){
        this.listeners.remove(listener);
    }
    
    protected void notifyListener(String field, Object oldValue, Object newValue){
        if (listeners == null || listeners.size() == 0) {
			return;
		} else {
			PropertyChangeEvent event = new PropertyChangeEvent(this, field,
					oldValue, newValue);

			for (int i = 0; i < listeners.size(); i++) {
				((java.beans.PropertyChangeListener) listeners.get(i))
						.propertyChange(event);
			}
		}
        
    }
}
