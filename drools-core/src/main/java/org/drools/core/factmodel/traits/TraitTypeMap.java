package org.drools.core.factmodel.traits;

import java.util.BitSet;
import java.util.Collection;

public interface TraitTypeMap<T extends String, K extends Thing<C>, C> {

    K putSafe(String key, K value);

    Collection<K> removeCascade(String traitName);

    Collection<K> removeCascade(BitSet code);

    Collection<K> getMostSpecificTraits();

    BitSet getCurrentTypeCode();

    void setBottomCode(BitSet code);
}
