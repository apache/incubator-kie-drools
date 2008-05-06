/**
 * 
 */
package org.drools.persister;

import java.io.ObjectInputStream;
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

public class WMSerialisationInContext {
    public final ObjectInputStream                  stream;
    public final InternalRuleBase                   ruleBase;
    public InternalWorkingMemory              wm;
    public final Map<Integer, BaseNode>             sinks;

    public  Map<Integer, InternalFactHandle>        handles;
    
    public final Map<RightTupleKey, RightTuple>     rightTuples;
    public final Map<Integer, LeftTuple>            terminalTupleMap;

    public final PlaceholderResolverStrategyFactory resolverStrategyFactory;
    public final Map<String, EntryPoint>            entryPoints;

    public final Map<Long, PropagationContext>      propagationContexts;

    public WMSerialisationInContext(ObjectInputStream stream,
                                    InternalRuleBase ruleBase,
                                    Map<Integer, BaseNode> sinks,
                                    PlaceholderResolverStrategyFactory resolverStrategyFactory) {
        super();
        this.stream = stream;
        this.ruleBase = ruleBase;
        this.sinks = sinks;
        handles = new HashMap<Integer, InternalFactHandle>();
        this.rightTuples = new HashMap<RightTupleKey, RightTuple>();
        this.terminalTupleMap = new HashMap<Integer, LeftTuple>();
        this.entryPoints = new HashMap<String, EntryPoint>();
        this.propagationContexts = new HashMap<Long, PropagationContext>();
        this.resolverStrategyFactory = resolverStrategyFactory;
    }
}