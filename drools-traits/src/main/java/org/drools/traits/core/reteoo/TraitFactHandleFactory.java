package org.drools.traits.core.reteoo;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.DefaultFactHandle;
import org.drools.base.reteoo.InitialFactImpl;
import org.drools.core.reteoo.ReteooFactHandleFactory;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.traits.core.common.TraitDefaultFactHandle;

public class TraitFactHandleFactory extends ReteooFactHandleFactory {

    @Override
    public DefaultFactHandle newInitialFactHandle(WorkingMemoryEntryPoint wmEntryPoint) {
        return new TraitDefaultFactHandle(0, InitialFactImpl.getInstance(), 0, wmEntryPoint);
    }

    @Override
    public FactHandleFactory newInstance() {
        return new TraitFactHandleFactory();
    }

    @Override
    public DefaultFactHandle createDefaultFactHandle(long id, Object object, long recency, WorkingMemoryEntryPoint entryPoint) {
        return new TraitDefaultFactHandle(id, object, recency, entryPoint);
    }
}
