package org.drools.core.factmodel.traits;

import java.util.BitSet;

public interface LatticeElement<T> {

    public T getValue();

    public BitSet getBitMask();

}