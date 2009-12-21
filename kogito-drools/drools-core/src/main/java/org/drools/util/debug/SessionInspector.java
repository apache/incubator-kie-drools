/*
 * Copyright 2008 Red Hat
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
 *
 */
package org.drools.util.debug;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.drools.common.NetworkNode;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.AccumulateNode;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.CollectNode;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.EvalConditionNode;
import org.drools.reteoo.ExistsNode;
import org.drools.reteoo.FromNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.ObjectSink;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.PropagationQueuingNode;
import org.drools.reteoo.QueryTerminalNode;
import org.drools.reteoo.Rete;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.RightInputAdapterNode;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.Rule;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * @author etirelli
 *
 */
public class SessionInspector {

    private StatefulKnowledgeSession                               session;
    private Map<Class< ? extends NetworkNode>, NetworkNodeVisitor> visitors;

    public SessionInspector(StatefulKnowledgeSession session) {
        this.session = session;
        this.visitors = new HashMap<Class< ? extends NetworkNode>, NetworkNodeVisitor>();
        
        // terminal nodes
        this.visitors.put( RuleTerminalNode.class,
                           RuleTerminalNodeVisitor.INSTANCE );
        this.visitors.put( QueryTerminalNode.class,
                           QueryTerminalNodeVisitor.INSTANCE );
        
        // root node
        this.visitors.put( Rete.class,
                           DefaultNetworkNodeVisitor.INSTANCE );
        
        // object source nodes
        this.visitors.put( EntryPointNode.class,
                           DefaultNetworkNodeVisitor.INSTANCE );
        this.visitors.put( ObjectTypeNode.class,
                           ObjectTypeNodeVisitor.INSTANCE );
        this.visitors.put( AlphaNode.class,
                           AlphaNodeVisitor.INSTANCE );
        this.visitors.put( RightInputAdapterNode.class,
                           RightInputAdapterNodeVisitor.INSTANCE );
        this.visitors.put( PropagationQueuingNode.class,
                           PropagationQueueingNodeVisitor.INSTANCE );
        
        // left tuple source nodes
        this.visitors.put( JoinNode.class,
                           BetaNodeVisitor.INSTANCE );
        this.visitors.put( NotNode.class,
                           BetaNodeVisitor.INSTANCE );
        this.visitors.put( ExistsNode.class,
                           BetaNodeVisitor.INSTANCE );
        this.visitors.put( AccumulateNode.class,
                           AccumulateNodeVisitor.INSTANCE );
        this.visitors.put( CollectNode.class,
                           CollectNodeVisitor.INSTANCE );
        this.visitors.put( EvalConditionNode.class,
                           EvalConditionNodeVisitor.INSTANCE );
        this.visitors.put( FromNode.class,
                           FromNodeVisitor.INSTANCE );
        this.visitors.put( LeftInputAdapterNode.class,
                           LeftInputAdapterNodeVisitor.INSTANCE );
        
    }

    public StatefulKnowledgeSessionInfo getSessionInfo() {
        StatefulKnowledgeSessionInfo info = new StatefulKnowledgeSessionInfo();
        ReteooWorkingMemory wm = ((StatefulKnowledgeSessionImpl) this.session).session;
        ReteooRuleBase rulebase = (ReteooRuleBase) wm.getRuleBase();

        info.setSession( wm );

        Stack<NetworkNode> nodeStack = new Stack<NetworkNode>();
        gatherNodeInfo( rulebase.getRete(),
                        nodeStack,
                        info );

        return info;
    }

    private void gatherNodeInfo(NetworkNode parent,
                                Stack<NetworkNode> nodeStack,
                                StatefulKnowledgeSessionInfo info) {
        if ( !info.visited( parent ) ) {
            nodeStack.push( parent );
            NetworkNodeVisitor visitor = visitors.get( parent.getClass() );
            if ( visitor != null ) {
                visitor.visit( parent,
                               nodeStack,
                               info );
            } else {
                throw new RuntimeException( "No visitor found for node: " + parent );
            }
            visitChildren( parent,
                           nodeStack,
                           info );
            nodeStack.pop();
        } else {
            // if already visited, then assign the same rules to the nodes currently in the stack
            Set<Rule> rules = info.getNodeInfo( parent ).getRules();
            for ( NetworkNode snode : nodeStack ) {
                for ( Rule rule : rules ) {
                    info.assign( snode,
                                 rule );
                }
            }
        }
    }

    protected void visitChildren(NetworkNode parent,
                                 Stack<NetworkNode> nodeStack,
                                 StatefulKnowledgeSessionInfo info) {
        if ( parent instanceof Rete ) {
            Rete rete = (Rete) parent;
            for ( EntryPointNode sink : rete.getEntryPointNodes().values() ) {
                gatherNodeInfo( sink,
                                nodeStack,
                                info );
            }
        } else if ( parent instanceof EntryPointNode ) {
            EntryPointNode epn = (EntryPointNode) parent;
            for ( ObjectTypeNode sink : epn.getObjectTypeNodes().values() ) {
                gatherNodeInfo( sink,
                                nodeStack,
                                info );
            }
        } else if ( parent instanceof ObjectSource ) {
            ObjectSource source = (ObjectSource) parent;
            for ( ObjectSink sink : source.getSinkPropagator().getSinks() ) {
                gatherNodeInfo( sink,
                                nodeStack,
                                info );
            }
        } else if ( parent instanceof LeftTupleSource ) {
            LeftTupleSource source = (LeftTupleSource) parent;
            for ( LeftTupleSink sink : source.getSinkPropagator().getSinks() ) {
                gatherNodeInfo( sink,
                                nodeStack,
                                info );
            }
        } else if ( parent instanceof RuleTerminalNode || parent instanceof QueryTerminalNode ) {
            // no children to visit
        } else {
            // did we forget any node type?
            throw new RuntimeException( "ERROR: No idea how to visit childrens of this node: " + parent );
        }
    }
}
