/*
 * Copyright 2007 JBoss Inc
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.base.ClassObjectType;
import org.drools.base.ShadowProxy;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.InternalWorkingMemoryEntryPoint;
import org.drools.common.PropagationContextImpl;
import org.drools.common.RuleBasePartitionId;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashSet.ObjectEntry;
import org.drools.reteoo.ObjectTypeNode.ObjectTypeNodeMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.EntryPoint;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;

/**
 * A node that is an entry point into the Rete network.
 *
 * As we move the design to support network partitions and concurrent processing
 * of parts of the network, we also need to support multiple, independent entry
 * points and this class represents that.
 *
 * It replaces the function of the Rete Node class in previous designs.
 *
 * @see ObjectTypeNode
 */
public class EntryPointNode extends ObjectSource
    implements
    Externalizable,
    ObjectSink {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long               serialVersionUID = 510l;

    /**
     * The entry point ID for this node
     */
    private EntryPoint                      entryPoint;

    /**
     * The object type nodes under this node
     */
    private Map<ObjectType, ObjectTypeNode> objectTypeNodes;
    
    private ObjectTypeNode queryNode;
    
    private ObjectTypeNode activationNode;   

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public EntryPointNode() {

    }

    public EntryPointNode(final int id,
                          final ObjectSource objectSource,
                          final BuildContext context) {
        this( id,
              context.getPartitionId(),
              context.getRuleBase().getConfiguration().isMultithreadEvaluation(),
              objectSource,
              context.getCurrentEntryPoint() ); // irrelevant for this node, since it overrides sink management
    }

    public EntryPointNode(final int id,
                          final RuleBasePartitionId partitionId,
                          final boolean partitionsEnabled,
                          final ObjectSource objectSource,
                          final EntryPoint entryPoint) {
        super( id,
               partitionId,
               partitionsEnabled,
               objectSource,
               999 ); // irrelevant for this node, since it overrides sink management
        this.entryPoint = entryPoint;
        this.objectTypeNodes = new ConcurrentHashMap<ObjectType, ObjectTypeNode>();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        entryPoint = (EntryPoint) in.readObject();
        objectTypeNodes = (Map<ObjectType, ObjectTypeNode>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( entryPoint );
        out.writeObject( objectTypeNodes );
    }

    /**
     * @return the entryPoint
     */
    public EntryPoint getEntryPoint() {
        return entryPoint;
    }
    void setEntryPoint(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;
    }
    
    public void assertQuery(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        if ( queryNode == null ) {
            this.queryNode = objectTypeNodes.get( ClassObjectType.DroolsQuery_ObjectType );
        }
        
        if ( queryNode != null ) {
            // There may be no queries defined
            this.queryNode.assertObject( factHandle, context, workingMemory );
        }       
    }
    
    public void retractQuery(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        if ( queryNode == null ) {
            this.queryNode = objectTypeNodes.get( ClassObjectType.DroolsQuery_ObjectType );
        }
        
        if ( queryNode != null ) {
            // There may be no queries defined
            this.queryNode.retractObject( factHandle, context, workingMemory );
        }       
    }    
    
    public void modifyQuery(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
         if ( queryNode == null ) {
             this.queryNode = objectTypeNodes.get( ClassObjectType.DroolsQuery_ObjectType );
         }
         
         if ( queryNode != null ) {
             ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples(factHandle.getFirstLeftTuple(), factHandle.getFirstRightTuple() );
             factHandle.clearLeftTuples();
             factHandle.clearRightTuples();
             
             // There may be no queries defined
             this.queryNode.modifyObject( factHandle, modifyPreviousTuples, context, workingMemory );
             modifyPreviousTuples.retractTuples( context, workingMemory );
         }       
     }   
    
    public void assertActivation(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        if ( activationNode == null ) {
            this.activationNode = objectTypeNodes.get( ClassObjectType.Activation_ObjectType );
        }
        
        if ( activationNode != null ) {
            // There may be no queries defined
            this.activationNode.assertObject( factHandle, context, workingMemory );
        }       
    }
    
    public void retractActivation(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        if ( activationNode == null ) {
            this.activationNode = objectTypeNodes.get( ClassObjectType.Activation_ObjectType );
        }
        
        if ( activationNode != null ) {
            // There may be no queries defined
            this.activationNode.retractObject( factHandle, context, workingMemory );
        }       
    }    
    
    public void modifyActivation(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
         if ( activationNode == null ) {
             this.activationNode = objectTypeNodes.get( ClassObjectType.Activation_ObjectType );
         }
         
         if ( activationNode != null ) {
             ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples(factHandle.getFirstLeftTuple(), factHandle.getFirstRightTuple() );
             factHandle.clearLeftTuples();
             factHandle.clearRightTuples();
             
             // There may be no queries defined
             this.activationNode.modifyObject( factHandle, modifyPreviousTuples, context, workingMemory );
             modifyPreviousTuples.retractTuples( context, workingMemory );
         }       
         
     }      

    public void assertObject(final InternalFactHandle handle,
                             final PropagationContext context,
                             final ObjectTypeConf objectTypeConf,
                             final InternalWorkingMemory workingMemory) {
        // checks if shadow is enabled
        if ( objectTypeConf.isShadowEnabled() ) {
            // the user has implemented the ShadowProxy interface, let their implementation
            // know it is safe to update the information the engine can see.
            ((ShadowProxy) handle.getObject()).updateProxy();
        }
        
        ObjectTypeNode[] cachedNodes = objectTypeConf.getObjectTypeNodes();

        for ( int i = 0, length = cachedNodes.length; i < length; i++ ) {
            cachedNodes[i].assertObject( handle,
                                         context,
                                         workingMemory );
        }
    }
    
    public void modifyObject(final InternalFactHandle handle,
                             final PropagationContext context,
                             final ObjectTypeConf objectTypeConf,
                             final InternalWorkingMemory workingMemory) {
        // checks if shadow is enabled
        if ( objectTypeConf.isShadowEnabled() ) {
            // the user has implemented the ShadowProxy interface, let their implementation
            // know it is safe to update the information the engine can see.
            ((ShadowProxy) handle.getObject()).updateProxy();
        }
        
        ObjectTypeNode[] cachedNodes = objectTypeConf.getObjectTypeNodes();
        
        // make a reference to the previous tuples, then null then on the handle
        ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples(handle.getFirstLeftTuple(), handle.getFirstRightTuple() );
        handle.clearLeftTuples();
        handle.clearRightTuples();
        
        for ( int i = 0, length = cachedNodes.length; i < length; i++ ) {
            cachedNodes[i].modifyObject( handle,
                                         modifyPreviousTuples,
                                         context, workingMemory );

            // remove any right tuples that matches the current OTN before continue the modify on the next OTN cache entry
            if (i < cachedNodes.length - 1) {
                RightTuple rightTuple = modifyPreviousTuples.peekRightTuple();
                while ( rightTuple != null && rightTuple.getRightTupleSink() instanceof BetaNode &&
                        ((BetaNode) rightTuple.getRightTupleSink()).getObjectTypeNode() == cachedNodes[i] ) {
                    modifyPreviousTuples.removeRightTuple();
                    rightTuple = modifyPreviousTuples.peekRightTuple();
                }
            }
        }
        modifyPreviousTuples.retractTuples( context, workingMemory );
        
      
    }
    
    public void modifyObject(InternalFactHandle factHandle,
                                   ModifyPreviousTuples modifyPreviousTuples,
                                   PropagationContext context,
                                   InternalWorkingMemory workingMemory) {
        // this method was silently failing, so I am now throwing an exception to make
        // sure no one calls it by mistake
        throw new UnsupportedOperationException( "This method should NEVER EVER be called" );
    }

    /**
     * This is the entry point into the network for all asserted Facts. Iterates a cache
     * of matching <code>ObjectTypdeNode</code>s asserting the Fact. If the cache does not
     * exist it first iterates and builds the cache.
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
        // this method was silently failing, so I am now throwing an exception to make
        // sure no one calls it by mistake
        throw new UnsupportedOperationException( "This method should NEVER EVER be called" );
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
                              final ObjectTypeConf objectTypeConf,
                              final InternalWorkingMemory workingMemory) {
        ObjectTypeNode[] cachedNodes = objectTypeConf.getObjectTypeNodes();

        if ( cachedNodes == null ) {
            // it is  possible that there are no ObjectTypeNodes for an  object being retracted
            return;
        }

        for ( int i = 0; i < cachedNodes.length; i++ ) {
            cachedNodes[i].retractObject( handle,
                                          context,
                                          workingMemory );
        }
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
        final ObjectTypeNode node = (ObjectTypeNode) objectSink;
        this.objectTypeNodes.put( node.getObjectType(),
                                  node );
    }

    protected void removeObjectSink(final ObjectSink objectSink) {
        final ObjectTypeNode node = (ObjectTypeNode) objectSink;
        this.objectTypeNodes.remove( node.getObjectType() );
    }

    public void attach() {
        this.source.addObjectSink( this );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            workingMemory.updateEntryPointsCache();
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null,
                                                                                      null );
            this.source.updateSink( this,
                                    propagationContext,
                                    workingMemory );
        }
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        final ObjectTypeNode objectTypeNode = (ObjectTypeNode) node;
        removeObjectSink( objectTypeNode );
    }

    public Map<ObjectType, ObjectTypeNode> getObjectTypeNodes() {
        return this.objectTypeNodes;
    }

    public int hashCode() {
        return this.entryPoint.hashCode();
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || !(object instanceof EntryPointNode) ) {
            return false;
        }

        final EntryPointNode other = (EntryPointNode) object;
        return this.entryPoint.equals( other.entryPoint );
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        // @todo
        // JBRULES-612: the cache MUST be invalidated when a new node type is added to the network, so iterate and reset all caches.
        final ObjectTypeNode node = (ObjectTypeNode) sink;

        final ObjectType newObjectType = node.getObjectType();

        InternalWorkingMemoryEntryPoint wmEntryPoint = (InternalWorkingMemoryEntryPoint) workingMemory.getWorkingMemoryEntryPoint( this.entryPoint.getEntryPointId() );

        for ( ObjectTypeConf objectTypeConf : wmEntryPoint.getObjectTypeConfigurationRegistry().values() ) {
            if ( newObjectType.isAssignableFrom( objectTypeConf.getConcreteObjectTypeNode().getObjectType() ) ) {
                objectTypeConf.resetCache();
                ObjectTypeNode sourceNode = objectTypeConf.getConcreteObjectTypeNode();
                Iterator it = ((ObjectTypeNodeMemory) workingMemory.getNodeMemory( sourceNode )).memory.iterator();
                for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
                    sink.assertObject( (InternalFactHandle) entry.getValue(),
                                       context,
                                       workingMemory );
                }
            }
        }
    }

    public boolean isObjectMemoryEnabled() {
        return false;
    }

    public void setObjectMemoryEnabled(boolean objectMemoryEnabled) {
        throw new UnsupportedOperationException( "Entry Point Node has no Object memory" );
    }

    public String toString() {
        return "[EntryPointNode(" + this.id + ") " + this.entryPoint + " ]";
    }

    public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                       ModifyPreviousTuples modifyPreviousTuples,
                                       PropagationContext context,
                                       InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long calculateDeclaredMask(List<String> settableProperties) {
        throw new UnsupportedOperationException();
    }

}
