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
import org.drools.FactException;
import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.spi.FieldConstraint;
import org.drools.spi.PropagationContext;
import org.drools.util.AbstractHashTable;
import org.drools.util.FactHashTable;
import org.drools.util.Iterator;
import org.drools.util.AbstractHashTable.FactEntry;

/**
 * <code>AlphaNodes</code> are nodes in the <code>Rete</code> network used
 * to apply <code>FieldConstraint<.code>s on asserted fact 
 * objects where the <code>FieldConstraint</code>s have no dependencies on any other of the facts in the current <code>Rule</code>.
 * 
 *  @see FieldConstraint
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
class AlphaNode extends ObjectSource
    implements
    ObjectSinkNode,
    NodeMemory {

    /**
     * 
     */
    private static final long     serialVersionUID = 8936511451364612838L;

    /** The <code>FieldConstraint</code> */
    private final FieldConstraint constraint;

    /** The <code>ObjectSource</code> */
    private final ObjectSource    objectSource;

    private ObjectSinkNode        previousObjectSinkNode;
    private ObjectSinkNode        nextObjectSinkNode;

    /**
     * Construct an <code>AlphaNode</code> with a unique id using the provided
     * <code>FieldConstraint</code>. <code>NodeMemory</code> is optional in
     * <code>AlphaNode</code>s and is only of benefit when adding additional
     * <code>Rule</code>s at runtime.
     * 
     * @param id
     * @param constraint
     * @param hasMemory
     * @param objectSource
     */
    AlphaNode(final int id,
              final FieldConstraint constraint,
              final ObjectSource objectSource) {
        super( id );
        this.constraint = constraint;
        this.objectSource = objectSource;
        setHasMemory( true );
    }

    /**
     * Retruns the <code>FieldConstraint</code>
     * 
     * @return <code>FieldConstraint</code>
     */
    public FieldConstraint getConstraint() {
        return this.constraint;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.reteoo.BaseNode#attach()
     */
    public void attach() {
        this.objectSource.addObjectSink( this );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        // we are attaching this node with existing working memories
        // so this  node must also have memory
        this.hasMemory = true;
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

    public void assertObject(final InternalFactHandle handle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) throws FactException {
        if ( this.constraint.isAllowed( handle.getObject(),
                                        null,
                                        workingMemory ) ) {
            if ( hasMemory() ) {
                final FactHashTable memory = (FactHashTable) workingMemory.getNodeMemory( this );
                memory.add( handle,
                            false );
            }
            sink.propagateAssertObject( handle,
                                        context,
                                        workingMemory );
        }
    }

    public void retractObject(final InternalFactHandle handle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory) {
        boolean propagate = true;
        if ( hasMemory() ) {
            final FactHashTable memory = (FactHashTable) workingMemory.getNodeMemory( this );
            propagate = memory.remove( handle );
        }
        if ( propagate ) {
            this.sink.propagateRetractObject( handle,
                                              context,
                                              workingMemory,
                                              true );
        }
    }
    
    public void updateSink(ObjectSink sink,
                           PropagationContext context,
                           InternalWorkingMemory workingMemory) {
        FactHashTable memory = null;

        // if it was not storing facts in memory previously, create memory and
        // start storing facts in the local memory
        if ( !hasMemory() ) {
            ObjectSinkAdapter adapter = new ObjectSinkAdapter( sink );
            this.objectSource.updateSink( adapter, context, workingMemory );
        } else {
            // if already has memory, just iterate and propagate
            memory = (FactHashTable) workingMemory.getNodeMemory( this );
            Iterator it = memory.iterator();            
            for ( FactEntry entry = ( FactEntry ) it.next(); entry != null; entry = ( FactEntry ) it.next() ) {
                sink.assertObject( entry.getFactHandle(),
                                   context,
                                   workingMemory );                
            }
        }
    }

    public void remove(final BaseNode node,
                       final InternalWorkingMemory[] workingMemories) {

        if ( !node.isInUse() ) {
            removeObjectSink( (ObjectSink) node );
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

    /**
     * Creates a HashSet for the AlphaNode's memory.
     */
    public Object createMemory(final RuleBaseConfiguration config) {
        return new FactHashTable();
    }

    public String toString() {
        return "[AlphaNode(" + this.id + ") constraint=" + this.constraint + "]";
    }

    public int hashCode() {
        return this.objectSource.hashCode() * 17 + ((this.constraint != null) ? this.constraint.hashCode() : 0);
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

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final AlphaNode other = (AlphaNode) object;

        return this.objectSource.equals( other.objectSource ) && this.constraint.equals( other.constraint );
    }

    /**
     * Returns the next node
     * @return
     *      The next ObjectSinkNode
     */
    public ObjectSinkNode getNextObjectSinkNode() {
        return this.nextObjectSinkNode;
    }

    /**
     * Sets the next node 
     * @param next
     *      The next ObjectSinkNode
     */
    public void setNextObjectSinkNode(ObjectSinkNode next) {
        this.nextObjectSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous ObjectSinkNode
     */
    public ObjectSinkNode getPreviousObjectSinkNode() {
        return this.previousObjectSinkNode;
    }

    /**
     * Sets the previous node 
     * @param previous
     *      The previous ObjectSinkNode
     */
    public void setPreviousObjectSinkNode(ObjectSinkNode previous) {
        this.previousObjectSinkNode = previous;
    }
    
    private static class ObjectSinkAdapter
    implements
    ObjectSink {
    private ObjectSink sink;
    public ObjectSinkAdapter(ObjectSink sink) {
        this.sink = sink;
    }        

    public void assertObject(InternalFactHandle handle,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        this.sink.assertObject( handle,
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
