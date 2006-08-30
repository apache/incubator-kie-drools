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

import org.drools.common.DefaultFactHandle;
import org.drools.spi.PropagationContext;

/**
 * When joining two <code>Not<code>s together the resulting <code>Tuple</code> from the first Not
 * Must be adapted to <code>FActHandleImpl</code> so it can be propagated into the second not
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public class RightInputAdapterNode extends ObjectSource
    implements
    TupleSink {
    /**
     * 
     */
    private static final long serialVersionUID = 5777695177854269219L;

    private final TupleSource tupleSource;

    private final int         column;

    /**
     * Constructor specifying the unique id of the node in the Rete network, the position of the propagating <code>FactHandleImpl</code> in
     * <code>ReteTuple</code> and the source that propagates the receive <code>ReteTuple<code>s.
     * 
     * @param id
     *      Unique id 
     * @param column
     *      The column which specifis the position of the <code>FactHandleImpl</code> in the <code>ReteTuple</code>
     * @param source
     *      The <code>TupleSource</code> which propagates the received <code>ReteTuple</code>
     */
    public RightInputAdapterNode(final int id,
                                 final int column,
                                 final TupleSource source) {

        super( id );
        this.column = column;
        this.tupleSource = source;
    }

    /**
     * Takes the asserted <code>ReteTuple</code> received from the <code>TupleSource</code> and extract and propagate
     * its <code>FactHandleImpl</code> based on the column specified by te node.
     * 
     * @param tuple
     *            The asserted <code>ReteTuple</code>.
     * @param context
     *             The <code>PropagationContext</code> of the <code>WorkingMemory<code> action.           
     * @param workingMemory
     *            the <code>WorkingMemory</code> session.
     */
    public void assertTuple(final ReteTuple tuple,
                            final PropagationContext context,
                            final ReteooWorkingMemory workingMemory) {
        propagateAssertObject( (DefaultFactHandle) tuple.get( this.column ),
                               context,
                               workingMemory );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.RetractionCallback#retractTuple(org.drools.reteoo.ReteTuple, org.drools.spi.PropagationContext, org.drools.reteoo.WorkingMemoryImpl)
     */
    public void retractTuple(final ReteTuple tuple,
                             final PropagationContext context,
                             final ReteooWorkingMemory workingMemory) {
        propagateRetractObject( (DefaultFactHandle) tuple.get( this.column ),
                                context,
                                workingMemory );
    }

    public void modifyTuple(final ReteTuple tuple,
                            final PropagationContext context,
                            final ReteooWorkingMemory workingMemory) {
        propagateModifyObject( (DefaultFactHandle) tuple.get( this.column ),
                               context,
                               workingMemory );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#attach()
     */
    public void attach() {
        this.tupleSource.addTupleSink( this );
    }

    public void attach(final ReteooWorkingMemory[] workingMemories) {
        attach();
        // this node has no memory, no point requesting repropagation     
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#updateNewNode(org.drools.reteoo.WorkingMemoryImpl, org.drools.spi.PropagationContext)
     */
    public void updateNewNode(final ReteooWorkingMemory workingMemory,
                              final PropagationContext context) {
        // this node has no memory, so we need to get the parent node to repropagate. We simulate this be re-attaching
        this.attachingNewNode = true;
        // We need to detach and re-attach to make sure the node is at the top
        // for the propagation
        this.tupleSource.removeTupleSink( this );
        this.tupleSource.addTupleSink( this );
        this.tupleSource.updateNewNode( workingMemory,
                                        context );
        this.attachingNewNode = false;
    }

    public void remove(final BaseNode node,
                       final ReteooWorkingMemory[] workingMemories) {
        if( !node.isInUse() ) {
            getObjectSinks().remove( (ObjectSink) node );
        }
        removeShare();
        this.tupleSource.remove( this,
                                 workingMemories );
    }

}
