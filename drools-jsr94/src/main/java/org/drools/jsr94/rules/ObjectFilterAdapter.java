package org.drools.jsr94.rules;

import javax.rules.ObjectFilter;

public class ObjectFilterAdapter implements org.drools.ObjectFilter {
    private ObjectFilter filter;
    
    public ObjectFilterAdapter(ObjectFilter filter) {
        this.filter = filter;
    }

    public boolean accept(Object object) {
        return ( this.filter == null || this.filter.filter( object ) != null );
    }
}
