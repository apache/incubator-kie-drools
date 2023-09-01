package org.drools.traits.core.factmodel;

import java.util.BitSet;
import java.util.Collection;

public interface HierarchyEncoder<T> extends CodedHierarchy<T> {

    BitSet encode( T name, Collection<T> parents );

    BitSet getBottom();

    void clear();
}
