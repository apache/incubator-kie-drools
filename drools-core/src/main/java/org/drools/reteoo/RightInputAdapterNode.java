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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.RuleBaseConfiguration;
import org.drools.base.DroolsQuery;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.UpdateContext;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashMap;
import org.drools.core.util.ObjectHashMap.ObjectEntry;
import org.drools.reteoo.builder.BuildContext;
import org.drools.spi.PropagationContext;

/**
 * When joining a subnetwork into the main network again, RightInputAdapterNode adapts the
 * subnetwork's tuple into a fact in order right join it with the tuple being propagated in
 * the main network.
 */
public class RightInputAdapterNode extends ObjectSource
    implements
    LeftTupleSinkNode,
    NodeMemory {

    private static final long serialVersionUID = 510l;

    private LeftTupleSource   tupleSource;

    protected boolean         tupleMemoryEnabled;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    public RightInputAdapterNode() {
    }

    /**
     * Constructor specifying the unique id of the node in the Rete network, the position of the propagating <code>FactHandleImpl</code> in
     * <code>ReteTuple</code> and the source that propagates the receive <code>ReteTuple<code>s.
     *
     * @param id
     *      Unique id
     * @param source
     *      The <code>TupleSource</code> which propagates the received <code>ReteTuple</code>
     */
    public RightInputAdapterNode(final int id,
                                 final LeftTupleSource source,
                                 final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        this.tupleSource = source;
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        tupleSource = (LeftTupleSource) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
        previousTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        nextTupleSinkNode = (LeftTupleSinkNode) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( tupleSource );
        out.writeBoolean( tupleMemoryEnabled );
        out.writeObject( previousTupleSinkNode );
        out.writeObject( nextTupleSinkNode );

    }

    /**
     * Creates and return the node memory
     */
    public Object createMemory(final RuleBaseConfiguration config) {
        return new ObjectHashMap();
    }

    /**
     * Takes the asserted <code>ReteTuple</code> received from the <code>TupleSource</code> and
     * adapts it into a FactHandleImpl
     *
     * @param tuple
     *            The asserted <code>ReteTuple</code>.
     * @param context
     *             The <code>PropagationContext</code> of the <code>WorkingMemory<code> action.
     * @param workingMemory
     *            the <code>WorkingMemory</code> session.
     */
    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        // creating a dummy fact handle to wrap the tuple
        final InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( leftTuple,
                                                                                              workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( context.getEntryPoint(),
                                                                                                                                                                    leftTuple ),
                                                                                              workingMemory,
                                                                                              null );
        boolean useLeftMemory = true;   
        if ( !this.tupleMemoryEnabled ) {
            // This is a hack, to not add closed DroolsQuery objects
            Object object = ((InternalFactHandle) leftTuple.get( 0 )).getObject();
            if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
                useLeftMemory = false;
            }
        }         
        
        if ( useLeftMemory) {
            final ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );
            // add it to a memory mapping
            memory.put( leftTuple,
                        handle );
        }

        // propagate it
        this.sink.propagateAssertObject( handle,
                                         context,
                                         workingMemory );
    }

    /**
     * Retracts the corresponding tuple by retrieving and retracting
     * the fact created for it
     */
    public void retractLeftTuple(final LeftTuple tuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        final ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );
        // retrieve handle from memory
        final InternalFactHandle factHandle = (InternalFactHandle) memory.remove( tuple );
        
        for ( RightTuple rightTuple = factHandle.getFirstRightTuple(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getHandleNext() ) {
            rightTuple.getRightTupleSink().retractRightTuple( rightTuple,
                                                              context,
                                                              workingMemory );
        }
        factHandle.clearRightTuples();

        for ( LeftTuple leftTuple = factHandle.getLastLeftTuple(); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
            leftTuple.getLeftTupleSink().retractLeftTuple( leftTuple,
                                                           context,
                                                           workingMemory );
        }
        factHandle.clearLeftTuples();
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException( "This method should never be called" );
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        final ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );
        // add it to a memory mapping
        InternalFactHandle handle = (InternalFactHandle) memory.get( leftTuple );

        // propagate it
        for ( RightTuple rightTuple = handle.getFirstRightTuple(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getHandleNext() ) {
            rightTuple.getRightTupleSink().modifyRightTuple( rightTuple,
                                                             context,
                                                             workingMemory );
        }
    }

    public void attach() {
        this.tupleSource.addTupleSink( this );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null,
                                                                                      null );
            this.tupleSource.updateSink( this,
                                         propagationContext,
                                         workingMemory );
        }
    }

    public void networkUpdated(UpdateContext updateContext) {
        this.tupleSource.networkUpdated(updateContext);
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {

        final ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );

        final Iterator it = memory.iterator();

        // iterates over all propagated handles and assert them to the new sink
        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            sink.assertObject( (InternalFactHandle) entry.getValue(),
                               context,
                               workingMemory );
        }
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        if ( !node.isInUse() ) {
            removeObjectSink( (ObjectSink) node );
        }

        if ( !this.isInUse() ) {
            for ( InternalWorkingMemory workingMemory : workingMemories ) {
                ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );

                Iterator it = memory.iterator();
                for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
                    LeftTuple leftTuple = (LeftTuple) entry.getKey();
                    leftTuple.unlinkFromLeftParent();
                    leftTuple.unlinkFromRightParent();
                }
                workingMemory.clearNodeMemory( this );
            }
        }
        this.tupleSource.remove( context,
                                 builder,
                                 this,
                                 workingMemories );
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextLeftTupleSinkNode(final LeftTupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousLeftTupleSinkNode(final LeftTupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

    public short getType() {
        return NodeTypeEnums.RightInputAdaterNode;
    }

    public int hashCode() {
        return this.tupleSource.hashCode() * 17 + ((this.tupleMemoryEnabled) ? 1234 : 4321);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof RightInputAdapterNode) ) {
            return false;
        }

        final RightInputAdapterNode other = (RightInputAdapterNode) object;

        return this.tupleMemoryEnabled == other.tupleMemoryEnabled && this.tupleSource.equals( other.tupleSource );
    }

    @Override
    public String toString() {
        return "RightInputAdapterNode(" + id + ")[ tupleMemoryEnabled=" + tupleMemoryEnabled + ", tupleSource=" + tupleSource + ", source="
               + source + ", associations=" + associations.keySet() + ", partitionId=" + partitionId + "]";
    }
    
    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new JoinNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled );
    }    
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new JoinNodeLeftTuple(leftTuple,sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTupleSink sink) {
        return new JoinNodeLeftTuple(leftTuple, rightTuple, sink );
    }   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new JoinNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
    }

    public LeftTupleSource getLeftTupleSource() {
        return this.tupleSource;
    }           

}
