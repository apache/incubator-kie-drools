/*
 * Copyright 2007 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.bitmask.BitMask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    protected static final transient Logger log = LoggerFactory.getLogger(EntryPointNode.class);

    /**
     * The entry point ID for this node
     */
    private EntryPointId                      entryPoint;

    /**
     * The object type nodes under this node
     */
    protected Map<ObjectType, ObjectTypeNode> objectTypeNodes;

    protected ObjectTypeNode queryNode;

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
              context.getKnowledgeBase().getConfiguration().isMultithreadEvaluation(),
              objectSource,
              context.getCurrentEntryPoint() ); // irrelevant for this node, since it overrides sink management
    }

    public EntryPointNode(final int id,
                          final RuleBasePartitionId partitionId,
                          final boolean partitionsEnabled,
                          final ObjectSource objectSource,
                          final EntryPointId entryPoint) {
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
        entryPoint = (EntryPointId) in.readObject();
        objectTypeNodes = (Map<ObjectType, ObjectTypeNode>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject( entryPoint );
        out.writeObject( objectTypeNodes );
    }

    public short getType() {
        return NodeTypeEnums.EntryPointNode;
    }

    /**
     * @return the entryPoint
     */
    public EntryPointId getEntryPoint() {
        return entryPoint;
    }
    void setEntryPoint(EntryPointId entryPoint) {
        this.entryPoint = entryPoint;
    }

    public void assertQuery(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException("rete only");
    }

    public void retractQuery(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException("rete only");
    }

    public void modifyQuery(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException("rete only");
     }

    public ObjectTypeNode getQueryNode() {
        if ( queryNode == null ) {
            this.queryNode = objectTypeNodes.get( ClassObjectType.DroolsQuery_ObjectType );
        }
        return this.queryNode;
    }

    public void assertActivation(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        if ( activationNode == null ) {
            this.activationNode = objectTypeNodes.get( ClassObjectType.Match_ObjectType );
        }

        if ( activationNode != null ) {
            // There may be no queries defined
            this.activationNode.propagateAssert(factHandle, context, workingMemory);
        }
    }

    public void retractActivation(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        if ( activationNode == null ) {
            this.activationNode = objectTypeNodes.get( ClassObjectType.Match_ObjectType );
        }

        if ( activationNode != null ) {
            // There may be no queries defined
            this.activationNode.retractObject(factHandle, context, workingMemory);
        }
    }

    public void modifyActivation(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
         if ( activationNode == null ) {
             this.activationNode = objectTypeNodes.get( ClassObjectType.Match_ObjectType );
         }

         if ( activationNode != null ) {
             ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples(factHandle.getFirstLeftTuple(), factHandle.getFirstRightTuple(), this );
             factHandle.clearLeftTuples();
             factHandle.clearRightTuples();

             // There may be no queries defined
             this.activationNode.modifyObject( factHandle, modifyPreviousTuples, context, workingMemory );
             modifyPreviousTuples.retractTuples(context, workingMemory);
         }

     }

    public void assertObject(final InternalFactHandle handle,
                             final PropagationContext context,
                             final ObjectTypeConf objectTypeConf,
                             final InternalWorkingMemory workingMemory) {
        if ( log.isTraceEnabled() ) {
            log.trace("Insert {}", handle.toString());
        }

        workingMemory.addPropagation(new PropagationEntry.Insert(handle, context, workingMemory, objectTypeConf ));
    }


    public void modifyObject(final InternalFactHandle handle,
                             final PropagationContext pctx,
                             final ObjectTypeConf objectTypeConf,
                             final InternalWorkingMemory workingMemory) {
        if ( log.isTraceEnabled() ) {
            log.trace( "Update {}", handle.toString()  );
        }

        workingMemory.addPropagation(new PropagationEntry.Update(this, handle, pctx, objectTypeConf));
    }

    public void propagateModify(InternalFactHandle handle, PropagationContext pctx, ObjectTypeConf objectTypeConf, InternalWorkingMemory wm) {
        ObjectTypeNode[] cachedNodes = objectTypeConf.getObjectTypeNodes();

        // make a reference to the previous tuples, then null then on the handle
        ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples(handle.getFirstLeftTuple(), handle.getFirstRightTuple(), this );
        handle.clearLeftTuples();
        handle.clearRightTuples();

        for ( int i = 0, length = cachedNodes.length; i < length; i++ ) {
            cachedNodes[i].modifyObject( handle,
                                         modifyPreviousTuples,
                                         pctx, wm );

            // remove any right tuples that matches the current OTN before continue the modify on the next OTN cache entry
            if (i < cachedNodes.length - 1) {
                RightTuple rightTuple = modifyPreviousTuples.peekRightTuple();
                while ( rightTuple != null &&
                        ((BetaNode) rightTuple.getTupleSink()).getObjectTypeNode() == cachedNodes[i] ) {
                    modifyPreviousTuples.removeRightTuple();

                    doRightDelete(pctx, wm, rightTuple);

                    rightTuple = modifyPreviousTuples.peekRightTuple();
                }


                LeftTuple leftTuple;
                ObjectTypeNode otn;
                while ( true ) {
                    leftTuple = modifyPreviousTuples.peekLeftTuple();
                    otn = null;
                    if (leftTuple != null) {
                        LeftTupleSink leftTupleSink = leftTuple.getTupleSink();
                        if (leftTupleSink instanceof LeftTupleSource) {
                            otn = leftTupleSink.getLeftTupleSource().getObjectTypeNode();
                        } else if (leftTupleSink instanceof RuleTerminalNode) {
                            otn = ((RuleTerminalNode)leftTupleSink).getObjectTypeNode();
                        }
                    }

                    if ( otn == null || cachedNodes[i] != otn ) break;

                    modifyPreviousTuples.removeLeftTuple();
                    doDeleteObject(pctx, wm, leftTuple);
                }
            }
        }
        modifyPreviousTuples.retractTuples(pctx, wm);
    }

    public void doDeleteObject(PropagationContext pctx, InternalWorkingMemory wm, LeftTuple leftTuple) {
        LeftInputAdapterNode liaNode = leftTuple.getTupleSource();
        LiaNodeMemory lm = wm.getNodeMemory( liaNode );
        LeftInputAdapterNode.doDeleteObject(leftTuple, pctx, lm.getSegmentMemory(), wm, liaNode, true, lm);
    }

    public void doRightDelete(PropagationContext pctx, InternalWorkingMemory wm, RightTuple rightTuple) {
        rightTuple.setPropagationContext( pctx );
        rightTuple.retractTuple( pctx, wm );
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
        if ( log.isTraceEnabled() ) {
            log.trace( "Delete {}", handle.toString()  );
        }

        workingMemory.addPropagation(new PropagationEntry.Delete(this, handle, context, objectTypeConf));
    }

    public void propagateRetract(InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf, InternalWorkingMemory workingMemory) {
        ObjectTypeNode[] cachedNodes = objectTypeConf.getObjectTypeNodes();

        if ( cachedNodes == null ) {
            // it is  possible that there are no ObjectTypeNodes for an  object being retracted
            return;
        }

        for ( ObjectTypeNode cachedNode : cachedNodes ) {
            cachedNode.retractObject( handle,
                                      context,
                                      workingMemory );
        }

        if (handle instanceof EventFactHandle) {
            ((EventFactHandle) handle).unscheduleAllJobs(workingMemory);
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
        this.objectTypeNodes.put(node.getObjectType(),
                                 node);
    }

    public boolean removeObjectSink(final ObjectSink objectSink) {
        final ObjectTypeNode node = (ObjectTypeNode) objectSink;
        this.objectTypeNodes.remove( node.getObjectType() );
        return false;
    }

    public void attach() {
        attach(null);
    }

    public void attach( BuildContext context ) {
        this.source.addObjectSink( this );
        if (context == null ) {
            return;
        }
        if ( context.getKnowledgeBase().getConfiguration().isPhreakEnabled() ) {
            for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
                workingMemory.updateEntryPointsCache();
            }
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            workingMemory.updateEntryPointsCache();
            PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
            final PropagationContext propagationContext = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION, null, null, null);
            this.source.updateSink( this,
                                    propagationContext,
                                    workingMemory );
        }
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder,
                               final InternalWorkingMemory[] workingMemories) {
        return false;
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
            if ( objectTypeConf.getConcreteObjectTypeNode() != null && newObjectType.isAssignableFrom( objectTypeConf.getConcreteObjectTypeNode().getObjectType() ) ) {
                objectTypeConf.resetCache();
                ObjectTypeNode sourceNode = objectTypeConf.getConcreteObjectTypeNode();
                Iterator<InternalFactHandle> it = workingMemory.getNodeMemory( sourceNode ).iterator();
                while ( it.hasNext() ) {
                    sink.assertObject( it.next(),
                                       context,
                                       workingMemory );
                }
            }
        }
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
    public BitMask calculateDeclaredMask(List<String> settableProperties) {
        throw new UnsupportedOperationException();
    }

}
