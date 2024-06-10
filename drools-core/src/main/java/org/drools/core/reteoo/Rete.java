/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.reteoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.base.base.ObjectType;
import org.drools.base.common.NetworkNode;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.rule.EntryPointId;
import org.drools.base.rule.Pattern;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.UpdateContext;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.util.bitmask.BitMask;

/**
 * The Rete-OO network.
 *
 * The Rete class is the root <code>Object</code>. All objects are asserted into
 * the Rete node where it propagates to all matching ObjectTypeNodes.
 *
 * The first time an  instance of a Class type is asserted it does a full
 * iteration of all ObjectTyppeNodes looking for matches, any matches are
 * then cached in a HashMap which is used for future assertions.
 *
 * While Rete  extends ObjectSource nad implements ObjectSink it nulls the
 * methods attach(), remove() and  updateNewNode() as this is the root node
 * they are no applicable
 *
 * @see ObjectTypeNode
 */
public class Rete extends ObjectSource implements ObjectSink {

    private final Map<EntryPointId, EntryPointNode> entryPoints = new HashMap<>();

    private final InternalRuleBase kBase;

    public Rete() {
        this( null );
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    public Rete(InternalRuleBase kBase) {
        super( 0, RuleBasePartitionId.MAIN_PARTITION );
        this.kBase = kBase;
        hashcode = calculateHashCode();
    }

    public int getType() {
        return NodeTypeEnums.ReteNode;
    }  

    /**
     * This is the entry point into the network for all asserted Facts. Iterates a cache
     * of matching <code>ObjectTypdeNode</code>s asserting the Fact. If the cache does not
     * exist it first iteraes and builds the cache.
     *
     * @param factHandle
     *            The FactHandle of the fact to assert
     * @param context
     *            The <code>PropagationContext</code> of the <code>WorkingMemory</code> action
     * @param reteEvaluator
     *            The working memory session.
     */
    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final ReteEvaluator reteEvaluator) {
        EntryPointId entryPoint = context.getEntryPoint();
        EntryPointNode node = this.entryPoints.get( entryPoint );
        ObjectTypeConf typeConf = reteEvaluator.getEntryPoint( entryPoint.getEntryPointId() )
                .getObjectTypeConfigurationRegistry().getOrCreateObjectTypeConf( entryPoint, factHandle.getObject() );
        node.assertObject( factHandle, context, typeConf, reteEvaluator );
    }

    /**
     * Retract a fact object from this <code>RuleBase</code> and the specified
     * <code>WorkingMemory</code>.
     *
     * @param handle
     *            The handle of the fact to retract.
     * @param reteEvaluator
     *            The working memory session.
     */
    public void retractObject(final InternalFactHandle handle,
                              final PropagationContext context,
                              final ReteEvaluator reteEvaluator) {
        EntryPointId entryPoint = context.getEntryPoint();
        EntryPointNode node = this.entryPoints.get( entryPoint );
        ObjectTypeConf typeConf = reteEvaluator.getEntryPoint( entryPoint.getEntryPointId() )
                .getObjectTypeConfigurationRegistry().getObjectTypeConf( handle.getObject() );
        node.retractObject( handle, context, typeConf, reteEvaluator );
    }
    
    public void modifyObject(final InternalFactHandle factHandle,
                             final ModifyPreviousTuples modifyPreviousTuples,
                             final PropagationContext context,
                             final ReteEvaluator reteEvaluator) {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds the <code>ObjectSink</code> so that it may receive
     * <code>Objects</code> propagated from this <code>ObjectSource</code>.
     *
     * @param objectSink
     *            The <code>ObjectSink</code> to receive propagated
     *            <code>Objects</code>. Rete only accepts <code>ObjectTypeNode</code>s
     *            as parameters to this method, though.
     */
    public void addObjectSink(final ObjectSink objectSink) {
        final EntryPointNode node = (EntryPointNode) objectSink;
        entryPoints.put(node.getEntryPoint(), node);
        kBase.registerAddedEntryNodeCache(node);
    }

    public void removeObjectSink(final ObjectSink objectSink) {
        final EntryPointNode node = (EntryPointNode) objectSink;
        entryPoints.remove(node.getEntryPoint());
        kBase.registeRremovedEntryNodeCache(node);
    }

    public void doAttach( BuildContext context ) {
        throw new UnsupportedOperationException( "cannot call attach() from the root Rete node" );
    }

    public void networkUpdated(UpdateContext updateContext) {
        // nothing to do
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder) {
        // for now, we don't remove EntryPointNodes because they might be referenced by external sources
        return false;
    }

    public EntryPointNode getEntryPointNode(final EntryPointId entryPoint) {
        return this.entryPoints.get( entryPoint );
    }

    public List<ObjectTypeNode> getObjectTypeNodes() {
        List<ObjectTypeNode> allNodes = new ArrayList<>();
        for ( EntryPointNode node : this.entryPoints.values() ) {
            allNodes.addAll( node.getObjectTypeNodes().values() );
        }
        return allNodes;
    }

    public Map<ObjectType, ObjectTypeNode> getObjectTypeNodes(EntryPointId entryPoint) {
        return this.entryPoints.get( entryPoint ).getObjectTypeNodes();
    }

    @Override
    public InternalRuleBase getRuleBase() {
        return this.kBase;
    }

    private int calculateHashCode() {
        return this.entryPoints.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if (((NetworkNode)object).getType() != NodeTypeEnums.ReteNode || this.hashCode() != object.hashCode()) {
            return false;
        }
        return this.entryPoints.equals( ((Rete)object).entryPoints );
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory wm) {
        // nothing to do, since Rete object itself holds no facts to propagate.
    }

    public Map<EntryPointId,EntryPointNode> getEntryPointNodes() {
        return this.entryPoints;
    }
    
    public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                       ModifyPreviousTuples modifyPreviousTuples,
                                       PropagationContext context,
                                       ReteEvaluator reteEvaluator) {
        throw new UnsupportedOperationException( "This should never get called, as the PropertyReactive first happens at the AlphaNode" );
    }   
    
    @Override
    public BitMask calculateDeclaredMask(Pattern pattern, ObjectType modifiedType, List<String> settableProperties) {
        throw new UnsupportedOperationException();
    }    
}
