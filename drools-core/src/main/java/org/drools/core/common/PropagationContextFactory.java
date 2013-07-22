package org.drools.core.common;

import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.EntryPoint;
import org.drools.core.rule.Rule;

public interface PropagationContextFactory {

    public PropagationContextImpl createPropagationContextImpl(final long number,
                                                               final int type,
                                                               final Rule rule,
                                                               final LeftTuple leftTuple,
                                                               final InternalFactHandle factHandle,
                                                               final EntryPoint entryPoint,
                                                               final long modificationMask,
                                                               final Class<?> modifiedClass,
                                                               final MarshallerReaderContext readerContext);

    public PropagationContextImpl createPropagationContextImpl(final long number,
                                                               final int type,
                                                               final Rule rule,
                                                               final LeftTuple leftTuple,
                                                               final InternalFactHandle factHandle,
                                                               final EntryPoint entryPoint,
                                                               final MarshallerReaderContext readerContext);

    public PropagationContextImpl createPropagationContextImpl(final long number,
                                                               final int type,
                                                               final Rule rule,
                                                               final LeftTuple leftTuple,
                                                               final InternalFactHandle factHandle,
                                                               final int activeActivations,
                                                               final int dormantActivations,
                                                               final EntryPoint entryPoint,
                                                               final long modificationMask);

    public PropagationContextImpl createPropagationContextImpl(final long number,
                                                               final int type,
                                                               final Rule rule,
                                                               final LeftTuple leftTuple,
                                                               final InternalFactHandle factHandle,
                                                               final EntryPoint entryPoint);

    public PropagationContextImpl createPropagationContextImpl(final long number,
                                                               final int type,
                                                               final Rule rule,
                                                               final LeftTuple leftTuple,
                                                               final InternalFactHandle factHandle);

}