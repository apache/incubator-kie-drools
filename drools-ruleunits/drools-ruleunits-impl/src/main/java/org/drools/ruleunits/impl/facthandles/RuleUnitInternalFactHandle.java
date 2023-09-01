package org.drools.ruleunits.impl.facthandles;

import org.drools.core.common.InternalFactHandle;
import org.drools.ruleunits.impl.InternalStoreCallback;
import org.drools.ruleunits.api.DataHandle;

public interface RuleUnitInternalFactHandle
        extends
        InternalFactHandle {

    DataHandle getDataHandle();

    void setDataHandle(DataHandle dataHandle);

    InternalStoreCallback getDataStore();

    void setDataStore(InternalStoreCallback dataStore);
}
