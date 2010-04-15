/**
 * 
 */
package org.drools.marshalling.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;

import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.rule.EntryPoint;
import org.drools.spi.PropagationContext;

public class MarshallerReaderContext extends ObjectInputStream {
    public final MarshallerReaderContext            stream;
    public final InternalRuleBase                   ruleBase;
    public InternalWorkingMemory                    wm;
    public final Map<Integer, BaseNode>             sinks;

    public Map<Integer, InternalFactHandle>         handles;

    public final Map<RightTupleKey, RightTuple>     rightTuples;
    public final Map<Integer, LeftTuple>            terminalTupleMap;

    public final ObjectMarshallingStrategyStore resolverStrategyFactory;
    public final Map<String, EntryPoint>            entryPoints;

    public final Map<Long, PropagationContext>      propagationContexts;

    public final boolean                            marshalProcessInstances;
    public final boolean                            marshalWorkItems;

    public MarshallerReaderContext(InputStream stream,
                                   InternalRuleBase ruleBase,
                                   Map<Integer, BaseNode> sinks,
                                   ObjectMarshallingStrategyStore resolverStrategyFactory) throws IOException {
        this( stream,
              ruleBase,
              sinks,
              resolverStrategyFactory,
              true,
              true );
    }

    public MarshallerReaderContext(InputStream stream,
                                   InternalRuleBase ruleBase,
                                   Map<Integer, BaseNode> sinks,
                                   ObjectMarshallingStrategyStore resolverStrategyFactory,
                                   boolean marshalProcessInstances,
                                   boolean marshalWorkItems) throws IOException {
        super( stream );
        this.stream = this;
        this.ruleBase = ruleBase;
        this.sinks = sinks;
        this.handles = new HashMap<Integer, InternalFactHandle>();
        this.rightTuples = new HashMap<RightTupleKey, RightTuple>();
        this.terminalTupleMap = new HashMap<Integer, LeftTuple>();
        this.entryPoints = new HashMap<String, EntryPoint>();
        this.propagationContexts = new HashMap<Long, PropagationContext>();
        this.resolverStrategyFactory = resolverStrategyFactory;
        this.marshalProcessInstances = marshalProcessInstances;
        this.marshalWorkItems = marshalWorkItems;
    }
    
    @Override
    protected Class< ? > resolveClass(ObjectStreamClass desc) throws IOException,
                                                             ClassNotFoundException {
        String name = desc.getName();
        try {
            return Class.forName(name, false, (this.ruleBase == null)?null:this.ruleBase.getRootClassLoader());
        } catch (ClassNotFoundException ex) {
            return super.resolveClass( desc );
        }        
    }
}