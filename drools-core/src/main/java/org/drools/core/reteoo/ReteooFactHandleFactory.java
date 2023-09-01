package org.drools.core.reteoo;

import java.io.Serializable;

import org.drools.base.reteoo.InitialFactImpl;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.AbstractFactHandleFactory;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.rule.accessor.FactHandleFactory;

public class ReteooFactHandleFactory extends AbstractFactHandleFactory implements Serializable {

    private static final long serialVersionUID = 510l;

    public ReteooFactHandleFactory() {
        super();
    }

    public ReteooFactHandleFactory(long id, long counter) {
        super( id, counter );
    }

    @Override
    public DefaultFactHandle newInitialFactHandle(WorkingMemoryEntryPoint wmEntryPoint) {
        return new DefaultFactHandle(0, InitialFactImpl.getInstance(), 0, wmEntryPoint);
    }

    public FactHandleFactory newInstance() {
        return new ReteooFactHandleFactory();
    }

    public FactHandleFactory newInstance(long id, long counter) {
        return new ReteooFactHandleFactory( id, counter );
    }

    public Class getFactHandleType() {
        return DefaultFactHandle.class;
    }
}
