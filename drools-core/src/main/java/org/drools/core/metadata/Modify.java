package org.drools.core.metadata;

import org.drools.core.metadata.Metadatable;
import org.drools.core.util.bitmask.BitMask;

public interface Modify<T extends Metadatable> extends WorkingMemoryTask<T> {

    public T getTarget();

    public T call( T o );

    public BitMask getModificationMask();

    public Class getModificationClass();

    public ModifyTask getSetterChain();

    public Object[] getAdditionalUpdates();

}
