package org.drools.traits.core.factmodel;

import java.util.BitSet;

import org.drools.base.factmodel.traits.TraitableBean;

public interface TraitProxy {
    TraitableBean getObject();

    BitSet _getTypeCode();

}
