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
import java.util.ArrayList;
import java.util.List;

import org.drools.FactException;
import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.facttemplates.Fact;
import org.drools.facttemplates.FactImpl;
import org.drools.spi.PropagationContext;
import org.drools.util.Iterator;
import org.drools.util.ObjectHashMap;
import org.drools.util.ObjectHashMap.ObjectEntry;

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
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
class Rete extends ObjectSource
    implements
    Serializable,
    ObjectSink,
    NodeMemory {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     * 
     */
    private static final long        serialVersionUID = 320L;
    /** The <code>Map</code> of <code>ObjectTypeNodes</code>. */
    private final ObjectHashMap      objectTypeNodes;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    public Rete() {
        super( 0 );
        this.objectTypeNodes = new ObjectHashMap();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * This is the entry point into the network for all asserted Facts. Iterates a cache
     * of matching <code>ObjectTypdeNode</code>s asserting the Fact. If the cache does not
     * exist it first iteraes and builds the cache.
     * 
     * @param handle
     *            The FactHandle of the fact to assert
     * @param context
     *            The <code>PropagationContext</code> of the <code>WorkingMemory</code> action   
     * @param workingMemory
     *            The working memory session.
     */
    public void assertObject(final InternalFactHandle handle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        final ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );

        final Object object = handle.getObject();
        
        Object key = null;
            
        if  ( object.getClass() == FactImpl.class ) {
            key = ( ( Fact ) object ).getFactTemplate().getName();
        } else {
            key = object.getClass();
        }
        
        ObjectTypeNode[] cachedNodes = (ObjectTypeNode[]) memory.get( key );
        if ( cachedNodes == null ) {
            cachedNodes = getMatchingNodes( object );
            memory.put( key,
                        cachedNodes,
                        false );
        }

        for ( int i = 0, length = cachedNodes.length; i < length; i++ ) {
            cachedNodes[i].assertObject( handle,
                                         context,
                                         workingMemory );
        }
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
        final ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );

        final Object object = handle.getObject();

        ObjectTypeNode[] cachedNodes = (ObjectTypeNode[]) memory.get( object.getClass() );
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

    private ObjectTypeNode[] getMatchingNodes(final Object object) throws FactException {
        final List cache = new ArrayList();

        Iterator it = this.objectTypeNodes.iterator();
        for ( ObjectEntry entry = (ObjectEntry)it.next(); entry != null; entry =  (ObjectEntry)it.next() ) {
            final ObjectTypeNode node = (ObjectTypeNode) entry.getValue();
            if ( node.matches( object ) ) {
                cache.add( node );
            }            
        }
        
        return (ObjectTypeNode[]) cache.toArray( new ObjectTypeNode[cache.size()] );
    }   

    /**
     * Adds the <code>TupleSink</code> so that it may receive
     * <code>Tuples</code> propagated from this <code>TupleSource</code>.
     * 
     * @param tupleSink
     *            The <code>TupleSink</code> to receive propagated
     *            <code>Tuples</code>.
     */
    protected void addObjectSink(final ObjectSink objectSink) {
        ObjectTypeNode node = ( ObjectTypeNode  ) objectSink;
        this.objectTypeNodes.put( node.getObjectType(),
                                  node,
                                  true );
    }

    protected void removeObjectSink(final ObjectSink objectSink) {
        this.objectTypeNodes.remove( objectSink );
    }

    public void attach() {
        throw new UnsupportedOperationException( "cannot call attach() from the root Rete node");
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        throw new UnsupportedOperationException( "cannot call attach() from the root Rete node");        
    }

    public void remove(final BaseNode node,
                       final InternalWorkingMemory[] workingMemories) {
        final ObjectTypeNode objectTypeNode = (ObjectTypeNode) node;                        
        removeObjectSink( objectTypeNode );
        //@todo: we really should attempt to clear the memory cache for this ObjectTypeNode        
    }

    public Object createMemory(final RuleBaseConfiguration config) {
        return new ObjectHashMap();
    }

    public int hashCode() {
        return this.objectTypeNodes.hashCode();
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || object.getClass() != Rete.class ) {
            return false;
        }

        final Rete other = (Rete) object;
        return this.objectTypeNodes.equals( other.objectTypeNodes );
    }

    public void updateSink(ObjectSink sink,
                           PropagationContext context,
                           InternalWorkingMemory workingMemory) {
        ObjectTypeNode node  = ( ObjectTypeNode) sink;
        for ( final java.util.Iterator i = workingMemory.getFactHandleMap().entrySet().iterator(); i.hasNext(); ) {
            final java.util.Map.Entry entry = (java.util.Map.Entry) i.next();
            final InternalFactHandle handle = (InternalFactHandle) entry.getValue();
            if ( node.matches( handle.getObject() ) ) {                
                node.assertObject( handle,
                                   context,
                                   workingMemory );
            }
        }                
    }

}
