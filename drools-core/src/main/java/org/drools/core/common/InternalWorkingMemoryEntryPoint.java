package org.drools.core.common;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.base.TraitHelper;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;

public interface InternalWorkingMemoryEntryPoint extends WorkingMemoryEntryPoint {

    TraitHelper getTraitHelper();

    PropagationContextFactory getPctxFactory();

    void insert(InternalFactHandle handle);

    FactHandle insert( Object object,
                       boolean dynamic,
                       RuleImpl rule,
                       TerminalNode terminalNode );

    void insert(InternalFactHandle handle,
                Object object,
                RuleImpl rule,
                TerminalNode terminalNode,
                ObjectTypeConf typeConf );

    void insert(InternalFactHandle handle,
                Object object,
                RuleImpl rule,
                ObjectTypeConf typeConf,
                PropagationContext pctx );

    FactHandle insertAsync(Object object);

    InternalFactHandle update(InternalFactHandle handle,
                              Object object,
                              BitMask mask,
                              Class<?> modifiedClass,
                              InternalMatch internalMatch);

    void update(InternalFactHandle handle,
                Object object,
                Object originalObject,
                ObjectTypeConf typeConf,
                PropagationContext propagationContext);

    PropagationContext delete(InternalFactHandle handle,
                              Object object,
                              ObjectTypeConf typeConf,
                              RuleImpl rule,
                              TerminalNode terminalNode);

    PropagationContext immediateDelete(InternalFactHandle handle,
                                       Object object,
                                       ObjectTypeConf typeConf,
                                       RuleImpl rule,
                                       TerminalNode terminalNode);

    void removeFromObjectStore(InternalFactHandle handle);
}
