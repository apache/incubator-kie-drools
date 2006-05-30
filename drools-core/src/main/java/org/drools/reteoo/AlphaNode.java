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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.FactException;
import org.drools.RuleBaseConfiguration;
import org.drools.common.DefaultFactHandle;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.spi.FieldConstraint;
import org.drools.spi.PropagationContext;

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
    ObjectSink,
    NodeMemory {

    /**
     * 
     */
    private static final long     serialVersionUID = 8936511451364612838L;

    /** The <code>FieldConstraint</code> */
    private final FieldConstraint constraint;

    /** The <code>ObjectSource</code> */
    private final ObjectSource    objectSource;

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
        this( id,
              null,
              constraint,
              objectSource );
    }

    /**
     * Construct an <code>AlphaNode</code> with a unique id using the provided
     * <code>ObjectSinkList</code> and <code>FieldConstraint</code>. 
     * 
     * @param id Node unique id
     * @param sinklist An object sink list. If null, a default will be used.
     * @param constraint Node's constraints
     * @param objectSource Node's object source
     */
    AlphaNode(final int id,
              final ObjectSinkList sinklist,
              final FieldConstraint constraint,
              final ObjectSource objectSource) {
        super( id,
               sinklist );
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

    public void attach(final ReteooWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final ReteooWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null );
            this.objectSource.updateNewNode( workingMemory,
                                             propagationContext );
        }
    }

    public void assertObject(final DefaultFactHandle handle,
                             final PropagationContext context,
                             final ReteooWorkingMemory workingMemory) throws FactException {
        final Set memory = (Set) workingMemory.getNodeMemory( this );
        if ( this.constraint.isAllowed( handle,
                                        null,
                                        workingMemory ) ) {
            memory.add( handle );
            propagateAssertObject( handle,
                                   context,
                                   workingMemory );
        }
    }

    public void retractObject(final DefaultFactHandle handle,
                              final PropagationContext context,
                              final ReteooWorkingMemory workingMemory) {
        final Set memory = (Set) workingMemory.getNodeMemory( this );
        if ( memory.remove( handle ) ) {
            propagateRetractObject( handle,
                                    context,
                                    workingMemory );
        }
    }

    public void modifyObject(final DefaultFactHandle handle,
                             final PropagationContext context,
                             final ReteooWorkingMemory workingMemory) {
        final Set memory = (Set) workingMemory.getNodeMemory( this );

        if ( this.constraint.isAllowed( handle,
                                        null,
                                        workingMemory ) ) {
            if ( memory.add( handle ) ) {
                propagateAssertObject( handle,
                                       context,
                                       workingMemory );
            } else {
                // handle already existed so propagate as modify
                propagateModifyObject( handle,
                                       context,
                                       workingMemory );
            }
        } else {
            if ( memory.remove( handle ) ) {
                propagateRetractObject( handle,
                                        context,
                                        workingMemory );
            }
        }
    }

    public void updateNewNode(final ReteooWorkingMemory workingMemory,
                              final PropagationContext context) {
        this.attachingNewNode = true;

        final Set memory = (Set) workingMemory.getNodeMemory( this );

        for ( final Iterator it = memory.iterator(); it.hasNext(); ) {
            final DefaultFactHandle handle = (DefaultFactHandle) it.next();
            final ObjectSink sink = this.objectSinks.getLastObjectSink();
            if ( sink != null ) {
                sink.assertObject( handle,
                                   context,
                                   workingMemory );
            } else {
                throw new RuntimeException( "Possible BUG: trying to propagate an assert to a node that was the last added node" );
            }
        }

        this.attachingNewNode = false;
    }

    public void remove(final BaseNode node,
                       final ReteooWorkingMemory[] workingMemories) {
        this.objectSinks.remove( (ObjectSink) node );
        removeShare();
        if ( this.sharedCount < 0 ) {
            for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
                workingMemories[i].clearNodeMemory( this );
            }
            this.objectSource.remove( this,
                                      workingMemories );
        }
    }

    /**
     * Creates a HashSet for the AlphaNode's memory.
     */
    public Object createMemory(final RuleBaseConfiguration config) {
        return new HashSet();
    }

    public String toString() {
        return "[AlphaNode constraint=" + this.constraint + "]";
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
}
