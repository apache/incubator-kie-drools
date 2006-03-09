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


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListNode;
import org.drools.util.LinkedListNodeWrapper;

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
    private final ObjectSource objectSource;

    /**
     * Constructus a LeftInputAdapterNode with a unique id that receives <code>FactHandle</code> from a 
     * parent <code>ObjectSource</code> and adds it to a given column in the resulting Tuples.
     * 
     * @param id
     *      The unique id of this node in the current Rete network
     * @param column
     *      The column which indicates the position of the FactHandle in the ReteTuple
     * @param source
     *      The parent node, where Facts are propagated from
     */
    public LeftInputAdapterNode(int id,
                                ObjectSource source) {
        super( id );        
        this.objectSource = source;
        setHasMemory( true );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#attach()
     */
    public void attach() {
        this.objectSource.addObjectSink( this );
    }
    
    public void attach(WorkingMemoryImpl[] workingMemories, PropagationContext context) {
        attach();
        
        for (int i = 0, length = 0; i < length; i++) { 
            this.objectSource.updateNewNode( workingMemories[i], context );
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

        int size = getTupleSinks().size();

        LinkedList list = new LinkedList();

        ReteTuple tuple = new ReteTuple( handle );

        list.add( new LinkedListNodeWrapper( tuple ) );

        ((TupleSink) getTupleSinks().get( 0 )).assertTuple( tuple,
                                                            context,
                                                            workingMemory );

        for ( int i = 1; i < size; i++ ) {
            tuple = new ReteTuple( tuple );
            list.add( new LinkedListNodeWrapper( tuple ) );
            ((TupleSink) getTupleSinks().get( i )).assertTuple( tuple,
                                                                context,
                                                                workingMemory );
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

        int i = 0;
        for ( LinkedListNode node = list.removeFirst(); node != null; node = list.removeFirst() ) {
            ((TupleSink) getTupleSinks().get( i++ )).retractTuple( (ReteTuple) ((LinkedListNodeWrapper) node).getNode(),
                                                                 context,
                                                                 workingMemory );
        }
    }
    
    public void modifyObject(FactHandleImpl handle,
                              PropagationContext context,
                              WorkingMemoryImpl workingMemory) {
        Map memory = (Map) workingMemory.getNodeMemory( this );
        LinkedList list = (LinkedList) memory.get( handle );

        int i = 0;
        for ( LinkedListNode node = list.removeFirst(); node != null; node = list.removeFirst() ) {
            ((TupleSink) getTupleSinks().get( i++ )).modifyTuple( (ReteTuple) ((LinkedListNodeWrapper) node).getNode(),
                                                                  context,
                                                                  workingMemory );
        }
    }    

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#updateNewNode(org.drools.reteoo.WorkingMemoryImpl, org.drools.spi.PropagationContext)
     */
    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) {
        this.attachingNewNode = true;

        Map memory = (Map) workingMemory.getNodeMemory( this );
        for ( Iterator it = memory.values().iterator(); it.hasNext(); ) {
            LinkedList list = (LinkedList) it.next();
            int i = 0;
            for ( LinkedListNode node = list.removeFirst(); node != null; node = list.removeFirst() ) {
                ((TupleSink) getTupleSinks().get( i++ )).modifyTuple( (ReteTuple) ((LinkedListNodeWrapper) node).getNode(),
                                                                      context,
                                                                      workingMemory );
            }            
        }    
        
        this.attachingNewNode = false;
    }

    public void remove(BaseNode node,
                       WorkingMemoryImpl workingMemory,
                       PropagationContext context) {
        getTupleSinks().remove( node );
        removeShare();
        if ( this.sharedCount < 0 ) {
            workingMemory.clearNodeMemory( this );
            this.objectSource.remove( this, workingMemory, context );
        }
    }


    /**
     * LeftInputAdapter uses a HashMap for memory. The key is the received <code>FactHandleImpl</code> and the
     * created <code>ReteTuple</code> is the value.
     */
    public Object createMemory() {
        return new HashMap();
    }
}
