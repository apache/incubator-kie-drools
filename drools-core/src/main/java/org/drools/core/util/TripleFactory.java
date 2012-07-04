package org.drools.core.util;


import java.io.Serializable;

public interface TripleFactory extends Serializable {
    
    public Triple newTriple( Object subject, String predicate, Object object );
    
    public Triple newTriple( Object subject, Object predicate, Object object);
}
