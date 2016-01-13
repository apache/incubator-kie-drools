/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.common.BaseNode;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.phreak.AddRemoveRule;
import org.drools.core.rule.InvalidPatternException;
import org.drools.core.rule.WindowDeclaration;
import org.kie.api.definition.rule.Rule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
    private transient InternalKnowledgeBase  kBase;

    private Map<String, BaseNode[]>     rules;
    private Map<String, BaseNode[]>     queries;

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
    public ReteooBuilder( final InternalKnowledgeBase  kBase ) {
        this.kBase = kBase;
        this.rules = new HashMap<String, BaseNode[]>();
        this.queries = new HashMap<String, BaseNode[]>();
        this.namedWindows = new HashMap<String, WindowNode>();

        //Set to 1 as Rete node is set to 0
        this.idGenerator = new IdGenerator( 1 );
        this.ruleBuilder = kBase.getConfiguration().getComponentFactory().getRuleBuilderFactory().newRuleBuilder();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Add a <code>Rule</code> to the network.
     *
     * @param rule
     *            The rule to add.
     * @throws InvalidPatternException
     */
    public synchronized void addRule(final RuleImpl rule) throws InvalidPatternException {
        final List<TerminalNode> terminals = this.ruleBuilder.addRule( rule,
                                                                       this.kBase,
                                                                       this.idGenerator );

        BaseNode[] nodes = terminals.toArray( new BaseNode[terminals.size()] );
        this.rules.put( rule.getFullyQualifiedName(), nodes );
        if (rule.isQuery()) {
            this.queries.put( rule.getName(), nodes );
        }
    }

    public void addEntryPoint( String id ) {
        this.ruleBuilder.addEntryPoint( id,
                                        this.kBase,
                                        this.idGenerator );
    }

    public synchronized void addNamedWindow( WindowDeclaration window ) {
        final WindowNode wnode = this.ruleBuilder.addWindowNode( window,
                                                                 this.kBase,
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

    public synchronized BaseNode[] getTerminalNodes(final RuleImpl rule) {
        return getTerminalNodes( rule.getFullyQualifiedName() );
    }

    public synchronized BaseNode[] getTerminalNodes(final String ruleName) {
        return this.rules.get( ruleName );
    }

    public synchronized BaseNode[] getTerminalNodesForQuery(final String ruleName) {
        BaseNode[] nodes = this.queries.get( ruleName );
        return nodes != null ? nodes : getTerminalNodes(ruleName);
    }

    public synchronized Map<String, BaseNode[]> getTerminalNodes() {
        return this.rules;
    }

    public synchronized void removeRule(final RuleImpl rule) {
        // reset working memories for potential propagation
        InternalWorkingMemory[] workingMemories = this.kBase.getWorkingMemories();

        final RuleRemovalContext context = new RuleRemovalContext( rule );
        context.setKnowledgeBase(kBase);

        for (BaseNode node : rules.remove( rule.getFullyQualifiedName() )) {
            removeTerminalNode(context, (TerminalNode) node, workingMemories);
        }

        if (rule.isQuery()) {
            this.queries.remove( rule.getName() );
        }
    }

    public void removeTerminalNode(RuleRemovalContext context, TerminalNode tn, InternalWorkingMemory[] workingMemories)  {
        if ( this.kBase.getConfiguration().isPhreakEnabled() ) {
            AddRemoveRule.removeRule( tn, workingMemories, kBase );
        }

        RuleRemovalContext.CleanupAdapter adapter = null;
        if ( !this.kBase.getConfiguration().isPhreakEnabled() ) {
            if ( tn instanceof RuleTerminalNode) {
                adapter = new RuleTerminalNode.RTNCleanupAdapter( (RuleTerminalNode) tn );
            }
            context.setCleanupAdapter( adapter );
        }

        BaseNode node = (BaseNode) tn;
        removeNodeAssociation(node, context.getRule());

        Set<BaseNode> removedSources = new HashSet<BaseNode>();
        LinkedList<BaseNode> betaStack = new LinkedList<BaseNode>();
        LinkedList<BaseNode> alphaStack = new LinkedList<BaseNode>();
        LinkedList<BaseNode> stillInUse = new LinkedList<BaseNode>();

        // alpha and beta stacks must be separate
        // beta stacks processed first.
        boolean processRian = true;
        while ( node != null ) {
            removeNode(node, removedSources, alphaStack, betaStack, stillInUse, processRian, workingMemories, context);
            if ( !betaStack.isEmpty() ) {
                processRian = node.getType() == NodeTypeEnums.RightInputAdaterNode;
                node = betaStack.removeLast();
            } else if ( !alphaStack.isEmpty() ) {
                node = alphaStack.removeLast();
            } else {
                node = null;
            }
        }

        resetMasks(stillInUse);
    }

    private void removeNode(BaseNode node, Set<BaseNode> removedSources, LinkedList<BaseNode> alphaStack, LinkedList<BaseNode> betaStack, LinkedList<BaseNode> stillInUse, boolean processRian, InternalWorkingMemory[] workingMemories, RuleRemovalContext context )  {
        if ( !betaStack.isEmpty() && node == betaStack.getLast() ) {
            return;
        }

        if ( node.getType() == NodeTypeEnums.EntryPointNode ) {
            return;
        }

        if ( node.isInUse() ) {
            stillInUse.add(node);
        }

        if ( node.getType() != NodeTypeEnums.ObjectTypeNode &&
             node.getType() != NodeTypeEnums.AlphaNode &&
             !node.isInUse() && kBase.getConfiguration().isPhreakEnabled() ) {
            // phreak must clear node memories, although this should ideally be pushed into AddRemoveRule
            for (InternalWorkingMemory workingMemory : workingMemories) {
                workingMemory.clearNodeMemory( (MemoryFactory) node);
            }
        }

        if ( NodeTypeEnums.isBetaNode( node ) ) {
            BaseNode parent =  ((LeftTupleSink) node).getLeftTupleSource();
            node.remove(context, this, workingMemories);

            if ( !((BetaNode)node).isRightInputIsRiaNode() ) {
                // all right inputs need processing too
                alphaStack.addLast( ((BetaNode) node).getRightInput() );
            }

            if ( processRian && ((BetaNode)node).isRightInputIsRiaNode() ) {
                betaStack.addLast( ((BetaNode) node).getLeftTupleSource() );
                betaStack.addLast( ((BetaNode) node).getRightInput() );
            } else {
                removeNode( parent, removedSources, alphaStack, betaStack, stillInUse, true, workingMemories, context );
            }
        } else if ( NodeTypeEnums.isLeftTupleSink(node) ) {
            BaseNode parent =  ((LeftTupleSink) node).getLeftTupleSource();
            node.remove(context, this, workingMemories);
            removeNode( parent, removedSources, alphaStack, betaStack, stillInUse, true, workingMemories, context );
        } else if ( NodeTypeEnums.LeftInputAdapterNode == node.getType() ) {
            BaseNode parent =  ((LeftInputAdapterNode) node).getParentObjectSource();
            node.remove(context, this, workingMemories);
            removeNode( parent , removedSources, alphaStack, betaStack, stillInUse, true, workingMemories, context );
        } else if ( NodeTypeEnums.isObjectSource( node ) ) {
            if ( !removedSources.contains(node) ) {
                BaseNode parent = ((ObjectSource) node).getParentObjectSource();
                if (node.remove(context, this, workingMemories)) {
                    removedSources.add( node );
                }
                removeNode(parent, removedSources, alphaStack, betaStack, stillInUse, true, workingMemories, context);
            }
        } else {
            throw new IllegalStateException("Defensive exception, should not fall through");
        }
    }

    private void removeNodeAssociation(BaseNode node, Rule rule) {
        if (node == null || !node.removeAssociation( rule )) {
            return;
        }
        if (node instanceof LeftTupleNode) {
            removeNodeAssociation( ((LeftTupleNode)node).getLeftTupleSource(), rule );
        }
        if ( NodeTypeEnums.isBetaNode( node ) ) {
            removeNodeAssociation( ((BetaNode) node).getRightInput(), rule );
        } else if ( node.getType() == NodeTypeEnums.LeftInputAdapterNode ) {
            removeNodeAssociation( ((LeftInputAdapterNode) node).getObjectSource(), rule );
        } else if ( node.getType() == NodeTypeEnums.AlphaNode ) {
            removeNodeAssociation( ((AlphaNode) node).getParentObjectSource(), rule );
        }
    }

    public void resetMasks(List<BaseNode> nodes) {
        NodeSet leafSet = new NodeSet();

        for ( BaseNode node : nodes ) {
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
            for ( ObjectSink sink : ((AlphaNode) baseNode).getObjectSinkPropagator().getSinks() ) {
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
            if ( baseNode.isInUse() ) {
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
        droolsStream.writeObject( queries );
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
        this.queries = (Map<String, BaseNode[]>) droolsStream.readObject();
        this.namedWindows = (Map<String, WindowNode>) droolsStream.readObject();
        this.idGenerator = (IdGenerator) droolsStream.readObject();
        if ( !isDrools ) {
            droolsStream.close();
            bytes.close();
        }

    }

    public void setRuleBase( InternalKnowledgeBase kBase ) {
        this.kBase = kBase;

        this.ruleBuilder = kBase.getConfiguration().getComponentFactory().getRuleBuilderFactory().newRuleBuilder();
    }

}
