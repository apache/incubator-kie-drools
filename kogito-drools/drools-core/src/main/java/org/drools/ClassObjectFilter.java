package org.drools;


/**
 * Filters Objects by Class, only accepting Classes of the specified type
 * @author mproctor
 *
 */
public class ClassObjectFilter extends  org.drools.runtime.ClassObjectFilter {

    public ClassObjectFilter(Class clazz) {
        super( clazz );
    }

    
}
