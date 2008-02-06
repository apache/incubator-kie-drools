package org.drools.reteoo;

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

import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.builder.BuildContext;
import org.drools.spi.PropagationContext;
import org.drools.util.Iterator;
import org.drools.util.ObjectHashMap;
import org.drools.util.ObjectHashMap.ObjectEntry;

/**
 * When joining a subnetwork into the main network again, RightInputAdapterNode adapts the 
 * subnetwork's tuple into a fact in order right join it with the tuple being propagated in
 * the main network.
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 * @author <a href="mailto:etirelli@redhat.com">Edson Tirelli</a>
 *
 */
public class RightInputAdapterNode extends ObjectSource
    implements
    TupleSinkNode,
    NodeMemory {

    private static final long serialVersionUID = 400L;

    private final TupleSource tupleSource;
    
    protected boolean          tupleMemoryEnabled;      

    private TupleSinkNode       previousTupleSinkNode;
    private TupleSinkNode       nextTupleSinkNode;

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
                                 final TupleSource source,
                                 final BuildContext context) {
        super( id );
        this.tupleSource = source;
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
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
    public void assertTuple(final ReteTuple tuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {

        // creating a dummy fact handle to wrap the tuple
        final InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( tuple, false, workingMemory );
        
        if ( this.tupleMemoryEnabled ) {
            final ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );
            // add it to a memory mapping
            memory.put( tuple,
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
    public void retractTuple(final ReteTuple tuple,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {

        final ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );

        // retrieve handle from memory
        final InternalFactHandle handle = (InternalFactHandle) memory.remove( tuple );

        // propagate a retract for it
        this.sink.propagateRetractObject( handle,
                                          context,
                                          workingMemory,
                                          true );

        // destroy dummy handle
        workingMemory.getFactHandleFactory().destroyFactHandle( handle );
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
                                                                                      null );
            this.tupleSource.updateSink( this,
                                         propagationContext,
                                         workingMemory );
        }
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
        if( ! context.alreadyVisited( this.tupleSource ) ) {
            this.tupleSource.remove( context,
                                     builder,
                                     this,
                                     workingMemories );
        }
    }
    
    public boolean isTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public TupleSinkNode getNextTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node 
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextTupleSinkNode(final TupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public TupleSinkNode getPreviousTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node 
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousTupleSinkNode(final TupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

    public int hashCode() {
        return this.tupleSource.hashCode() * 17 + ((this.tupleMemoryEnabled) ? 1234 : 4321 );
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
}
