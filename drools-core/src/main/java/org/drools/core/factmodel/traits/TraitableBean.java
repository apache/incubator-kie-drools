package org.drools.core.factmodel.traits;

import java.util.BitSet;
import java.util.Collection;

public interface TraitableBean<K, X extends TraitableBean> {

    BitSet getCurrentTypeCode();

    boolean hasTrait(String type);

    Collection<Thing<K>> removeTrait(String type);

    Collection<Thing<K>> removeTrait(BitSet typeCode);

    // TODO this is duplicated in drools-traits/TraitableBean
    String MAP_FIELD_NAME = "__$$dynamic_properties_map$$";
    String TRAITSET_FIELD_NAME = "__$$dynamic_traits_map$$";
    String FIELDTMS_FIELD_NAME = "__$$field_Tms$$";

}
