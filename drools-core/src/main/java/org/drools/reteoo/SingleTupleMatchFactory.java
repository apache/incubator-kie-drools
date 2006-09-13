package org.drools.reteoo;

public class SingleTupleMatchFactory
    implements
    TupleMatchFactory {

    private static TupleMatchFactory INSTANCE;
    
    public static TupleMatchFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new SingleTupleMatchFactory();
        }
        return INSTANCE;
    }
    
    private SingleTupleMatchFactory() {
        
    }
    
    
    public TupleMatch newTupleMatch(ReteTuple tuple,
                                    ObjectMatches objectMatches) {
        return new CompositeTupleMatch(tuple, objectMatches);
    }

}
