package org.drools.core.common;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.reteoo.TerminalNode;
import org.drools.base.rule.EntryPointId;
import org.drools.core.util.bitmask.BitMask;

public interface PropagationContextFactory {

    PropagationContext createPropagationContext(long number,
                                                PropagationContext.Type type,
                                                RuleImpl rule,
                                                TerminalNode terminalNode,
                                                InternalFactHandle factHandle,
                                                EntryPointId entryPoint,
                                                BitMask modificationMask,
                                                Class<?> modifiedClass,
                                                MarshallerReaderContext readerContext);

    PropagationContext createPropagationContext(long number,
                                                PropagationContext.Type type,
                                                RuleImpl rule,
                                                TerminalNode terminalNode,
                                                InternalFactHandle factHandle,
                                                EntryPointId entryPoint,
                                                MarshallerReaderContext readerContext);

    PropagationContext createPropagationContext(long number,
                                                PropagationContext.Type type,
                                                RuleImpl rule,
                                                TerminalNode terminalNode,
                                                InternalFactHandle factHandle,
                                                EntryPointId entryPoint);

    PropagationContext createPropagationContext(long number,
                                                PropagationContext.Type type,
                                                RuleImpl rule,
                                                TerminalNode terminalNode,
                                                InternalFactHandle factHandle);

}