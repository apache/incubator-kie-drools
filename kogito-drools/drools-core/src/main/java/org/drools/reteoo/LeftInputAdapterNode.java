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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.BetaNodeConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.spi.FieldConstraint;
import org.drools.spi.PropagationContext;
import org.drools.util.Iterator;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListNode;
import org.drools.util.LinkedListEntry;
import org.drools.util.ObjectHashMap;
import org.drools.util.ObjectHashMap.ObjectEntry;

/**
 * All asserting Facts must propagated into the right <code>ObjectSink</code> side of a BetaNode, if this is the first Column
 * then there are no BetaNodes to propagate to. <code>LeftInputAdapter</code> is used to adapt an ObjectSink propagation into a 
 * <code>TupleSource</code> which propagates a <code>ReteTuple</code> suitable fot the right <code>ReteTuple</code> side 
 * of a <code>BetaNode</code>.
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
class LeftInputAdapterNode extends TupleSource
    implements
    ObjectSink,
    NodeMemory {

    /**
     * 
     */
    private static final long         serialVersionUID = 320L;
    private final ObjectSource        objectSource;
    private final BetaNodeConstraints constraints;

    /**
     * Constructus a LeftInputAdapterNode with a unique id that receives <code>FactHandle</code> from a 
     * parent <code>ObjectSource</code> and adds it to a given column in the resulting Tuples.
     * 
     * @param id
     *      The unique id of this node in the current Rete network
     * @param source
     *      The parent node, where Facts are propagated from
     */
    public LeftInputAdapterNode(final int id,
                                final ObjectSource source) {
        this( id,
              source,
              null );
    }

    /**
     * Constructus a LeftInputAdapterNode with a unique id that receives <code>FactHandle</code> from a 
     * parent <code>ObjectSource</code> and adds it to a given column in the resulting Tuples.
     * 
     * @param id
     *      The unique id of this node in the current Rete network
     * @param source
     *      The parent node, where Facts are propagated from
     * @param binder
     *      An optional binder to filter out propagations. This binder will exist when
     *      a predicate is used in the first column, for instance
     */
    public LeftInputAdapterNode(final int id,
                                final ObjectSource source,
                                final BetaNodeConstraints constraints) {
        super( id );
        this.objectSource = source;
        this.constraints = constraints;
        setHasMemory( false );
    }

    public FieldConstraint[] getConstraints() {
        LinkedList constraints = this.constraints.getConstraints();

        FieldConstraint[] array = new FieldConstraint[constraints.size()];
        int i = 0;
        for ( LinkedListEntry entry = (LinkedListEntry) constraints.getFirst(); entry != null; entry = (LinkedListEntry) entry.getNext() ) {
            array[i++] = (FieldConstraint) entry.getObject();
        }
        return array;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#attach()
     */
    public void attach() {
        this.objectSource.addObjectSink( this );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null );
            this.objectSource.updateSink( this,
                                          propagationContext,
                                          workingMemory );
        }
    }

    /**
     * Takes the asserted <code>FactHandleImpl</code> received from the <code>ObjectSource</code> and puts it
     * in a new <code>ReteTuple</code> before propagating to the <code>TupleSinks</code>
     * 
     * @param handle
     *            The asserted <code>FactHandle/code>.
     * @param context
     *             The <code>PropagationContext</code> of the <code>WorkingMemory<code> action.           
     * @param workingMemory
     *            the <code>WorkingMemory</code> session.
     */
    public void assertObject(final InternalFactHandle handle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        if ( (this.constraints == null) || (this.constraints.isAllowed( handle,
                                                                        null,
                                                                        workingMemory )) ) {
            ReteTuple tuple = this.sink.createAndPropagateAssertTuple( handle,
                                                                       context,
                                                                       workingMemory );

            if ( this.hasMemory ) {
                ObjectHashMap map = (ObjectHashMap) workingMemory.getNodeMemory( this );
                map.put( handle,
                         tuple,
                         false );
            }
        }
    }

    /**
     * Retract an existing <code>FactHandleImpl</code> by placing it in a new <code>ReteTuple</code> before 
     * proagating to the <code>TupleSinks</code>
     * 
     * @param handle
     *            The <code>FactHandle/code> to retract.
     * @param context
     *             The <code>PropagationContext</code> of the <code>WorkingMemory<code> action.           
     * @param workingMemory
     *            the <code>WorkingMemory</code> session.
     */
    public void retractObject(final InternalFactHandle handle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory) {
        ReteTuple tuple = null;
        if ( this.hasMemory ) {
            ObjectHashMap map = (ObjectHashMap) workingMemory.getNodeMemory( this );
            tuple = ( ReteTuple ) map.remove( handle );
        } else {
            tuple = new ReteTuple( handle );            
        }
        
        this.sink.createAndPropagateRetractTuple( tuple,
                                                  context,
                                                  workingMemory );
        tuple.release();
    }

    public void updateSink(TupleSink sink,
                           PropagationContext context,
                           InternalWorkingMemory workingMemory) {
        if ( this.hasMemory ) {
            // We have memory so iterate over all entries
            ObjectHashMap map = (ObjectHashMap) workingMemory.getNodeMemory( this );
            Iterator it = map.iterator();
            for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; it.next() ) {
                InternalFactHandle handle = (InternalFactHandle) entry.getKey();
                ReteTuple tuple = (ReteTuple) entry.getValue();
                sink.assertTuple( tuple,
                                  context,
                                  workingMemory );
            }
        } else {
            ObjectSinkAdapter adapter = new ObjectSinkAdapter( sink );
            this.objectSource.updateSink( adapter,
                                          context,
                                          workingMemory );
        }
    }

    public void remove(final BaseNode node,
                       final InternalWorkingMemory[] workingMemories) {
        if ( !node.isInUse() ) {
            removeTupleSink( (TupleSink) node );
        }
        removeShare();
        if ( !this.isInUse() ) {
            for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
                workingMemories[i].clearNodeMemory( this );
            }
        }
        this.objectSource.remove( this,
                                  workingMemories );
    }

    public int hashCode() {
        return this.objectSource.hashCode();
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || object.getClass() != LeftInputAdapterNode.class ) {
            return false;
        }

        final LeftInputAdapterNode other = (LeftInputAdapterNode) object;
        if ( this.constraints == null ) {
            return this.objectSource.equals( other.objectSource ) && other.constraints == null;
        } else {
            return this.objectSource.equals( other.objectSource ) && this.constraints.equals( other.constraints );
        }
    }

    public Object createMemory(RuleBaseConfiguration config) {
        return new ObjectHashMap();
    }

    /**
     * Used with the updateSink method, so that the parent ObjectSource
     * can  update the  TupleSink
     * @author mproctor
     *
     */
    private static class ObjectSinkAdapter
        implements
        ObjectSink {
        private TupleSink sink;

        public ObjectSinkAdapter(TupleSink sink) {
            this.sink = sink;
        }

        public void assertObject(InternalFactHandle handle,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
            ReteTuple tuple = new ReteTuple( handle );
            this.sink.assertTuple( tuple,
                                   context,
                                   workingMemory );
        }

        public void modifyObject(InternalFactHandle handle,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
            throw new UnsupportedOperationException( "ObjectSinkAdapter onlys supports assertObject method calls" );
        }

        public void retractObject(InternalFactHandle handle,
                                  PropagationContext context,
                                  InternalWorkingMemory workingMemory) {
            throw new UnsupportedOperationException( "ObjectSinkAdapter onlys supports assertObject method calls" );
        }
    }
}
