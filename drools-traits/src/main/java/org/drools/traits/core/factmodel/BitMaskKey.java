package org.drools.traits.core.factmodel;

import java.util.BitSet;

import org.drools.base.factmodel.traits.TraitType;

public class BitMaskKey<T> extends Key<T> implements LatticeElement<T> {

    public BitMaskKey(int id, T value) {
        super(id, value);
    }

    public BitSet getBitMask() {
        return ((TraitType) getValue())._getTypeCode();
    }

}