package org.drools.traits.core.metadata;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.util.bitmask.BitMask;

public interface Modify<T> extends WorkingMemoryTask<T> {

    public T getTarget();

    public T call( T o );

    public T call( InternalKnowledgeBase knowledgeBase);

    public BitMask getModificationMask();

    public Class getModificationClass();

    public ModifyTask getSetterChain();

    public Object[] getAdditionalUpdates();

    public BitMask getAdditionalUpdatesModificationMask( int j );
}
