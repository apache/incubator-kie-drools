/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.marshalling.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.common.BaseNode;
import org.drools.common.InternalRuleBase;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.ObjectSink;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.RuleTerminalNode;

public class RuleBaseNodes {
    public static Map<Integer, BaseNode> getNodeMap(InternalRuleBase ruleBase) {        
        Map<Integer, BaseNode> nodes = new HashMap<Integer, BaseNode>();
        buildNodeMap( ruleBase, nodes );
        return nodes;
    }
    
    private static void buildNodeMap(InternalRuleBase ruleBase,
                                     Map<Integer, BaseNode> nodes) {
        for ( ObjectTypeNode sink : ruleBase.getRete().getObjectTypeNodes() ) {
            nodes.put( sink.getId(),
                       sink );
            addObjectSink( ruleBase,
                           sink,
                           nodes );
        }
    }

    private static void addObjectSink(InternalRuleBase ruleBase,
                                     ObjectSink sink,
                                     Map<Integer, BaseNode> nodes) {
        // we don't need to store alpha nodes, as they have no state to serialise
        if ( sink instanceof LeftTupleSource ) {
            LeftTupleSource node = (LeftTupleSource) sink;
            for ( LeftTupleSink leftTupleSink : node.getSinkPropagator().getSinks() ) {
                addLeftTupleSink( ruleBase,
                                  leftTupleSink,
                                  nodes );
            }
        } else {
            ObjectSource node = ( ObjectSource ) sink;
            for ( ObjectSink objectSink : node.getSinkPropagator().getSinks() ) {
                addObjectSink( ruleBase,
                               objectSink,
                               nodes );
            }
        }
    }

    private static void addLeftTupleSink(InternalRuleBase ruleBase,
                                        LeftTupleSink sink,
                                        Map<Integer, BaseNode> nodes) {
        if ( sink instanceof LeftTupleSource ) {
            nodes.put( sink.getId(),
                       (LeftTupleSource) sink );            
            for ( LeftTupleSink leftTupleSink : ((LeftTupleSource) sink).getSinkPropagator().getSinks() ) {                
                addLeftTupleSink( ruleBase,
                                  leftTupleSink,
                                  nodes );
            }
        } else if ( sink instanceof ObjectSource ) { 
            // it may be a RIAN
            nodes.put( sink.getId(), 
                       (ObjectSource) sink );
            for ( ObjectSink objectSink : ((ObjectSource)sink).getSinkPropagator().getSinks() ) {
                addObjectSink( ruleBase,
                               objectSink,
                               nodes );
            }
        } else if ( sink instanceof RuleTerminalNode ) {
            nodes.put( sink.getId(),
                       (RuleTerminalNode) sink );
        }
    }
}
