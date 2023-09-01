package org.drools.ruleunits.impl.facthandles;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.DefaultFactHandle;
import org.drools.base.rule.EntryPointId;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.impl.InternalStoreCallback;

public class RuleUnitDefaultFactHandle extends DefaultFactHandle implements RuleUnitInternalFactHandle {

    public RuleUnitDefaultFactHandle() {
    }

    public RuleUnitDefaultFactHandle(long id, Object object) {
        super(id, object);
    }

    public RuleUnitDefaultFactHandle(long id, Object object, long recency, WorkingMemoryEntryPoint wmEntryPoint) {
        super(id, object, recency, wmEntryPoint);
    }

    public RuleUnitDefaultFactHandle(long id, int identityHashCode, Object object, long recency, WorkingMemoryEntryPoint wmEntryPoint) {
        super(id, identityHashCode, object, recency, wmEntryPoint);
    }

    public RuleUnitDefaultFactHandle(long id, int identityHashCode, Object object, long recency, EntryPointId entryPointId) {
        super(id, identityHashCode, object, recency, entryPointId);
    }

    public RuleUnitDefaultFactHandle(long id, String wmEntryPointId, int identityHashCode, int objectHashCode, long recency, Object object) {
        super(id, wmEntryPointId, identityHashCode, objectHashCode, recency, object);
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
