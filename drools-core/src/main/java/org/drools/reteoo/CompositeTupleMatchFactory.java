package org.drools.reteoo;

public class CompositeTupleMatchFactory
    implements
    TupleMatchFactory {
    
    private static TupleMatchFactory INSTANCE;
    
    public static TupleMatchFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new CompositeTupleMatchFactory();
        }
        return INSTANCE;
    }
    
    private CompositeTupleMatchFactory() {
        
    }

    public TupleMatch newTupleMatch(ReteTuple tuple,
                                    ObjectMatches objectMatches) {
        return new CompositeTupleMatch(tuple, objectMatches);
    }

}
