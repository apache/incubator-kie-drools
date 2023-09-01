package org.drools.traits.core.factmodel;


import java.util.BitSet;

public interface TypeLattice<T> extends CodedHierarchy<T> {

    BitSet getTopCode();

    void setTopCode( BitSet code );

    BitSet getBottomCode();

    void setBottomCode( BitSet code );
}
