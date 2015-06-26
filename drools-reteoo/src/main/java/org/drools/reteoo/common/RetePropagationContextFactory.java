package org.drools.reteoo.common;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.bitmask.BitMask;

import java.io.Serializable;

public class RetePropagationContextFactory implements PropagationContextFactory, Serializable  {

    private static final PropagationContextFactory INSTANCE = new RetePropagationContextFactory();

    public static PropagationContextFactory getInstance() {
        return INSTANCE;
    }

    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint,
                                                       final BitMask modificationMask,
                                                       final Class<?> modifiedClass,
                                                       final MarshallerReaderContext readerContext) {
        return new RetePropagationContext(number, type, rule, leftTuple, factHandle, entryPoint, modificationMask, modifiedClass, readerContext);
    }

    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint,
                                                       final MarshallerReaderContext readerContext) {
        return new RetePropagationContext(number, type, rule, leftTuple, factHandle, entryPoint, readerContext);
    }

    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final int activeActivations,
                                                       final int dormantActivations,
                                                       final EntryPointId entryPoint,
                                                       final BitMask modificationMask) {
        return new RetePropagationContext(number, type, rule, leftTuple, factHandle, activeActivations, dormantActivations, entryPoint, modificationMask);
    }

    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint) {
        return new RetePropagationContext(number, type, rule, leftTuple, factHandle, entryPoint);
    }

    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle) {
        return new RetePropagationContext(number, type, rule, leftTuple, factHandle);
    }
}
