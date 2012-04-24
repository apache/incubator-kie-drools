package org.drools.core.util;


public interface TripleFactory {
    
    public Triple newTriple( Object subject, String predicate, Object object );
    
    public Triple newTriple( Object subject, Object predicate, Object object);
}
