package org.drools.core.common;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.PropagationContext;

public interface PropagationContextFactory {

    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint,
                                                       final long modificationMask,
                                                       final Class<?> modifiedClass,
                                                       final MarshallerReaderContext readerContext);

    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint,
                                                       final MarshallerReaderContext readerContext);

    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final int activeActivations,
                                                       final int dormantActivations,
                                                       final EntryPointId entryPoint,
                                                       final long modificationMask);

    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint);

    public PropagationContext createPropagationContext(final long number,
                                                       final int type,
                                                       final RuleImpl rule,
                                                       final LeftTuple leftTuple,
                                                       final InternalFactHandle factHandle);

}