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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.common.BaseNode;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NetworkNode;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.phreak.AddRemoveRule;
import org.drools.core.rule.InvalidPatternException;
import org.drools.core.rule.WindowDeclaration;
import org.kie.api.definition.rule.Rule;

import static org.drools.core.impl.StatefulKnowledgeSessionImpl.DEFAULT_RULE_UNIT;

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
        this.rules = new HashMap<>();
        this.queries = new HashMap<>();
        this.namedWindows = new HashMap<>();

        //Set to 1 as Rete node is set to 0
        this.idGenerator = new IdGenerator();
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
    public synchronized void addRule(final RuleImpl rule) {
        final List<TerminalNode> terminals = this.ruleBuilder.addRule( rule,
                                                                       this.kBase );

        BaseNode[] nodes = terminals.toArray( new BaseNode[terminals.size()] );
        this.rules.put( rule.getFullyQualifiedName(), nodes );
        if (rule.isQuery()) {
            this.queries.put( rule.getName(), nodes );
        }
    }

    public void addEntryPoint( String id ) {
        this.ruleBuilder.addEntryPoint( id,
                                        this.kBase );
    }

    public synchronized void addNamedWindow( WindowDeclaration window ) {
        final WindowNode wnode = this.ruleBuilder.addWindowNode( window,
                                                                 this.kBase );

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

    public synchronized void removeRules(Collection<RuleImpl> rulesToBeRemoved) {
        // reset working memories for potential propagation
        Collection<InternalWorkingMemory> workingMemories = this.kBase.getWorkingMemories();

        for (RuleImpl rule : rulesToBeRemoved) {
            if (rule.hasChildren() && !rulesToBeRemoved.containsAll( rule.getChildren() )) {
                throw new RuntimeException("Cannot remove parent rule " + rule + " without having removed all its chikdren");
            }

            final RuleRemovalContext context = new RuleRemovalContext( rule );
            context.setKnowledgeBase( kBase );

            BaseNode[] rulesTerminalNodes = rules.remove( rule.getFullyQualifiedName() );
            if (rulesTerminalNodes == null) {
                // there couldn't be any rule to be removed if it comes from a broken drl
                continue;
            }

            for ( BaseNode node : rulesTerminalNodes ) {
                removeTerminalNode( context, (TerminalNode) node, workingMemories );
            }

            if ( rule.isQuery() ) {
                this.queries.remove( rule.getName() );
            }

            if (rule.getParent() != null && !rulesToBeRemoved.contains( rule.getParent() )) {
                rule.getParent().removeChild( rule );
            }
        }
    }

    public void removeTerminalNode(RuleRemovalContext context, TerminalNode tn, Collection<InternalWorkingMemory> workingMemories)  {
        AddRemoveRule.removeRule( tn, workingMemories, kBase );

        BaseNode node = (BaseNode) tn;
        removeNodeAssociation(node, context.getRule(), new HashSet<>());

        resetMasks(removeNodes((AbstractTerminalNode)tn, workingMemories, context));
    }

    private Collection<BaseNode> removeNodes(AbstractTerminalNode terminalNode, Collection<InternalWorkingMemory> wms, RuleRemovalContext context) {
        Map<Integer, BaseNode> stillInUse = new HashMap<>();
        Collection<ObjectSource> alphas = new HashSet<>();

        removePath(wms, context, stillInUse, alphas, terminalNode);

        Set<Integer> removedNodes = new HashSet<>();
        for (ObjectSource alpha : alphas) {
            removeObjectSource( wms, stillInUse, removedNodes, alpha, context );
        }

        return stillInUse.values();
    }

    /**
     * Path's must be removed starting from the outer most path, iterating towards the inner most path.
     * Each time it reaches a subnetwork beta node, the current path evaluation ends, and instead the subnetwork
     * path continues.
     */
    private void removePath( Collection<InternalWorkingMemory> wms, RuleRemovalContext context, Map<Integer, BaseNode> stillInUse, Collection<ObjectSource> alphas, PathEndNode endNode ) {
        LeftTupleNode[] nodes = endNode.getPathNodes();
        for (int i = endNode.getPositionInPath(); i >= 0; i--) {
            BaseNode node = (BaseNode) nodes[i];

            boolean removed = false;
            if ( NodeTypeEnums.isLeftTupleNode( node ) ) {
                removed = removeLeftTupleNode(wms, context, stillInUse, node);
            }

            if ( removed ) {
                // reteoo requires to call remove on the OTN for tuples cleanup
                if (NodeTypeEnums.isBetaNode(node) && !((BetaNode) node).isRightInputIsRiaNode()) {
                    alphas.add(((BetaNode) node).getRightInput());
                } else if (node.getType() == NodeTypeEnums.LeftInputAdapterNode) {
                    alphas.add(((LeftInputAdapterNode) node).getObjectSource());
                }
            }

            if (NodeTypeEnums.isBetaNode(node) && ((BetaNode) node).isRightInputIsRiaNode()) {
                endNode = (PathEndNode) ((BetaNode) node).getRightInput();
                removePath(wms, context, stillInUse, alphas, endNode);
                return;
            }
        }
    }

    private boolean removeLeftTupleNode(Collection<InternalWorkingMemory> wms, RuleRemovalContext context, Map<Integer, BaseNode> stillInUse, BaseNode node) {
        boolean removed;
        removed = node.remove(context, this);

        if (removed) {
            stillInUse.remove( node.getId() );
            // phreak must clear node memories, although this should ideally be pushed into AddRemoveRule
            for (InternalWorkingMemory workingMemory : wms) {
                workingMemory.clearNodeMemory((MemoryFactory) node);
            }
        } else {
            stillInUse.put( node.getId(), node );
        }

        return removed;
    }

    private void removeObjectSource(Collection<InternalWorkingMemory> wms, Map<Integer, BaseNode> stillInUse, Set<Integer> removedNodes, ObjectSource node, RuleRemovalContext context ) {
        if (removedNodes.contains( node.getId() )) {
            return;
        }
        ObjectSource parent = node.getParentObjectSource();

        boolean removed = node.remove( context, this );

        if ( !removed ) {
            stillInUse.put( node.getId(), node );
        } else {
            stillInUse.remove(node.getId());
            removedNodes.add(node.getId());

            if ( node.getType() != NodeTypeEnums.ObjectTypeNode &&
                 node.getType() != NodeTypeEnums.AlphaNode ) {
                // phreak must clear node memories, although this should ideally be pushed into AddRemoveRule
                for (InternalWorkingMemory workingMemory : wms) {
                    workingMemory.clearNodeMemory( (MemoryFactory) node);
                }
            }

            if (parent != null && parent.getType() != NodeTypeEnums.EntryPointNode) {
                removeObjectSource(wms, stillInUse, removedNodes, parent, context);
            }
        }
    }

    private void removeNodeAssociation(BaseNode node, Rule rule, Set<Integer> removedNodes) {
        if (node == null || !removedNodes.add( node.getId() ) || !node.removeAssociation( rule )) {
            return;
        }
        if (node instanceof LeftTupleNode) {
            removeNodeAssociation( ((LeftTupleNode)node).getLeftTupleSource(), rule, removedNodes );
        }
        if ( NodeTypeEnums.isBetaNode( node ) ) {
            removeNodeAssociation( ((BetaNode) node).getRightInput(), rule, removedNodes );
        } else if ( node.getType() == NodeTypeEnums.LeftInputAdapterNode ) {
            removeNodeAssociation( ((LeftInputAdapterNode) node).getObjectSource(), rule, removedNodes );
        } else if ( node.getType() == NodeTypeEnums.AlphaNode ) {
            removeNodeAssociation( ((AlphaNode) node).getParentObjectSource(), rule, removedNodes );
        }
    }

    private void resetMasks(Collection<BaseNode> nodes) {
        NodeSet leafSet = new NodeSet();

        for ( BaseNode node : nodes ) {
            if ( node.getType() == NodeTypeEnums.AlphaNode ) {
                ObjectSource source = (AlphaNode) node;
                while ( true ) {
                    source.resetInferredMask();
                    BaseNode parent = source.getParentObjectSource();
                    if (parent.getType() != NodeTypeEnums.AlphaNode) {
                        break;
                    }
                    source = (ObjectSource)parent;
                }
                updateLeafSet(source, leafSet );
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
        } else if ( NodeTypeEnums.isBetaNode( baseNode ) && ( baseNode.isInUse() )) {
            leafSet.add( baseNode );
        }
    }

    public static class IdGenerator implements Externalizable {
        private InternalIdGenerator defaultGenerator = new InternalIdGenerator( 1 );
        private Map<String, InternalIdGenerator> generators = new ConcurrentHashMap<>();

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            defaultGenerator = (InternalIdGenerator) in.readObject();
            generators = (Map<String, InternalIdGenerator>) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( defaultGenerator );
            out.writeObject( generators );
        }

        public int getNextId() {
            return defaultGenerator.getNextId();
        }

        public int getNextId(String topic) {
            return generators.computeIfAbsent( topic, key -> new InternalIdGenerator( 1 ) ).getNextId();
        }

        public synchronized void releaseId(NetworkNode node) {
            defaultGenerator.releaseId( node.getId() );
            if (node instanceof MemoryFactory) {
                generators.get( DEFAULT_RULE_UNIT ).releaseId( ( (MemoryFactory) node ).getMemoryId() );
            }
        }

        public int getLastId() {
            return defaultGenerator.getLastId();
        }

        public int getLastId(String topic) {
            InternalIdGenerator gen = generators.get( topic );
            return gen != null ? gen.getLastId() : 0;
        }
    }

    private static class InternalIdGenerator implements Externalizable {

        private static final long serialVersionUID = 510l;

        private Queue<Integer>    recycledIds;
        private int               nextId;

        public InternalIdGenerator() { }

        public InternalIdGenerator(final int firstId) {
            this.nextId = firstId;
            this.recycledIds = new LinkedList<>();
        }

        @SuppressWarnings("unchecked")
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
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
        DroolsObjectOutputStream droolsStream;
        ByteArrayOutputStream bytes;

        if ( out instanceof DroolsObjectOutputStream ) {
            bytes = null;
            droolsStream = (DroolsObjectOutputStream) out;
        } else {
            bytes = new ByteArrayOutputStream();
            droolsStream = new DroolsObjectOutputStream( bytes );
        }
        try {
            droolsStream.writeObject( rules );
            droolsStream.writeObject( queries );
            droolsStream.writeObject( namedWindows );
            droolsStream.writeObject( idGenerator );
        } finally {
            if ( bytes != null ) {
                droolsStream.flush();
                droolsStream.close();
                bytes.close();
                out.writeInt( bytes.size() );
                out.writeObject( bytes.toByteArray() );
            }
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        DroolsObjectInputStream droolsStream;
        ByteArrayInputStream bytes;

        if ( in instanceof DroolsObjectInputStream ) {
            bytes = null;
            droolsStream = (DroolsObjectInputStream) in;
        } else {
            bytes = new ByteArrayInputStream( (byte[]) in.readObject() );
            droolsStream = new DroolsObjectInputStream( bytes );
        }
        try {
            this.rules = (Map<String, BaseNode[]>) droolsStream.readObject();
            this.queries = (Map<String, BaseNode[]>) droolsStream.readObject();
            this.namedWindows = (Map<String, WindowNode>) droolsStream.readObject();
            this.idGenerator = (IdGenerator) droolsStream.readObject();
        } finally {
            if ( bytes != null ) {
                droolsStream.close();
                bytes.close();
            }
        }
    }

    public void setRuleBase( InternalKnowledgeBase kBase ) {
        this.kBase = kBase;

        this.ruleBuilder = kBase.getConfiguration().getComponentFactory().getRuleBuilderFactory().newRuleBuilder();
    }

}