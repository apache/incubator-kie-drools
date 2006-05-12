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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.common.BetaNodeBinder;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.EvalCondition;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListNode;
import org.drools.util.LinkedListObjectWrapper;

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

    private final ObjectSource   objectSource;
    private final BetaNodeBinder binder;

    /**
     * Constructus a LeftInputAdapterNode with a unique id that receives <code>FactHandle</code> from a 
     * parent <code>ObjectSource</code> and adds it to a given column in the resulting Tuples.
     * 
     * @param id
     *      The unique id of this node in the current Rete network
     * @param source
     *      The parent node, where Facts are propagated from
     */
    public LeftInputAdapterNode(int id,
                                ObjectSource source) {
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
    public LeftInputAdapterNode(int id,
                                ObjectSource source,
                                BetaNodeBinder binder) {
        super( id );
        this.objectSource = source;
        this.binder = binder;
        setHasMemory( true );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#attach()
     */
    public void attach() {
        this.objectSource.addObjectSink( this );
    }

    public void attach(WorkingMemoryImpl[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            WorkingMemoryImpl workingMemory = workingMemories[i];
            PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                PropagationContext.RULE_ADDITION,
                                                                                null,
                                                                                null );
            this.objectSource.updateNewNode( workingMemory,
                                             propagationContext );
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
    public void assertObject(FactHandleImpl handle,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        Map memory = (Map) workingMemory.getNodeMemory( this );

        if ( (this.binder == null) || (this.binder.isAllowed( handle,
                                                              null,
                                                              workingMemory )) ) {
            createAndAssertTuple( handle,
                                  context,
                                  workingMemory,
                                  memory );
        }
    }

    /**
     * A private helper method to avoid code duplication between assert and 
     * modify object methods
     * 
     * @param handle
     * @param context
     * @param workingMemory
     * @param memory
     */
    private void createAndAssertTuple(FactHandleImpl handle,
                                      PropagationContext context,
                                      WorkingMemoryImpl workingMemory,
                                      Map memory) {
        int size = getTupleSinks().size();

        LinkedList list = new LinkedList();

        ReteTuple tuple = new ReteTuple( handle );

        list.add( new LinkedListObjectWrapper( tuple ) );

        if ( !getTupleSinks().isEmpty() ) {
            // We do this one seperately so we avoid another tuple replication
            ((TupleSink) getTupleSinks().get( 0 )).assertTuple( tuple,
                                                                context,
                                                                workingMemory );

            for ( int i = 1; i < size; i++ ) {
                tuple = new ReteTuple( tuple );
                list.add( new LinkedListObjectWrapper( tuple ) );
                ((TupleSink) getTupleSinks().get( i )).assertTuple( tuple,
                                                                    context,
                                                                    workingMemory );
            }
        }

        memory.put( handle,
                    list );
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
    public void retractObject(FactHandleImpl handle,
                              PropagationContext context,
                              WorkingMemoryImpl workingMemory) {
        Map memory = (Map) workingMemory.getNodeMemory( this );
        LinkedList list = (LinkedList) memory.remove( handle );

        // the handle might have been filtered out by the binder
        if ( list != null ) {
            int i = 0;
            for ( LinkedListNode node = list.removeFirst(); node != null; node = list.removeFirst() ) {
                ((TupleSink) getTupleSinks().get( i++ )).retractTuple( (ReteTuple) ((LinkedListObjectWrapper) node).getObject(),
                                                                       context,
                                                                       workingMemory );
            }
        }
    }

    public void modifyObject(FactHandleImpl handle,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        Map memory = (Map) workingMemory.getNodeMemory( this );

        if ( (this.binder == null) || (this.binder.isAllowed( handle,
                                                              null,
                                                              workingMemory )) ) {
            LinkedList list = (LinkedList) memory.get( handle );

            if ( list != null ) {
                // already existed, so propagate as a modify
                int i = 0;
                for ( LinkedListNode node = list.getFirst(); node != null; node = node.getNext() ) {
                    ((TupleSink) getTupleSinks().get( i++ )).modifyTuple( (ReteTuple) ((LinkedListObjectWrapper) node).getObject(),
                                                                          context,
                                                                          workingMemory );
                }
            } else {
                // didn't existed, so propagate as an assert
                this.createAndAssertTuple( handle,
                                           context,
                                           workingMemory,
                                           memory );
            }
        } else {
            LinkedList list = (LinkedList) memory.remove( handle );

            if ( list != null ) {
                int i = 0;
                for ( LinkedListNode node = list.getFirst(); node != null; node = node.getNext() ) {
                    ((TupleSink) getTupleSinks().get( i++ )).retractTuple( (ReteTuple) ((LinkedListObjectWrapper) node).getObject(),
                                                                           context,
                                                                           workingMemory );
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#updateNewNode(org.drools.reteoo.WorkingMemoryImpl, org.drools.spi.PropagationContext)
     */
    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) {
        this.attachingNewNode = true;

        // Get the newly attached TupleSink
        TupleSink sink = (TupleSink) getTupleSinks().get( getTupleSinks().size() - 1 );

        // Iterate the memory and assert all tuples into the newly attached TupleSink
        Map memory = (Map) workingMemory.getNodeMemory( this );
        for ( Iterator it = memory.values().iterator(); it.hasNext(); ) {
            LinkedList list = (LinkedList) it.next();
            for ( LinkedListNode node = list.getFirst(); node != null; node = node.getNext() ) {
                sink.assertTuple( (ReteTuple) ((LinkedListObjectWrapper) node).getObject(),
                                  context,
                                  workingMemory );
            }
        }

        this.attachingNewNode = false;
    }

    public void remove(BaseNode node,
                       WorkingMemoryImpl[] workingMemories) {
        getTupleSinks().remove( node );
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
     * LeftInputAdapter uses a HashMap for memory. The key is the received <code>FactHandleImpl</code> and the
     * created <code>ReteTuple</code> is the value.
     */
    public Object createMemory() {
        return new HashMap();
    }
    
    public int hashCode() {
        return this.objectSource.hashCode();
    }
    
    public boolean equals(Object object) {
        if ( object == this ){
            return true;
        }
        
        if ( object == null || object.getClass() != LeftInputAdapterNode.class ) {
            return false;
        }
        
        LeftInputAdapterNode other = ( LeftInputAdapterNode ) object;
        if ( this.binder == null ) {
            return this.objectSource.equals( other.objectSource ) && other.binder == null;
        } else {        
            return this.objectSource.equals( other.objectSource ) && this.binder.equals( other.binder );
        }
    }

    /**
     * @inheritDoc
     */
    public List getPropagatedTuples(WorkingMemoryImpl workingMemory, TupleSink sink) {
        Map memory = (Map) workingMemory.getNodeMemory( this );
        int index = this.getTupleSinks().indexOf( sink );
        List propagatedTuples = new ArrayList();

        for( Iterator it = memory.values().iterator(); it.hasNext(); ) {
            LinkedList tuples = (LinkedList) it.next();
            LinkedListObjectWrapper wrapper = (LinkedListObjectWrapper) tuples.getFirst();
            for( int i = 0; i < index; i++) {
                wrapper = (LinkedListObjectWrapper) wrapper.getNext();
            }
            propagatedTuples.add( wrapper.getObject() );
        }
        
        return propagatedTuples;
    }   
}
