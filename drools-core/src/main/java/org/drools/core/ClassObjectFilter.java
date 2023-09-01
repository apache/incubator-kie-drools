package org.drools.core;


/**
 * Filters Objects by Class, only accepting Classes of the specified type
 */
public class ClassObjectFilter extends  org.kie.api.runtime.ClassObjectFilter {

    public ClassObjectFilter(Class clazz) {
        super( clazz );
    }

    
}
