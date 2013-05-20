/*
 * Copyright 2005 JBoss Inc
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

package org.drools.core.reteoo;

import org.drools.core.base.SalienceInteger;
import org.drools.core.common.BaseNode;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.marshalling.impl.ProtobufMessages.NodeMemory;
import org.drools.core.phreak.AddRemoveRule;
import org.drools.core.rule.InvalidPatternException;
import org.drools.core.rule.Rule;
import org.drools.core.rule.WindowDeclaration;
import org.drools.core.spi.Salience;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.drools.core.reteoo.builder.ReteooRuleBuilder;

/**
 * Builds the Rete-OO network for a <code>Package</code>.
 *
 */
public class ReteooBuilder
    implements
    Externalizable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long           serialVersionUID = 510l;

    /** The RuleBase */
    private transient InternalRuleBase  ruleBase;

    private Map<String, BaseNode[]>       rules;
    
    private Map<String, WindowNode>     namedWindows;

    private transient RuleBuilder       ruleBuilder;

    private IdGenerator                 idGenerator;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    public ReteooBuilder() {

    }

    /**
     * Construct a <code>Builder</code> against an existing <code>Rete</code>
     * network.
     */
    public ReteooBuilder( final InternalRuleBase ruleBase ) {
        this.ruleBase = ruleBase;
        this.rules = new HashMap<String, BaseNode[]>();
        this.namedWindows = new HashMap<String, WindowNode>();

        //Set to 1 as Rete node is set to 0
        this.idGenerator = new IdGenerator( 1 );
        this.ruleBuilder = ruleBase.getConfiguration().getComponentFactory().getRuleBuilderFactory().newRuleBuilder();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Add a <code>Rule</code> to the network.
     *
     * @param rule
     *            The rule to add.
     *
     * @throws org.drools.core.RuleIntegrationException
     *             if an error prevents complete construction of the network for
     *             the <code>Rule</code>.
     * @throws InvalidPatternException
     */
    public synchronized void addRule(final Rule rule) throws InvalidPatternException {
        final List<TerminalNode> terminals = this.ruleBuilder.addRule( rule,
                                                                       this.ruleBase,
                                                                       this.idGenerator );

        this.rules.put( rule.getName(),
                        terminals.toArray( new BaseNode[terminals.size()] ) );
    }
    
    public void addEntryPoint( String id ) {
        this.ruleBuilder.addEntryPoint( id,
                                        this.ruleBase,
                                        this.idGenerator );
    }

    public synchronized void addNamedWindow( WindowDeclaration window ) {
        final WindowNode wnode = this.ruleBuilder.addWindowNode( window,
                                                                 this.ruleBase,
                                                                 this.idGenerator );

        this.namedWindows.put( window.getName(),
                               wnode );
    }

    public WindowNode getWindowNode( String name ) {
        return this.namedWindows.get( name );
    }

    public IdGenerator getIdGenerator() {
        return this.idGenerator;
    }

    public synchronized BaseNode[] getTerminalNodes(final Rule rule) {
        return this.rules.get( rule.getName() );
    }

    public synchronized BaseNode[] getTerminalNodes(final String ruleName) {
        return this.rules.get( ruleName );
    }

    public synchronized Map<String, BaseNode[]> getTerminalNodes() {
        return this.rules;
    }
    
    public synchronized void removeRule(final Rule rule) {
        // reset working memories for potential propagation
        InternalWorkingMemory[] workingMemories = this.ruleBase.getWorkingMemories();

        final RuleRemovalContext context = new RuleRemovalContext( rule );
        context.setRuleBase( ruleBase );

        if (  ruleBase.getConfiguration().isPhreakEnabled() ) { // && !( ReteooRuleBuilder.unlinkingAllowedForRule( rule ) ) ) {
            context.setUnlinkEnabled( true);
        } else {
            context.setUnlinkEnabled( false );
        }
        
        final BaseNode[] nodes = this.rules.remove( rule.getName() );

        if ( this.ruleBase.getConfiguration().isPhreakEnabled() ) {
            for ( BaseNode node : nodes ) {
                AddRemoveRule.removeRule( (TerminalNode) node, workingMemories);
            }
        }

        for (BaseNode node : nodes) {
            NodeSet nodeSet = new NodeSet();
            node.collectAncestors(nodeSet);

            List<BaseNode> removingNodes = nodeSet.getNodes();
            Collections.sort(removingNodes, new Comparator<BaseNode>() {
                private Map<Integer, NodeSet> ancestorsMap = new HashMap<Integer, NodeSet>();

                public int compare(BaseNode o1, BaseNode o2) {
                    return o2.getId() > o1.getId() ?
                            ( getAncestors(o1).contains(o2) ? -1 : 1 ) :
                            ( getAncestors(o2).contains(o1) ? 1 : -1 );
                }

                private NodeSet getAncestors(BaseNode o2) {
                    NodeSet ancestors = ancestorsMap.get(o2.getId());
                    if (ancestors == null) {
                        ancestors = new NodeSet();
                        o2.collectAncestors(ancestors);
                        ancestorsMap.put(o2.getId(), ancestors);
                    }
                    return ancestors;
                }
            });

            RuleRemovalContext.CleanupAdapter adapter = null;
            if (node instanceof RuleTerminalNode) {
                adapter = context.getCleanupAdapter();
                context.setCleanupAdapter( new RuleTerminalNode.RTNCleanupAdapter( (RuleTerminalNode) node ) );
            }

            for (BaseNode removingNode : removingNodes) {
                removingNode.remove(context, this, workingMemories);

                if ( removingNode.getType() != NodeTypeEnums.ObjectTypeNode &&
                     !removingNode.isInUse() && ruleBase.getConfiguration().isPhreakEnabled() ) {
                    for (InternalWorkingMemory workingMemory : workingMemories) {
                        workingMemory.clearNodeMemory( (MemoryFactory) removingNode);
                    }
                }
            }

            if (node instanceof TerminalNode) {
                for ( InternalWorkingMemory workingMemory : workingMemories ) {
                    workingMemory.executeQueuedActions();
                    workingMemory.clearNodeMemory( (MemoryFactory) node);
                }
                context.setCleanupAdapter(adapter);
            }
        }
        resetMasks(context);
    }
    
    /**
     * Rule removal now keeps a list of all the visited nodes.
     * We iterate each of those to find the nodes still in use, who's inferred mask is now stale.
     * For each node we find the tip, which is either BetaNode or RuleTerminalNode and re initialise
     * the inferred mask, which will trickle back up to the OTN
     * 
     */
    public void resetMasks(RuleRemovalContext context) {
        List<BaseNode> nodes = context.getRemovedNodes();
        NodeSet leafSet = new NodeSet();
        
        for ( BaseNode node : nodes ) {
            if ( node.isInUse() ) {
                if ( node.getType() == NodeTypeEnums.AlphaNode ) {
                    updateLeafSet(node, leafSet );
                } else if( NodeTypeEnums.isBetaNode( node ) ) {
                    BetaNode betaNode = ( BetaNode ) node;
                    if ( betaNode.isInUse() ) {
                        leafSet.add( betaNode );
                    }
                } else if ( NodeTypeEnums.isTerminalNode( node )  ) {
                    RuleTerminalNode rtNode = ( RuleTerminalNode ) node;
                    if ( rtNode.isInUse() ) {
                        leafSet.add( rtNode );
                    }                    
                }
            }
        }
        
        for ( BaseNode node : leafSet ) {
            if ( NodeTypeEnums.isTerminalNode( node ) ) {
                ((TerminalNode)node).initInferredMask();
            } else { // else node instanceof BetaNode
                ((BetaNode)node).initInferredMask();
            }
        }
    }
    
    private void updateLeafSet(BaseNode baseNode, NodeSet leafSet) {
        if ( baseNode.getType() == NodeTypeEnums.AlphaNode ) {
            ((AlphaNode) baseNode).resetInferredMask();
            for ( ObjectSink sink : ((AlphaNode) baseNode).getSinkPropagator().getSinks() ) {
                if ( ((BaseNode)sink).isInUse() ) {
                    updateLeafSet( ( BaseNode ) sink, leafSet );
                }
            }
        } else  if ( baseNode.getType() ==  NodeTypeEnums.LeftInputAdapterNode ) {
            for ( LeftTupleSink sink : ((LeftInputAdapterNode) baseNode).getSinkPropagator().getSinks() ) {
                if ( sink.getType() ==  NodeTypeEnums.RuleTerminalNode ) {
                    leafSet.add( (BaseNode) sink );
                } else if ( ((BaseNode)sink).isInUse() ) {
                    updateLeafSet( ( BaseNode ) sink, leafSet );
                }
            }
        } else if ( baseNode.getType() == NodeTypeEnums.EvalConditionNode ) {
            for ( LeftTupleSink sink : ((EvalConditionNode) baseNode).getSinkPropagator().getSinks() ) {
                if ( ((BaseNode)sink).isInUse() ) { 
                    updateLeafSet( ( BaseNode ) sink, leafSet );
                }
            }
        } else if ( NodeTypeEnums.isBetaNode( baseNode ) ) {
            if ( ((BaseNode)baseNode).isInUse() ) {
                leafSet.add( baseNode );
            }
        }
    }

    public static class IdGenerator
        implements
        Externalizable {

        private static final long serialVersionUID = 510l;

        private Queue<Integer>    recycledIds;
        private int               nextId;

        public IdGenerator() {
        }

        public IdGenerator(final int firstId) {
            this.nextId = firstId;
            this.recycledIds = new LinkedList<Integer>();
        }

        @SuppressWarnings("unchecked")
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            recycledIds = (Queue<Integer>) in.readObject();
            nextId = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( recycledIds );
            out.writeInt( nextId );
        }

        public synchronized int getNextId() {
            Integer id = this.recycledIds.poll();
            return ( id == null ) ? this.nextId++ : id;
        }

        public synchronized void releaseId(int id) {
            this.recycledIds.add( id );
        }

        public int getLastId() {
            return this.nextId - 1;
        }

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        boolean isDrools = out instanceof DroolsObjectOutputStream;
        DroolsObjectOutputStream droolsStream;
        ByteArrayOutputStream bytes;

        if ( isDrools ) {
            bytes = null;
            droolsStream = (DroolsObjectOutputStream) out;
        } else {
            bytes = new ByteArrayOutputStream();
            droolsStream = new DroolsObjectOutputStream( bytes );
        }
        droolsStream.writeObject( rules );
        droolsStream.writeObject( namedWindows );
        droolsStream.writeObject( idGenerator );
        if ( !isDrools ) {
            droolsStream.flush();
            droolsStream.close();
            bytes.close();
            out.writeInt( bytes.size() );
            out.writeObject( bytes.toByteArray() );
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        boolean isDrools = in instanceof DroolsObjectInputStream;
        DroolsObjectInputStream droolsStream;
        ByteArrayInputStream bytes;

        if ( isDrools ) {
            bytes = null;
            droolsStream = (DroolsObjectInputStream) in;
        } else {
            bytes = new ByteArrayInputStream( (byte[]) in.readObject() );
            droolsStream = new DroolsObjectInputStream( bytes );
        }
        
        this.rules = (Map<String, BaseNode[]>) droolsStream.readObject();
        this.namedWindows = (Map<String, WindowNode>) droolsStream.readObject();
        this.idGenerator = (IdGenerator) droolsStream.readObject();
        if ( !isDrools ) {
            droolsStream.close();
            bytes.close();
        }

    }

    public void setRuleBase( ReteooRuleBase reteooRuleBase ) {
        this.ruleBase = reteooRuleBase;

        this.ruleBuilder = ruleBase.getConfiguration().getComponentFactory().getRuleBuilderFactory().newRuleBuilder();
    }

}
