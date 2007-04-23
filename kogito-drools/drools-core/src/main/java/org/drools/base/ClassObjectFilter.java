package org.drools.base;

import org.drools.ObjectFilter;

public class ClassObjectFilter implements ObjectFilter {
    private Class clazz;
    
    public ClassObjectFilter(Class clazz) {
        this.clazz = clazz;
    }

    public boolean accept(Object object) {
        return object.getClass().isAssignableFrom( this.clazz );
    }
    
}
