package org.drools.core.phreak;

import java.util.HashSet;
import java.util.Set;

import org.drools.core.phreak.ReactiveObjectUtil.ModificationType;
import org.drools.core.spi.Tuple;

public class ReactiveSet<T> extends ReactiveCollection<T, Set<T>> implements Set<T> {

    public ReactiveSet() {
        super((Set<T>) new HashSet<T>());
    }
    
    public ReactiveSet(Set<T> wrapped) {
        super(wrapped);
    }

    @Override
    public boolean add(T t) {
        boolean result = wrapped.add(t);
        if (result) {
            ReactiveObjectUtil.notifyModification(t, getLeftTuples(), ModificationType.ADD);
            if (t instanceof ReactiveObject) {
                for (Tuple lts : getLeftTuples()) {
                    ((ReactiveObject) t).addLeftTuple(lts);
                }
            }
        }
        return result;
    }

}
