package org.drools.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.Environment;
import org.drools.runtime.Globals;

public class EnvironmentImpl implements Environment {

	private Map<String, Object> environment = new HashMap<String, Object>();
	
    private Environment delegate;
    
    public void setDelegate(Environment delegate) {
        this.delegate = delegate;
    }       
	
	public Object get(String identifier) {
	    Object object = environment.get(identifier);
	    if ( object == null && delegate != null ) {
	        object = this.delegate.get( identifier );
	    }
		return object;
	}

	public void set(String name, Object object) {
		environment.put(name, object);
	}

}
