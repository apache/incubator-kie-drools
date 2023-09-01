package org.drools.traits.core.factmodel;

import java.io.Serializable;
import java.util.BitSet;


public class TraitFieldDefaultValue implements LatticeElement<Object>, Serializable {

    private Object value;
    private BitSet bitMask;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public BitSet getBitMask() {
        return bitMask;
    }

    public void setBitMask(BitSet bitMask) {
        this.bitMask = bitMask;
    }

    public TraitFieldDefaultValue(Object value, BitSet bitMask) {
        this.value = value;
        this.bitMask = bitMask;
    }

    @Override
    public String toString() {
        return "TraitFieldDefaultValue{" +
               "value=" + value +
               ", bitMask=" + bitMask +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TraitFieldDefaultValue that = (TraitFieldDefaultValue) o;

        if (!bitMask.equals(that.bitMask)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return bitMask.hashCode();
    }
}
