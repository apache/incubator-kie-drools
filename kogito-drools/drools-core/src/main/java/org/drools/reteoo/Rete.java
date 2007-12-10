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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.drools.base.ShadowProxy;
import org.drools.common.BaseNode;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.drools.util.FactEntry;
import org.drools.util.FactHashTable;
import org.drools.util.Iterator;
import org.drools.util.ObjectHashMap;

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
public class Rete extends ObjectSource
    implements
    Serializable,
    ObjectSink {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     *
     */
    private static final long          serialVersionUID = 400L;
    /** The <code>Map</code> of <code>ObjectTypeNodes</code>. */
    private final ObjectHashMap        objectTypeNodes;

    private transient InternalRuleBase ruleBase;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    public Rete(InternalRuleBase ruleBase) {
        super( 0 );
        this.objectTypeNodes = new ObjectHashMap();
        this.ruleBase = ruleBase;
    }

    private void readObject(ObjectInputStream stream) throws IOException,
                                                     ClassNotFoundException {
        stream.defaultReadObject();
        this.ruleBase = ((DroolsObjectInputStream) stream).getRuleBase();
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

        ObjectTypeConf objectTypeConf = workingMemory.getObjectTypeConf( handle.getObject() );

        // checks if shadow is enabled
        if ( objectTypeConf.isShadowEnabled() ) {
            // need to improve this
            if ( !(handle.getObject() instanceof ShadowProxy) ) {
                // replaces the actual object by its shadow before propagating
                handle.setObject( objectTypeConf.getShadow( handle.getObject() ) );
                handle.setShadowFact( true );
            } else {
                ((ShadowProxy) handle.getObject()).updateProxy();
            }
        }

        ObjectTypeNode[] cachedNodes = objectTypeConf.getObjectTypeNodes();

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
        final Object object = handle.getObject();

        ObjectTypeConf objectTypeConf = workingMemory.getObjectTypeConf( object );
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
     * Adds the <code>TupleSink</code> so that it may receive
     * <code>Tuples</code> propagated from this <code>TupleSource</code>.
     *
     * @param tupleSink
     *            The <code>TupleSink</code> to receive propagated
     *            <code>Tuples</code>.
     */
    protected void addObjectSink(final ObjectSink objectSink) {
        final ObjectTypeNode node = (ObjectTypeNode) objectSink;
        this.objectTypeNodes.put( node.getObjectType(),
                                  node,
                                  true );
    }

    protected void removeObjectSink(final ObjectSink objectSink) {
        final ObjectTypeNode node = (ObjectTypeNode) objectSink;
        this.objectTypeNodes.remove( node.getObjectType() );
    }

    public void attach() {
        throw new UnsupportedOperationException( "cannot call attach() from the root Rete node" );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        throw new UnsupportedOperationException( "cannot call attach() from the root Rete node" );
    }

    public void remove(final BaseNode node,
                       final InternalWorkingMemory[] workingMemories) {
        final ObjectTypeNode objectTypeNode = (ObjectTypeNode) node;
        removeObjectSink( objectTypeNode );
        for ( int i = 0; i < workingMemories.length; i++ ) {
            // clear the node memory for each working memory.
            workingMemories[i].clearNodeMemory( (NodeMemory) node );
        }
    }

    public ObjectHashMap getObjectTypeNodes() {
        return this.objectTypeNodes;
    }

    public InternalRuleBase getRuleBase() {
        return this.ruleBase;
    }

    public int hashCode() {
        return this.objectTypeNodes.hashCode();
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || !(object instanceof Rete) ) {
            return false;
        }

        final Rete other = (Rete) object;
        return this.objectTypeNodes.equals( other.objectTypeNodes );
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        // JBRULES-612: the cache MUST be invalidated when a new node type is added to the network, so iterate and reset all caches.
        final ObjectTypeNode node = (ObjectTypeNode) sink;
        final ObjectType newObjectType = node.getObjectType();

        for ( ObjectTypeConf objectTypeConf : workingMemory.getObjectTypeConfMap().values() ) {
            if ( newObjectType.isAssignableFrom( objectTypeConf.getConcreteObjectTypeNode().getObjectType() ) ) {
                objectTypeConf.resetCache();
                ObjectTypeNode sourceNode = objectTypeConf.getConcreteObjectTypeNode();
                FactHashTable table = (FactHashTable) workingMemory.getNodeMemory( sourceNode );
                Iterator factIter = table.iterator();
                for ( FactEntry factEntry = (FactEntry) factIter.next(); factEntry != null; factEntry = (FactEntry) factIter.next() ) {
                    sink.assertObject( factEntry.getFactHandle(),
                                       context,
                                       workingMemory );
                }
            }
        }
    }
    
    public boolean isObjectMemoryEnabled() {
        throw new UnsupportedOperationException("Rete has no Object memory");
    }

    public void setObjectMemoryEnabled(boolean objectMemoryEnabled) {
        throw new UnsupportedOperationException("ORete has no Object memory");
    }

}
