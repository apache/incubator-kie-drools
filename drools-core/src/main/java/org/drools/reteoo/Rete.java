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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.common.BaseNode;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.InternalWorkingMemoryEntryPoint;
import org.drools.common.RuleBasePartitionId;
import org.drools.common.UpdateContext;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.EntryPoint;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;

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
public class Rete extends ObjectSource
    implements
    Externalizable,
    ObjectSink {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long               serialVersionUID = 510l;

    private Map<EntryPoint, EntryPointNode> entryPoints;

    private transient InternalRuleBase      ruleBase;

    public Rete() {
        this( null );
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    public Rete(InternalRuleBase ruleBase) {
        super( 0, RuleBasePartitionId.MAIN_PARTITION, ruleBase != null ? ruleBase.getConfiguration().isMultithreadEvaluation() : false );
        this.entryPoints = Collections.synchronizedMap( new HashMap<EntryPoint, EntryPointNode>() );
        this.ruleBase = ruleBase;
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * This is the entry point into the network for all asserted Facts. Iterates a cache
     * of matching <code>ObjectTypdeNode</code>s asserting the Fact. If the cache does not
     * exist it first iteraes and builds the cache.
     *
     * @param factHandle
     *            The FactHandle of the fact to assert
     * @param context
     *            The <code>PropagationContext</code> of the <code>WorkingMemory</code> action
     * @param workingMemory
     *            The working memory session.
     */
    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        EntryPoint entryPoint = context.getEntryPoint();
        EntryPointNode node = this.entryPoints.get( entryPoint );
        ObjectTypeConf typeConf = ((InternalWorkingMemoryEntryPoint) workingMemory.getWorkingMemoryEntryPoint( entryPoint.getEntryPointId() )).getObjectTypeConfigurationRegistry().getObjectTypeConf( entryPoint,
                                                                                                                                                                                                       factHandle.getObject() );
        node.assertObject( factHandle,
                           context,
                           typeConf,
                           workingMemory );
    }

    /**
     * Retract a fact object from this <code>RuleBase</code> and the specified
     * <code>WorkingMemory</code>.
     *
     * @param handle
     *            The handle of the fact to retract.
     * @param workingMemory
     *            The working memory session.
     */
    public void retractObject(final InternalFactHandle handle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory) {
        EntryPoint entryPoint = context.getEntryPoint();
        EntryPointNode node = this.entryPoints.get( entryPoint );
        ObjectTypeConf typeConf = ((InternalWorkingMemoryEntryPoint) workingMemory.getWorkingMemoryEntryPoint( entryPoint.getEntryPointId() )).getObjectTypeConfigurationRegistry().getObjectTypeConf( entryPoint,
                                                                                                                                                                                                       handle.getObject() );
        node.retractObject( handle,
                            context,
                            typeConf,
                            workingMemory );
    }
    
    public void modifyObject(final InternalFactHandle factHandle,
                             final ModifyPreviousTuples modifyPreviousTuples,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
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
        ruleBase.registerAddedEntryNodeCache(node);
    }

    protected void removeObjectSink(final ObjectSink objectSink) {
        final EntryPointNode node = (EntryPointNode) objectSink;
        entryPoints.remove(node.getEntryPoint());
        ruleBase.registeRremovedEntryNodeCache(node);
    }

    public void attach( BuildContext context ) {
        throw new UnsupportedOperationException( "cannot call attach() from the root Rete node" );
    }

    public void networkUpdated(UpdateContext updateContext) {
        // nothing to do
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        // for now, we don't remove EntryPointNodes because they might be referenced by external sources
    }

    public EntryPointNode getEntryPointNode(final EntryPoint entryPoint) {
        return this.entryPoints.get( entryPoint );
    }

    public List<ObjectTypeNode> getObjectTypeNodes() {
        List<ObjectTypeNode> allNodes = new LinkedList<ObjectTypeNode>();
        for ( EntryPointNode node : this.entryPoints.values() ) {
            allNodes.addAll( node.getObjectTypeNodes().values() );
        }
        return allNodes;
    }

    public Map<ObjectType, ObjectTypeNode> getObjectTypeNodes(EntryPoint entryPoint) {
        return this.entryPoints.get( entryPoint ).getObjectTypeNodes();
    }

    public InternalRuleBase getRuleBase() {
        return this.ruleBase;
    }

    public int hashCode() {
        return this.entryPoints.hashCode();
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || !(object instanceof Rete) ) {
            return false;
        }

        final Rete other = (Rete) object;
        return this.entryPoints.equals( other.entryPoints );
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        // nothing to do, since Rete object itself holds no facts to propagate.
    }

    public boolean isObjectMemoryEnabled() {
        throw new UnsupportedOperationException( "Rete has no Object memory" );
    }

    public void setObjectMemoryEnabled(boolean objectMemoryEnabled) {
        throw new UnsupportedOperationException( "ORete has no Object memory" );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( entryPoints );
        super.writeExternal( out );
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        entryPoints = (Map<EntryPoint, EntryPointNode>) in.readObject();
        ruleBase = ((DroolsObjectInputStream)in).getRuleBase();
        for (Map.Entry<EntryPoint, EntryPointNode> entry : entryPoints.entrySet()) {
            EntryPointNode node = entry.getValue();
            if (node.getEntryPoint() == null) node.setEntryPoint(entry.getKey());
            ruleBase.registerAddedEntryNodeCache(node);
        }
        super.readExternal( in );
    }

    public Map<EntryPoint,EntryPointNode> getEntryPointNodes() {
        return this.entryPoints;
    }
    
    public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                       ModifyPreviousTuples modifyPreviousTuples,
                                       PropagationContext context,
                                       InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException( "This should never get called, as the PropertyReactive first happens at the AlphaNode" );
    }   
    
    @Override
    public long calculateDeclaredMask(List<String> settableProperties) {
        throw new UnsupportedOperationException();
    }    
}
