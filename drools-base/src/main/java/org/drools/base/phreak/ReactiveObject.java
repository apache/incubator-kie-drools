package org.drools.base.phreak;

import org.drools.base.reteoo.BaseTuple;

import java.util.Collection;

public interface ReactiveObject {
    void addTuple(BaseTuple tuple);
    void removeTuple(BaseTuple tuple);
    Collection<BaseTuple> getTuples();
}
