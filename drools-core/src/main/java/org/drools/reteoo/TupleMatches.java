package org.drools.reteoo;

import java.util.ArrayList;
import java.util.List;

class TupleMatches {

    private final ReteTuple tuple;
    private final List      matches;

    TupleMatches(ReteTuple tuple){
        super();
        this.tuple = tuple;
        this.matches = new ArrayList();
    }

    TupleKey getKey(){
        return this.tuple.getKey();
    }

    ReteTuple getTuple(){
        return this.tuple;
    }

    List getMatches(){
        return this.matches;
    }

    void addMatch(FactHandleImpl handle){
        this.matches.add( handle );
    }

    void removeMatch(FactHandleImpl handle){
        this.matches.remove( handle );
    }

    boolean matched(FactHandleImpl handle){
        return this.matches.contains( handle );
    }
}
