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

import org.drools.FactException;
import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.ContextEntry;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.PropagationContext;
import org.drools.util.FactEntry;
import org.drools.util.FactHashTable;
import org.drools.util.Iterator;

/**
 * <code>AlphaNodes</code> are nodes in the <code>Rete</code> network used
 * to apply <code>FieldConstraint<.code>s on asserted fact 
 * objects where the <code>FieldConstraint</code>s have no dependencies on any other of the facts in the current <code>Rule</code>.
 * 
 *  @see AlphaNodeFieldConstraint
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public class AlphaNode extends ObjectSource
    implements
    ObjectSinkNode,
    NodeMemory {

    /**
     * 
     */
    private static final long              serialVersionUID = 400L;

    /** The <code>FieldConstraint</code> */
    private final AlphaNodeFieldConstraint constraint;

    private ObjectSinkNode                 previousObjectSinkNode;
    private ObjectSinkNode                 nextObjectSinkNode;

    private boolean                        objectMemoryEnabled;

    private boolean                        objectMemoryAllowed;

    /**
     * Construct an <code>AlphaNode</code> with a unique id using the provided
     * <code>FieldConstraint</code> and the given <code>ObjectSource</code>.
     * Set the boolean flag to true if the node is supposed to have local 
     * memory, or false otherwise. Memory is optional for <code>AlphaNode</code>s 
     * and is only of benefic when adding additional <code>Rule</code>s at runtime. 
     * 
     * @param id Node's ID
     * @param constraint Node's constraints
     * @param objectSource Node's object source
     * @param hasMemory true if node shall be configured with local memory. False otherwise.
     */
    public AlphaNode(final int id,
                     final AlphaNodeFieldConstraint constraint,
                     final ObjectSource objectSource,
                     final BuildContext context) {
        super( id,
               objectSource,
               context.getRuleBase().getConfiguration().getAlphaNodeHashingThreshold() );
        this.constraint = constraint;
        this.objectMemoryAllowed = context.isAlphaMemoryAllowed();
        if ( this.objectMemoryAllowed ) {
            this.objectMemoryEnabled = context.getRuleBase().getConfiguration().isAlphaMemory();
        } else {
            this.objectMemoryEnabled = false;
        }
    }

    /**
     * Retruns the <code>FieldConstraint</code>
     * 
     * @return <code>FieldConstraint</code>
     */
    public AlphaNodeFieldConstraint getConstraint() {
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
        // indicating that we are in a dynamic environment, that might benefit from alpha node memory, if allowed
        if ( this.objectMemoryAllowed ) {
            setObjectMemoryEnabled( true );
        }
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
        final AlphaMemory memory = (AlphaMemory) workingMemory.getNodeMemory( this );
        if ( this.constraint.isAllowed( handle,
                                        workingMemory,
                                        memory.context ) ) {
            if ( isObjectMemoryEnabled() ) {
                memory.facts.add( handle,
                                  false );
            }

            this.sink.propagateAssertObject( handle,
                                             context,
                                             workingMemory );
        }
    }

    public void retractObject(final InternalFactHandle handle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory) {
        boolean propagate = true;
        final AlphaMemory memory = (AlphaMemory) workingMemory.getNodeMemory( this );
        if ( isObjectMemoryEnabled() ) {
            propagate = memory.facts.remove( handle );
        } else {
            propagate = this.constraint.isAllowed( handle,
                                                   workingMemory,
                                                   memory.context );
        }
        if ( propagate ) {
            this.sink.propagateRetractObject( handle,
                                              context,
                                              workingMemory,
                                              true );
        }
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        AlphaMemory memory = (AlphaMemory) workingMemory.getNodeMemory( this );

        if ( !isObjectMemoryEnabled() ) {
            // get the objects from the parent
            ObjectSinkUpdateAdapter adapter = new ObjectSinkUpdateAdapter( sink,
                                                                           this.constraint,
                                                                           memory.context );
            this.objectSource.updateSink( adapter,
                                          context,
                                          workingMemory );
        } else {
            // if already has memory, just iterate and propagate
            final Iterator it = memory.facts.iterator();
            for ( FactEntry entry = (FactEntry) it.next(); entry != null; entry = (FactEntry) it.next() ) {
                sink.assertObject( entry.getFactHandle(),
                                   context,
                                   workingMemory );
            }
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
            for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
                workingMemories[i].clearNodeMemory( this );
            }
        }
        this.objectSource.remove( context,
                                  builder,
                                  this,
                                  workingMemories );
    }

    public void setObjectMemoryAllowed(boolean objectMemoryAllowed) {
        this.objectMemoryAllowed = objectMemoryAllowed;
    }

    public boolean isObjectMemoryEnabled() {
        return this.objectMemoryEnabled;
    }

    public void setObjectMemoryEnabled(boolean objectMemoryEnabled) {
        this.objectMemoryEnabled = objectMemoryEnabled;
    }

    /**
     * Creates a HashSet for the AlphaNode's memory.
     */
    public Object createMemory(final RuleBaseConfiguration config) {
        AlphaMemory memory = new AlphaMemory();
        memory.context = this.constraint.createContextEntry();
        if ( this.objectMemoryEnabled ) {
            memory.facts = new FactHashTable();
        }
        return memory;
    }

    /** 
     * @inheritDoc
     */
    protected void addObjectSink(final ObjectSink objectSink) {
        super.addObjectSink( objectSink );
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

        if ( object == null || !(object instanceof AlphaNode) ) {
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
    public void setNextObjectSinkNode(final ObjectSinkNode next) {
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
    public void setPreviousObjectSinkNode(final ObjectSinkNode previous) {
        this.previousObjectSinkNode = previous;
    }

    public static class AlphaMemory
        implements
        Serializable {
        private static final long serialVersionUID = -5852576405010023458L;

        public FactHashTable      facts;
        public ContextEntry       context;

    }

    /**
     * Used with the updateSink method, so that the parent ObjectSource
     * can  update the  TupleSink
     * @author mproctor
     *
     */
    private static class ObjectSinkUpdateAdapter
        implements
        ObjectSink {
        private final ObjectSink               sink;
        private final AlphaNodeFieldConstraint constraint;
        private final ContextEntry             alphaContext;

        public ObjectSinkUpdateAdapter(final ObjectSink sink,
                                       final AlphaNodeFieldConstraint constraint,
                                       final ContextEntry context ) {
            this.sink = sink;
            this.constraint = constraint;
            this.alphaContext = context;
        }

        public void assertObject(final InternalFactHandle handle,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
            if ( this.constraint.isAllowed( handle,
                                            workingMemory,
                                            this.alphaContext ) ) {
                this.sink.assertObject( handle,
                                        context,
                                        workingMemory );
            }
        }

        public void modifyObject(final InternalFactHandle handle,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
            throw new UnsupportedOperationException( "ObjectSinkUpdateAdapter onlys supports assertObject method calls" );
        }

        public void retractObject(final InternalFactHandle handle,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory) {
            throw new UnsupportedOperationException( "ObjectSinkUpdateAdapter onlys supports assertObject method calls" );
        }

        public boolean isObjectMemoryEnabled() {
            throw new UnsupportedOperationException( "ObjectSinkUpdateAdapter have no Object memory" );
        }

        public void setObjectMemoryEnabled(boolean objectMemoryEnabled) {
            throw new UnsupportedOperationException( "ObjectSinkUpdateAdapter have no Object memory" );
        }
    }
}
