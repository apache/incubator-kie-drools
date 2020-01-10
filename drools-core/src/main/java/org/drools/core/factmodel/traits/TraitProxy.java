package org.drools.core.factmodel.traits;

import java.util.BitSet;

public interface TraitProxy {
    public abstract TraitableBean getObject();

    public BitSet _getTypeCode();

}
