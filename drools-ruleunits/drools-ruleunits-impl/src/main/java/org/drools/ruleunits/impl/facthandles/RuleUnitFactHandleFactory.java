package org.drools.ruleunits.impl.facthandles;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.DefaultEventHandle;
import org.drools.core.reteoo.ReteooFactHandleFactory;
import org.drools.core.rule.accessor.FactHandleFactory;

public class RuleUnitFactHandleFactory extends ReteooFactHandleFactory {

    public RuleUnitFactHandleFactory() {
        super();
    }

    public RuleUnitFactHandleFactory(long id, long counter) {
        super(id, counter);
    }

    @Override
    public DefaultFactHandle createDefaultFactHandle(long id, Object object, long recency, WorkingMemoryEntryPoint entryPoint) {
        return new RuleUnitDefaultFactHandle(id, object, recency, entryPoint);
    }

    @Override
    public DefaultEventHandle createEventFactHandle(long id, Object object, long recency, WorkingMemoryEntryPoint entryPoint, long timestamp, long duration) {
        return new RuleUnitEventFactHandle(id, object, recency, timestamp, duration, entryPoint);
    }

    @Override
    public FactHandleFactory newInstance() {
        return new RuleUnitFactHandleFactory();
    }

    @Override
    public FactHandleFactory newInstance(long id, long counter) {
        return new RuleUnitFactHandleFactory(id, counter);
    }
}
