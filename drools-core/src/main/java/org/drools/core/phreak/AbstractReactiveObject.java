package org.drools.core.phreak;

import org.drools.base.phreak.ReactiveObject;
import org.drools.base.reteoo.BaseTuple;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class AbstractReactiveObject implements ReactiveObject {

    private Collection<BaseTuple> tuples;

    public void addTuple(BaseTuple tuple) {
        if (tuples == null) {
            tuples = new HashSet<>();
        }
        tuples.add(tuple);
    }

    public Collection<BaseTuple> getTuples() {
        return tuples != null ? tuples : Collections.emptyList();
    }

    protected void notifyModification() {
        ReactiveObjectUtil.notifyModification(this);
    }

    @Override
    public void removeTuple(BaseTuple tuple) {
        tuples.remove(tuple);
    }
}
