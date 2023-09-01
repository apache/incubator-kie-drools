package org.drools.ruleunits.impl;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.util.bitmask.BitMask;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.impl.facthandles.RuleUnitInternalFactHandle;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleContext;

public interface InternalStoreCallback {
    DataHandle lookup(Object object);

    void update(RuleUnitInternalFactHandle fh, Object obj, BitMask mask, Class<?> modifiedClass, InternalMatch internalMatch);
    void update(DataHandle dh, Object obj, BitMask mask, Class<?> modifiedClass, InternalMatch internalMatch);

    void delete(RuleUnitInternalFactHandle fh, RuleImpl rule, TerminalNode terminalNode, FactHandle.State fhState);

    void addLogical(RuleContext ruleContext, Object object);
}
