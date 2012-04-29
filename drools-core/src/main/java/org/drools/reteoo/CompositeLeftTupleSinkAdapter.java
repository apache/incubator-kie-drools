/*
 * Copyright 2010 JBoss Inc
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

package org.drools.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.spi.PropagationContext;

public class CompositeLeftTupleSinkAdapter extends AbstractLeftTupleSinkAdapter {
    private LeftTupleSinkNodeList sinks;

    public CompositeLeftTupleSinkAdapter() {
        super( RuleBasePartitionId.MAIN_PARTITION );
    }

    public CompositeLeftTupleSinkAdapter(final RuleBasePartitionId partitionId) {
        super( partitionId );
        this.sinks = new LeftTupleSinkNodeList();
    }

    public void addTupleSink(final LeftTupleSink sink) {
        this.sinks.add( (LeftTupleSinkNode) sink );
    }

    public void removeTupleSink(final LeftTupleSink sink) {
        this.sinks.remove( (LeftTupleSinkNode) sink );
    }
    
    public void createChildLeftTuplesforQuery(final LeftTuple leftTuple,
                                              final RightTuple rightTuple,
                                              boolean leftTupleMemoryEnabled,
                                              boolean linkRightTuple) {
        // this must be in reverse order to ensure that there is correct child tuple iteration
        // for QueryElementNode. This is important for Accumulate now where it must propate to the right before the left input
        for ( LeftTupleSinkNode sink = this.sinks.getLast(); sink != null; sink = sink.getPreviousLeftTupleSinkNode() ) {
            LeftTuple child = sink.createLeftTuple( leftTuple,
                                                    rightTuple,
                                                    sink );
        }
    }
    
    public void modifyChildLeftTuplesforQuery(final RightTuple rightTuple,
                                              final PropagationContext context,
                                              final InternalWorkingMemory workingMemory) {
        LeftTuple childLeftTuple = rightTuple.firstChild;
        while ( childLeftTuple != null ) {
            childLeftTuple.getLeftTupleSink().modifyLeftTuple( childLeftTuple,
                                                               context,
                                                               workingMemory );            
            childLeftTuple = childLeftTuple.getRightParentNext();
        }        
    }

    public void propagateAssertLeftTuple(final LeftTuple leftTuple,
                                         final RightTuple rightTuple,
                                         final LeftTuple currentLeftChild,
                                         final LeftTuple currentRightChild,
                                         final PropagationContext context,
                                         final InternalWorkingMemory workingMemory,
                                         final boolean leftTupleMemoryEnabled) {

        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            LeftTuple newLeftTuple = sink.createLeftTuple( leftTuple,
                                                           rightTuple,
                                                           currentLeftChild,
                                                           currentRightChild,
                                                           sink,
                                                           leftTupleMemoryEnabled );
            doPropagateAssertLeftTuple( context,
                                        workingMemory,
                                        sink,
                                        newLeftTuple );
        }
    }

    public void propagateAssertLeftTuple(final LeftTuple tuple,
                                         final PropagationContext context,
                                         final InternalWorkingMemory workingMemory,
                                         final boolean leftTupleMemoryEnabled) {
        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            doPropagateAssertLeftTuple( context,
                                        workingMemory,
                                        sink,
                                        sink.createLeftTuple( tuple,
                                                              sink,
                                                              leftTupleMemoryEnabled ) );
        }
    }

    public void createAndPropagateAssertLeftTuple(final InternalFactHandle factHandle,
                                                  final PropagationContext context,
                                                  final InternalWorkingMemory workingMemory,
                                                  final boolean leftTupleMemoryEnabled,
                                                  LeftInputAdapterNode liaNode) {
        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            doPropagateAssertLeftTuple( context,
                                        workingMemory,
                                        sink,
                                        sink.createLeftTuple( factHandle,
                                                              sink,
                                                              leftTupleMemoryEnabled ) );
        }
    }

    public void propagateRetractLeftTuple(final LeftTuple leftTuple,
                                          final PropagationContext context,
                                          final InternalWorkingMemory workingMemory) {
        LeftTuple child = leftTuple.getFirstChild();
        while ( child != null ) {
            LeftTuple temp = child.getLeftParentNext();
            doPropagateRetractLeftTuple( context,
                                         workingMemory,
                                         child,
                                         child.getLeftTupleSink() );
            child.unlinkFromRightParent();
            child.unlinkFromLeftParent();
            child = temp;
        }
    }

    public void propagateRetractLeftTupleDestroyRightTuple(final LeftTuple leftTuple,
                                                           final PropagationContext context,
                                                           final InternalWorkingMemory workingMemory) {
        LeftTuple child = leftTuple.getFirstChild();
        InternalFactHandle rightParent = child.getRightParent().getFactHandle();
        while ( child != null ) {
            LeftTuple temp = child.getLeftParentNext();
            doPropagateRetractLeftTuple( context,
                                         workingMemory,
                                         child,
                                         child.getLeftTupleSink() );
            child.unlinkFromRightParent();
            child.unlinkFromLeftParent();
            child = temp;
        }
        //workingMemory.getFactHandleFactory().destroyFactHandle( rightParent );
    }

    public void propagateRetractRightTuple(final RightTuple rightTuple,
                                           final PropagationContext context,
                                           final InternalWorkingMemory workingMemory) {
        LeftTuple child = rightTuple.firstChild;
        while ( child != null ) {
            LeftTuple temp = child.getRightParentNext();
            doPropagateRetractLeftTuple( context,
                                         workingMemory,
                                         child,
                                         child.getLeftTupleSink() );
            child.unlinkFromLeftParent();
            child.unlinkFromRightParent();
            child = temp;
        }
    }

    public BaseNode getMatchingNode(BaseNode candidate) {
        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            if ( candidate.equals( sink ) ) {
                return (BaseNode) sink;
            }
        }
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }
    
    

    public LeftTupleSink[] getSinks() {
        final LeftTupleSink[] sinkArray = new LeftTupleSink[this.sinks.size()];

        int i = 0;
        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            sinkArray[i++] = sink;
        }

        return sinkArray;
    }

    public int size() {
        return this.sinks.size();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.sinks = (LeftTupleSinkNodeList) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( this.sinks );
    }
    
    public void doPropagateAssertLeftTuple(PropagationContext context,
                                           InternalWorkingMemory workingMemory,
                                           LeftTuple leftTuple,
                                           LeftTupleSink sink) {
     sink.assertLeftTuple( leftTuple, context, workingMemory );
 }

    /**
     * This is a hook method that may be overriden by subclasses. Please keep it
     * protected.
     *
     * @param context
     * @param workingMemory
     * @param sink
     * @param leftTuple
     */
    protected void doPropagateAssertLeftTuple(PropagationContext context,
                                              InternalWorkingMemory workingMemory,
                                              LeftTupleSinkNode sink,
                                              LeftTuple leftTuple) {
        sink.assertLeftTuple( leftTuple,
                              context,
                              workingMemory );
    }

    /**
     * This is a hook method that may be overriden by subclasses. Please keep it
     * protected.
     *
     * @param context
     * @param workingMemory
     * @param leftTuple
     * @param sink
     */
    protected void doPropagateRetractLeftTuple(PropagationContext context,
                                               InternalWorkingMemory workingMemory,
                                               LeftTuple leftTuple,
                                               LeftTupleSink sink) {
        sink.retractLeftTuple( leftTuple,
                               context,
                               workingMemory );
    }

    public void doPropagateModifyObject(InternalFactHandle factHandle,
                                        ModifyPreviousTuples modifyPreviousTuples,
                                        PropagationContext context,
                                        InternalWorkingMemory workingMemory,
                                        LeftTupleSink sink) {
        sink.modifyLeftTuple( factHandle,
                              modifyPreviousTuples,
                              context,
                              workingMemory );
    }

    // related to true modify

    //this.sink.propagateModifyObject( factHandle, modifyPreviousTuples, context, workingMemory );

    public void propagateModifyObject(InternalFactHandle factHandle,
                                      ModifyPreviousTuples modifyPreviousTuples,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory) {
        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            doPropagateModifyObject( factHandle,
                                     modifyPreviousTuples,
                                     context,
                                     workingMemory,
                                     sink );
        }
    }

    public LeftTuple propagateModifyChildLeftTuple(LeftTuple childLeftTuple,
                                                   RightTuple parentRightTuple,
                                                   PropagationContext context,
                                                   InternalWorkingMemory workingMemory,
                                                   boolean tupleMemoryEnabled) {
        // iterate to find all child tuples for the shared node
        while ( childLeftTuple != null && childLeftTuple.getRightParent() == parentRightTuple ) {
            // this will iterate for each child node when the
            // the current node is shared

            // preserve the current LeftTuple, as we need to iterate to the next before re-adding
            LeftTuple temp = childLeftTuple;
            childLeftTuple.getLeftTupleSink().modifyLeftTuple( childLeftTuple,
                                                               context,
                                                               workingMemory );
            childLeftTuple = childLeftTuple.getLeftParentNext();
            temp.reAddRight();
        }
        return childLeftTuple;
    }

    public LeftTuple propagateModifyChildLeftTuple(LeftTuple childLeftTuple,
                                                   LeftTuple parentLeftTuple,
                                                   PropagationContext context,
                                                   InternalWorkingMemory workingMemory,
                                                   boolean tupleMemoryEnabled) {
        // iterate to find all child tuples for the shared node
        while ( childLeftTuple != null && childLeftTuple.getLeftParent() == parentLeftTuple ) {
            // this will iterate for each child node when the
            // the current node is shared      

            // preserve the current LeftTuple, as we need to iterate to the next before re-adding
            LeftTuple temp = childLeftTuple;
            childLeftTuple.getLeftTupleSink().modifyLeftTuple( childLeftTuple,
                                                               context,
                                                               workingMemory );
            childLeftTuple = childLeftTuple.getRightParentNext();
            temp.reAddLeft();
        }
        return childLeftTuple;
    }

    public void propagateModifyChildLeftTuple(LeftTuple leftTuple,
                                              PropagationContext context,
                                              InternalWorkingMemory workingMemory,
                                              boolean tupleMemoryEnabled) {
        for ( LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; childLeftTuple = childLeftTuple.getLeftParentNext() ) {
            childLeftTuple.getLeftTupleSink().modifyLeftTuple( childLeftTuple,
                                                               context,
                                                               workingMemory );
        }
    }

    public LeftTuple propagateRetractChildLeftTuple(LeftTuple childLeftTuple,
                                                    RightTuple parentRightTuple,
                                                    PropagationContext context,
                                                    InternalWorkingMemory workingMemory) {
        // iterate to find all child tuples for the shared node
        while ( childLeftTuple != null && childLeftTuple.getRightParent() == parentRightTuple ) {
            // this will iterate for each child node when the
            // the current node is shared     
            LeftTuple temp = childLeftTuple.getLeftParentNext();
            doPropagateRetractLeftTuple( context,
                                         workingMemory,
                                         childLeftTuple,
                                         childLeftTuple.getLeftTupleSink() );
            childLeftTuple.unlinkFromRightParent();
            childLeftTuple.unlinkFromLeftParent();
            childLeftTuple = temp;
        }
        return childLeftTuple;
    }

    public LeftTuple propagateRetractChildLeftTuple(LeftTuple childLeftTuple,
                                                    LeftTuple parentLeftTuple,
                                                    PropagationContext context,
                                                    InternalWorkingMemory workingMemory) {
        // iterate to find all child tuples for the shared node
        while ( childLeftTuple != null && childLeftTuple.getLeftParent() == parentLeftTuple ) {
            // this will iterate for each child node when the
            // the current node is shared     
            LeftTuple temp = childLeftTuple.getRightParentNext();
            doPropagateRetractLeftTuple( context,
                                         workingMemory,
                                         childLeftTuple,
                                         childLeftTuple.getLeftTupleSink() );
            childLeftTuple.unlinkFromRightParent();
            childLeftTuple.unlinkFromLeftParent();
            childLeftTuple = temp;
        }
        return childLeftTuple;
    }

    public void byPassModifyToBetaNode (final InternalFactHandle factHandle,
                                        final ModifyPreviousTuples modifyPreviousTuples,
                                        final PropagationContext context,
                                        final InternalWorkingMemory workingMemory) {
        // only called from lianode 
        for ( LeftTupleSinkNode sink = this.sinks.getLast(); sink != null; sink = sink.getPreviousLeftTupleSinkNode() ) {           
            sink.modifyLeftTuple( factHandle, modifyPreviousTuples, context, workingMemory );
        }
    }

}
