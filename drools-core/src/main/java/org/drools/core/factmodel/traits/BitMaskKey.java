package org.drools.core.factmodel.traits;

import java.util.BitSet;

public class BitMaskKey<T> extends Key<T> implements LatticeElement<T> {

    public BitMaskKey(int id, T value) {
        super(id, value);
    }

    public BitSet getBitMask() {
        return ((TraitType) getValue())._getTypeCode();
    }

}