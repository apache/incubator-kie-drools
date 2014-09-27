package org.drools.core.metadata;

import org.drools.core.metadata.Metadatable;

public interface Modify<T extends Metadatable> extends WorkingMemoryTask<T> {

    public T getTarget();

    public T call( T o );

    public long getModificationMask();

    public Class getModificationClass();

    public ModifyTask getSetterChain();

}
