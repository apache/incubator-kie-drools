package org.drools.common;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;


import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.Activation;

public interface InternalWorkingMemoryEntryPoint extends WorkingMemoryEntryPoint {
    ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry();
    RuleBase getRuleBase();
    public void retract(final FactHandle factHandle,
                        final boolean removeLogical,
                        final boolean updateEqualsMap,
                        final Rule rule,
                        final Activation activation) throws FactException;
    public void update(org.drools.runtime.rule.FactHandle handle,
                       Object object,
                       Rule rule,
                       Activation activation) throws FactException;

    public EntryPoint getEntryPoint();
    public InternalWorkingMemory getInternalWorkingMemory();

    public FactHandle getFactHandleByIdentity(final Object object);
}
