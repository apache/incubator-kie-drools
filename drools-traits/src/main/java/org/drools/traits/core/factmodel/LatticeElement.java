package org.drools.traits.core.factmodel;

import java.util.BitSet;

public interface LatticeElement<T> {

    T getValue();

    BitSet getBitMask();

}