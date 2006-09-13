package org.drools.reteoo;

public interface TupleMatchFactory {
    public TupleMatch newTupleMatch(ReteTuple tuple, ObjectMatches objectMatches);
}
