package org.drools.core.factmodel.traits;

import java.util.BitSet;
import java.util.Collection;

public interface TraitableBean<K, X extends TraitableBean> {

    BitSet getCurrentTypeCode();

    boolean hasTrait(String type);

    Collection<Thing<K>> removeTrait(String type);

    Collection<Thing<K>> removeTrait(BitSet typeCode);
}
