package org.drools.base;

import org.drools.ObjectFilter;

/**
 * Filters Objects by Class, only accepting Classes of the specified type
 * @author mproctor
 *
 */
public class ClassObjectFilter implements ObjectFilter {
    private Class clazz;
    
    /** 
     * The Allowed Class type
     * @param clazz
     */
    public ClassObjectFilter(Class clazz) {
        this.clazz = clazz;
    }

    /**
     * Returning true means the Iterator accepts, and thus returns, the current Object's Class type.
     * @param object
     * @return
     */    
    public boolean accept(Object object) {
        return this.clazz.isAssignableFrom( object.getClass() );
    }
    
}
