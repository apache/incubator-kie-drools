package org.drools.traits.core.factmodel;


public class TripleFactoryImpl implements TripleFactory {

    static final TripleFactory INSTANCE = new TripleFactoryImpl();

    public Triple newTriple( Object subject, String predicate, Object object ) {
        return new TripleImpl( subject, predicate, object );
    }

    public Triple newTriple( Object subject, Object predicate, Object object ) {
        return new TripleImpl( subject, predicate, object );
    }

}
