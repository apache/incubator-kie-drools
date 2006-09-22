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
import org.drools.util.FactHashSet;
import org.drools.util.FactHashSet.FactEntry;

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
    
    private ObjectSinkNode previousObjectSinkNode;
    private ObjectSinkNode nextObjectSinkNode; 

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

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null );
            this.objectSource.updateNewNode( workingMemory,
                                             propagationContext );
        }
    }

    public void assertObject(final InternalFactHandle handle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) throws FactException {
        final FactHashSet memory = (FactHashSet) workingMemory.getNodeMemory( this );
        if ( this.constraint.isAllowed( handle.getObject(),
                                        null,
                                        workingMemory ) ) {
            memory.add( handle, false );
            sink.propagateAssertObject( handle,
                                        context,
                                        workingMemory );
        }
    }

    public void retractObject(final InternalFactHandle handle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory) {
        final FactHashSet memory = (FactHashSet) workingMemory.getNodeMemory( this );
        if ( memory.remove( handle ) ) {
            this.sink.propagateRetractObject( handle,
                                              context,
                                              workingMemory,
                                              true );
        }
    }

    public void modifyObject(final InternalFactHandle handle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        final FactHashSet memory = (FactHashSet) workingMemory.getNodeMemory( this );

        if ( this.constraint.isAllowed( handle.getObject(),
                                        null,
                                        workingMemory ) ) {
            if ( memory.add( handle, true ) ) {
                this.sink.propagateAssertObject( handle,
                                                 context,
                                                 workingMemory );
            } else {
                // handle already existed so propagate as modify
                this.sink.propagateModifyObject( handle,
                                                 context,
                                                 workingMemory );
            }
        } else {
            if ( memory.remove( handle ) ) {
                this.sink.propagateRetractObject( handle,
                                                  context,
                                                  workingMemory,
                                                  false );
            }
        }
    }

    public void updateNewNode(final InternalWorkingMemory workingMemory,
                              final PropagationContext context) {
        this.attachingNewNode = true;

        final FactHashSet memory = (FactHashSet) workingMemory.getNodeMemory( this );
        FactEntry[] entries  = memory.getTable();
        for ( int i = 0, length  = entries.length; i < length; i++ ) {
            FactEntry current = entries[i];
            while  ( current != null ) {
                this.sink.propagateNewObjectSink( current.getFactHandle(),
                                                  context,
                                                  workingMemory );
                current = ( FactEntry ) current.getNext();
            }
        }        

        this.attachingNewNode = false;
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
        return new FactHashSet();
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
}
