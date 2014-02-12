package org.drools.core.common;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.PropagationContext;

import java.io.Serializable;

public class PhreakPropagationContextFactory implements PropagationContextFactory, Serializable  {
    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint,
                                                       final long modificationMask,
                                                       final Class<?> modifiedClass,
                                                       final MarshallerReaderContext readerContext) {
        return new PhreakPropagationContext(number, type, rule, leftTuple, factHandle, entryPoint, modificationMask, modifiedClass, readerContext);
    }

    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint,
                                                       final MarshallerReaderContext readerContext) {
        return new PhreakPropagationContext(number, type, rule, leftTuple, factHandle, entryPoint, readerContext);
    }

    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final int activeActivations,
                                                       final int dormantActivations,
                                                       final EntryPointId entryPoint,
                                                       final long modificationMask) {
        return new PhreakPropagationContext(number, type, rule, leftTuple, factHandle, activeActivations, dormantActivations, entryPoint, modificationMask);
    }

    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint) {
        return new PhreakPropagationContext(number, type, rule, leftTuple, factHandle, entryPoint);
    }

    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle) {
        return new PhreakPropagationContext(number, type, rule, leftTuple, factHandle);
    }
}
