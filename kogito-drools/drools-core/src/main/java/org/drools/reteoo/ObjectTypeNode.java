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

import java.io.Serializable;
import java.util.Iterator;

import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.drools.util.PrimitiveLongMap;

/**
 * <code>ObjectTypeNodes<code> are responsible for filtering and propagating the matching
 * fact assertions propagated from the <code>Rete</code> node using <code>ObjectType</code> interface.
 * <p>
 * The assert and retract methods do not attempt to filter as this is the role of the <code>Rete</code>
 * node which builds up a cache of matching <code>ObjectTypdeNodes</code>s for each asserted object, using
 * the <code>matches(Object object)</code> method. Incorrect propagation in these methods is not checked and
 * will result in <code>ClassCastExpcections</code> later on in the network.
 * <p>
 * Filters <code>Objects</code> coming from the <code>Rete</code> using a
 * <code>ObjectType</code> semantic module.
 * 
 * 
 * @see ObjectType
 * @see Rete
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
class ObjectTypeNode extends ObjectSource
    implements
    ObjectSink,
    Serializable,
    NodeMemory

{
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The <code>ObjectType</code> semantic module. */
    private final ObjectType objectType;

    /** The parent Rete node */
    private final Rete       rete;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct given a semantic <code>ObjectType</code> and the provided
     * unique id. All <code>ObjectTypdeNode</code> have node memory.
     * 
     * @param id
     *          The unique id for the node.
     * @param objectType
     *           The semantic object-type differentiator.
     */
    public ObjectTypeNode(int id,
                          ObjectType objectType,
                          Rete rete) {
        super( id );
        this.rete = rete;
        this.objectType = objectType;
        setHasMemory( true );
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the semantic <code>ObjectType</code> differentiator.
     * 
     * @return 
     *      The semantic <code>ObjectType</code> differentiator.
     */
    public ObjectType getObjectType() {
        return this.objectType;
    }

    /**
     * Tests the provided object to see if this <code>ObjectTypeNode</code> can receive the object
     * for assertion and retraction propagations.
     * 
     * @param object
     * @return
     *      boolean value indicating whether the <code>ObjectTypeNode</code> can receive the object.
     */
    public boolean matches(Object object) {
        return this.objectType.matches( object );
    }

    /**
     * Propagate the <code>FactHandleimpl</code> through the <code>Rete</code> network. All
     * <code>FactHandleImpl</code> should be remembered in the node memory, so that later runtime rule attachmnents
     * can have the matched facts propagated to them.
     * 
     * @param handle
     *            The fact handle.
     * @param object
     *            The object to assert.
     * @param workingMemory
     *            The working memory session.
     */
    public void assertObject(FactHandleImpl handle,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        PrimitiveLongMap memory = (PrimitiveLongMap) workingMemory.getNodeMemory( this );
        memory.put( handle.getId(),
                    handle );

        propagateAssertObject( handle,
                               context,
                               workingMemory );
    }

    /**
     * Retract the <code>FactHandleimpl</code> from the <code>Rete</code> network. Also remove the 
     * <code>FactHandleImpl</code> from the node memory.
     * 
     * @param handle
     *            The fact handle.
     * @param object
     *            The object to assert.
     * @param workingMemory
     *            The working memory session.
     */
    public void retractObject(FactHandleImpl handle,
                              PropagationContext context,
                              WorkingMemoryImpl workingMemory) {
        PrimitiveLongMap memory = (PrimitiveLongMap) workingMemory.getNodeMemory( this );
        memory.remove( handle.getId() );

        propagateRetractObject( handle,
                                context,
                                workingMemory );
    }

    public void modifyObject(FactHandleImpl handle,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        PrimitiveLongMap memory = (PrimitiveLongMap) workingMemory.getNodeMemory( this );

        propagateModifyObject( handle,
                               context,
                               workingMemory );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#updateNewNode(org.drools.reteoo.WorkingMemoryImpl, org.drools.spi.PropagationContext)
     */
    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) {
        this.attachingNewNode = true;

        PrimitiveLongMap memory = (PrimitiveLongMap) workingMemory.getNodeMemory( this );

        for ( Iterator it = memory.values().iterator(); it.hasNext(); ) {
            FactHandleImpl handle = (FactHandleImpl) it.next();
            propagateAssertObject( handle,
                                   context,
                                   workingMemory );
        }

        this.attachingNewNode = false;
    }

    /**
     * Rete needs to know that this ObjectTypeNode has been added
     */
    public void attach() {
        this.rete.addObjectSink( this );
    }
    
    public void attach(WorkingMemoryImpl[] workingMemories) {
        attach();
        // Rete does not hold any data, so no need to propagate
    }     

    public void remove(BaseNode node,
                       WorkingMemoryImpl[] workingMemories) {
        getObjectSinks().remove( node );
        removeShare();
        if ( this.sharedCount < 0 ) {
            for ( int i = 0, length = workingMemories.length; i < length; i++) {
                workingMemories[i].clearNodeMemory( this );    
            }
            this.rete.removeObjectSink( this );
        }
    }
    /**
     * Rete needs to know that this ObjectTypeNode has had new nodes attached to
     * it one one of its ancestors
     */
    public void addShare() {
        super.addShare();
    }

    /**
     * Creates memory for the node using PrimitiveLongMap as its optimised for storage and reteivals of Longs.
     * However PrimitiveLongMap is not ideal for spase data. So it should be monitored incase its more optimal
     * to switch back to a standard HashMap.
     */
    public Object createMemory() {
        return new PrimitiveLongMap( 32,
                                     8 );
    }

    public String toString() {
        return "[ObjectTypeNode objectType=" + this.objectType + "]";
    }

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        return this.objectType.equals( this.objectType );
    }

    /**
     * Uses he hashcode() of the underlying ObjectType implementation.
     */
    public int hashCode() {
        return this.objectType.hashCode();
    }
}
