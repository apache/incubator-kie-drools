package org.drools.jsr94.rules;

import javax.rules.ObjectFilter;

/**
 * Adaptor class, that makes JSR94 ObjectFilters work from a delegating Drools ObjectFilter
 * @author mproctor
 *
 */
public class ObjectFilterAdapter implements org.drools.runtime.ObjectFilter {
    private ObjectFilter filter;
    
    public ObjectFilterAdapter(ObjectFilter filter) {
        this.filter = filter;
    }

    public boolean accept(Object object) {
        return ( this.filter == null || this.filter.filter( object ) != null );
    }
}
