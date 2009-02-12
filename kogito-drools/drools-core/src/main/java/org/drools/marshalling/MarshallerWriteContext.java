/**
 * 
 */
package org.drools.marshalling;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.common.BaseNode;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;

public class MarshallerWriteContext extends ObjectOutputStream {
    public final MarshallerWriteContext        stream;
    public final InternalRuleBase                 ruleBase;
    public final InternalWorkingMemory            wm;
    public final Map<Integer, BaseNode>           sinks;

    public final PrintStream                      out = System.out;
    
    public final PlaceholderResolverStrategyFactory resolverStrategyFactory;

    public final Map<LeftTuple, Integer>            terminalTupleMap;
    
    public final boolean                            marshalProcessInstances;
    public final boolean                            marshalWorkItems;
    public final boolean                            marshalTimers;    
    
    public MarshallerWriteContext(OutputStream stream,
                                  InternalRuleBase ruleBase,
                                  InternalWorkingMemory wm,
                                  Map<Integer, BaseNode> sinks,
                                  PlaceholderResolverStrategyFactory resolverStrategyFactory) throws IOException {
        this(stream, ruleBase, wm, sinks, resolverStrategyFactory, true, true, true);
    }

    public MarshallerWriteContext(OutputStream stream,
                                     InternalRuleBase ruleBase,
                                     InternalWorkingMemory wm,
                                     Map<Integer, BaseNode> sinks,
                                     PlaceholderResolverStrategyFactory resolverStrategyFactory,
                                     boolean marshalProcessInstances,
                                     boolean marshalWorkItems,
                                     boolean marshalTimers) throws IOException {
        super( stream );
        this.stream = this;
        this.ruleBase = ruleBase;
        this.wm = wm;
        this.sinks = sinks;        

        this.resolverStrategyFactory = resolverStrategyFactory;
        
        this.terminalTupleMap = new IdentityHashMap<LeftTuple, Integer>();
        
        this.marshalProcessInstances = marshalProcessInstances;
        this.marshalWorkItems = marshalWorkItems;
        this.marshalTimers = marshalTimers;        
    }
}