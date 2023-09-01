package org.drools.core.phreak;

import java.util.HashSet;
import java.util.Set;

public class ReactiveSet<T> extends ReactiveCollection<T, Set<T>> implements Set<T> {

    public ReactiveSet() {
        super(new HashSet<>());
    }
    
    public ReactiveSet(Set<T> wrapped) {
        super(wrapped);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReactiveSet[").append(wrapped).append("]");
        return builder.toString();
    }

}
