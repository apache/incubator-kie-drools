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

import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.UpdateContext;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    private Map<EntryPointId, EntryPointNode> entryPoints;

    private transient InternalKnowledgeBase kBase;

    public Rete() {
        this( null );
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    public Rete(InternalKnowledgeBase kBase) {
        super( 0, RuleBasePartitionId.MAIN_PARTITION, kBase != null ? kBase.getConfiguration().isMultithreadEvaluation() : false );
        this.entryPoints = Collections.synchronizedMap( new HashMap<EntryPointId, EntryPointNode>() );
        this.kBase = kBase;
    }

    public short getType() {
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
     * @param workingMemory
     *            The working memory session.
     */
    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        EntryPointId entryPoint = context.getEntryPoint();
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
        EntryPointId entryPoint = context.getEntryPoint();
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
        kBase.registerAddedEntryNodeCache(node);
    }

    public void removeObjectSink(final ObjectSink objectSink) {
        final EntryPointNode node = (EntryPointNode) objectSink;
        entryPoints.remove(node.getEntryPoint());
        kBase.registeRremovedEntryNodeCache(node);
    }

    public void attach( BuildContext context ) {
        throw new UnsupportedOperationException( "cannot call attach() from the root Rete node" );
    }

    public void networkUpdated(UpdateContext updateContext) {
        // nothing to do
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final InternalWorkingMemory[] workingMemories) {
        // for now, we don't remove EntryPointNodes because they might be referenced by external sources
    }

    public EntryPointNode getEntryPointNode(final EntryPointId entryPoint) {
        return this.entryPoints.get( entryPoint );
    }

    public List<ObjectTypeNode> getObjectTypeNodes() {
        List<ObjectTypeNode> allNodes = new LinkedList<ObjectTypeNode>();
        for ( EntryPointNode node : this.entryPoints.values() ) {
            allNodes.addAll( node.getObjectTypeNodes().values() );
        }
        return allNodes;
    }

    public Map<ObjectType, ObjectTypeNode> getObjectTypeNodes(EntryPointId entryPoint) {
        return this.entryPoints.get( entryPoint ).getObjectTypeNodes();
    }

    public InternalKnowledgeBase getKnowledgeBase() {
        return this.kBase;
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
        entryPoints = (Map<EntryPointId, EntryPointNode>) in.readObject();
        kBase = ((DroolsObjectInputStream)in).getKnowledgeBase();
        for (Map.Entry<EntryPointId, EntryPointNode> entry : entryPoints.entrySet()) {
            EntryPointNode node = entry.getValue();
            if (node.getEntryPoint() == null) node.setEntryPoint(entry.getKey());
            kBase.registerAddedEntryNodeCache(node);
        }
        super.readExternal( in );
    }

    public Map<EntryPointId,EntryPointNode> getEntryPointNodes() {
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
