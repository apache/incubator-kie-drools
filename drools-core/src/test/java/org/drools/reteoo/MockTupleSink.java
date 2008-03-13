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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.drools.FactException;
import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.spi.PropagationContext;

public class MockTupleSink extends TupleSource
    implements
    TupleSinkNode,
    NodeMemory {
    /**
     * 
     */
    private static final long serialVersionUID = 400L;
    private final List        asserted         = new ArrayList();
    private final List        retracted        = new ArrayList();

    private TupleSinkNode     previousTupleSinkNode;
    private TupleSinkNode     nextTupleSinkNode;

    public MockTupleSink() {
        super( 0 );
    }

    public MockTupleSink(final int id) {
        super( id );
    }

    public void assertTuple(final ReteTuple tuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        this.asserted.add( new Object[]{tuple, context, workingMemory} );

    }

    public void retractTuple(final ReteTuple tuple,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        this.retracted.add( new Object[]{tuple, context, workingMemory} );

    }

    public List getAsserted() {
        return this.asserted;
    }

    public List getRetracted() {
        return this.retracted;
    }

    public void ruleAttached() {
        // TODO Auto-generated method stub
    }

    public int getId() {
        return this.id;
    }

    public Object createMemory(final RuleBaseConfiguration config) {
        return new HashMap();
    }

    public void attach() {
        // TODO Auto-generated method stub

    }

    public void updateSink(final TupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) throws FactException {
        // TODO Auto-generated method stub

    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        // TODO Auto-generated method stub

    }

    public List getPropagatedTuples(final ReteooWorkingMemory workingMemory,
                                    final TupleSink sink) {
        // TODO Auto-generated method stub
        return Collections.EMPTY_LIST;
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public TupleSinkNode getNextTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node 
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextTupleSinkNode(final TupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public TupleSinkNode getPreviousTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node 
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousTupleSinkNode(final TupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

    public boolean isTupleMemoryEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void networkUpdated() {
        // TODO Auto-generated method stub
        
    }

}