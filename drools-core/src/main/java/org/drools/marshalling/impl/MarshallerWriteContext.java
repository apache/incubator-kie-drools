/**
 * 
 */
package org.drools.marshalling.impl;

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
    public final MarshallerWriteContext         stream;
    public final InternalRuleBase               ruleBase;
    public final InternalWorkingMemory          wm;
    public final Map<Integer, BaseNode>         sinks;

    public final PrintStream                    out = System.out;

    public final ObjectMarshallingStrategyStore objectMarshallingStrategyStore;

    public final Map<LeftTuple, Integer>        terminalTupleMap;

    public final boolean                        marshalProcessInstances;
    public final boolean                        marshalWorkItems;

    public MarshallerWriteContext(OutputStream stream,
                                  InternalRuleBase ruleBase,
                                  InternalWorkingMemory wm,
                                  Map<Integer, BaseNode> sinks,
                                  ObjectMarshallingStrategyStore resolverStrategyFactory) throws IOException {
        this( stream,
              ruleBase,
              wm,
              sinks,
              resolverStrategyFactory,
              true,
              true );
    }

    public MarshallerWriteContext(OutputStream stream,
                                  InternalRuleBase ruleBase,
                                  InternalWorkingMemory wm,
                                  Map<Integer, BaseNode> sinks,
                                  ObjectMarshallingStrategyStore resolverStrategyFactory,
                                  boolean marshalProcessInstances,
                                  boolean marshalWorkItems) throws IOException {
        super( stream );
        this.stream = this;
        this.ruleBase = ruleBase;
        this.wm = wm;
        this.sinks = sinks;

        this.objectMarshallingStrategyStore = resolverStrategyFactory;

        this.terminalTupleMap = new IdentityHashMap<LeftTuple, Integer>();

        this.marshalProcessInstances = marshalProcessInstances;
        this.marshalWorkItems = marshalWorkItems;
    }
}