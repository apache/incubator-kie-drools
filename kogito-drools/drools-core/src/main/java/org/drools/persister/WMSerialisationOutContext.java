/**
 * 
 */
package org.drools.persister;

import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.drools.common.BaseNode;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;

public class WMSerialisationOutContext {
    public final ObjectOutputStream                 stream;
    public final InternalRuleBase                   ruleBase;
    public final InternalWorkingMemory              wm;
    public final Map<Integer, BaseNode>             sinks;

    public final PlaceholderResolverStrategyFactory resolverStrategyFactory;
//    public final Placeholders                       placeholders;

    public final Map<LeftTuple, Integer>                  terminalTupleMap;

    public WMSerialisationOutContext(ObjectOutputStream stream,
                                     InternalRuleBase ruleBase,
                                     InternalWorkingMemory wm,
                                     Map<Integer, BaseNode> sinks,
                                     PlaceholderResolverStrategyFactory resolverStrategyFactory) {
                                     //Placeholders placeholders) {
        super();
        this.stream = stream;
        this.ruleBase = ruleBase;
        this.wm = wm;
        this.sinks = sinks;

        this.resolverStrategyFactory = resolverStrategyFactory;
//        this.placeholders = placeholders;
        
        this.terminalTupleMap = new HashMap<LeftTuple, Integer>();
    }
}