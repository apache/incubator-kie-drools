package org.drools.traits.core.factmodel;


import java.io.Serializable;

public interface TripleFactory extends Serializable {
    
    Triple newTriple( Object subject, String predicate, Object object );
    
    Triple newTriple( Object subject, Object predicate, Object object);
}
