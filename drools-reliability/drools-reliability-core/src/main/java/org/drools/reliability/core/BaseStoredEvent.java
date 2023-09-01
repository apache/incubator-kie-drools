package org.drools.reliability.core;

import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.rule.accessor.FactHandleFactory;

public abstract class BaseStoredEvent extends BaseStoredObject implements StoredEvent {

    protected final long timestamp;
    protected final long duration;

    protected BaseStoredEvent(boolean propagated, long timestamp, long duration) {
        super(propagated);
        this.timestamp = timestamp;
        this.duration = duration;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public long repropagate(InternalWorkingMemoryEntryPoint ep) {
        FactHandleFactory fhFactory = ep.getHandleFactory();
        DefaultEventHandle eFh = fhFactory.createEventFactHandle(fhFactory.getNextId(), getObject(), fhFactory.getNextRecency(), ep, timestamp, duration);
        ep.insert(eFh);
        return eFh.getId();
    }
}