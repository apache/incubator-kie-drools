package org.drools.ruleunits.impl.facthandles;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.DefaultEventHandle;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.impl.InternalStoreCallback;

public class RuleUnitEventFactHandle extends DefaultEventHandle implements RuleUnitInternalFactHandle {
    public RuleUnitEventFactHandle() {
    }

    public RuleUnitEventFactHandle(long id, Object object, long recency, long timestamp, long duration, WorkingMemoryEntryPoint wmEntryPoint) {
        super(id, object, recency, timestamp, duration, wmEntryPoint);
    }

    private DataHandle dataHandle;
    private InternalStoreCallback dataStore;

    @Override
    public DataHandle getDataHandle() {
        return dataHandle;
    }

    @Override
    public void setDataHandle(DataHandle dataHandle) {
        this.dataHandle = dataHandle;
    }

    @Override
    public InternalStoreCallback getDataStore() {
        return dataStore;
    }

    @Override
    public void setDataStore(InternalStoreCallback dataStore) {
        this.dataStore = dataStore;
    }
}
