package org.drools.core.util;


public class TripleFactoryImpl implements TripleFactory {

    public Triple newTriple( Object subject, String predicate, Object object ) {
        return new TripleImpl( subject, predicate, object );
    }

    public Triple newTriple( Object subject, Object predicate, Object object ) {
        return new TripleImpl( subject, predicate, object );
    }

}
