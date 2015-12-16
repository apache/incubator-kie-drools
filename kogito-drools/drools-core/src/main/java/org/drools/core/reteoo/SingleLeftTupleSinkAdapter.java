/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SingleLeftTupleSinkAdapter extends AbstractLeftTupleSinkAdapter {
    protected LeftTupleSink sink;
    
    private LeftTupleSink[] array;

    public SingleLeftTupleSinkAdapter() {
        this( RuleBasePartitionId.MAIN_PARTITION,
              null );
    }

    public SingleLeftTupleSinkAdapter(final RuleBasePartitionId partitionId,
                                      final LeftTupleSink sink) {
        super( partitionId );
        this.sink = sink;
    }
    
    public void createChildLeftTuplesforQuery(final LeftTuple leftTuple,
                                              final RightTuple rightTuple,
                                              boolean leftTupleMemoryEnabled,
                                              boolean linkRightTuple) {        
        this.sink.createLeftTuple( leftTuple,
                                   rightTuple,
                                   this.sink );
    }  
    
    public void modifyChildLeftTuplesforQuery(final RightTuple rightTuple,
                                              final PropagationContext context,
                                              final InternalWorkingMemory workingMemory) {
        LeftTuple childLeftTuple = rightTuple.getFirstChild();
        childLeftTuple.getTupleSink().modifyLeftTuple( childLeftTuple,
                                                           context,
                                                           workingMemory );       
    }
    
    
    

    public void propagateAssertLeftTuple(final LeftTuple leftTuple,
                                         final RightTuple rightTuple,
                                         final LeftTuple currentLeftChild,
                                         final LeftTuple currentRightChild,
                                         final PropagationContext context,
                                         final InternalWorkingMemory workingMemory,
                                         boolean leftTupleMemoryEnabled) {
        doPropagateAssertLeftTuple( context,
                                    workingMemory,
                                    sink.createLeftTuple( leftTuple,
                                                          rightTuple,
                                                          currentLeftChild,
                                                          currentRightChild,
                                                          this.sink,
                                                          leftTupleMemoryEnabled ) );
    }

    public void propagateAssertLeftTuple(final LeftTuple tuple,
                                         final PropagationContext context,
                                         final InternalWorkingMemory workingMemory,
                                         boolean leftTupleMemoryEnabled) {
        doPropagateAssertLeftTuple( context,
                                    workingMemory,
                                    sink.createLeftTuple( tuple,
                                                          this.sink,
                                                          context, leftTupleMemoryEnabled) );
    }

    public void propagateRetractLeftTuple(final LeftTuple leftTuple,
                                          final PropagationContext context,
                                          final InternalWorkingMemory workingMemory) {
        LeftTuple child = leftTuple.getFirstChild();
        while ( child != null ) { 
            LeftTuple temp = child.getHandleNext();
            doPropagateRetractLeftTuple( context,
                                         workingMemory,
                                         child,
                                         child.getTupleSink() );
            child.unlinkFromRightParent();
            child.unlinkFromLeftParent();
            child = temp;
        }
    }

    public void propagateRetractLeftTupleDestroyRightTuple(final LeftTuple leftTuple,
                                                           final PropagationContext context,
                                                           final InternalWorkingMemory workingMemory) {
        LeftTuple child = leftTuple.getFirstChild();
        while ( child != null ) {
            LeftTuple temp = child.getHandleNext();
            doPropagateRetractLeftTuple( context,
                                         workingMemory,
                                         child,
                                         child.getTupleSink() );
            //workingMemory.getFactHandleFactory().destroyFactHandle( child.getRightParent().getFactHandle() );
            child.unlinkFromRightParent();
            child.unlinkFromLeftParent();
            child = temp;
        }
    }

    public void propagateRetractRightTuple(final RightTuple rightTuple,
                                           final PropagationContext context,
                                           final InternalWorkingMemory workingMemory) {
        LeftTuple child = rightTuple.getFirstChild();
        while ( child != null ) {
            LeftTuple temp = child.getRightParentNext();
            doPropagateRetractLeftTuple( context,
                                         workingMemory,
                                         child,
                                         child.getTupleSink() );
            child.unlinkFromLeftParent();
            child.unlinkFromRightParent();
            child = temp;
        }
    }    

    public void createAndPropagateAssertLeftTuple(final InternalFactHandle factHandle,
                                                  final PropagationContext context,                                                  
                                                  final InternalWorkingMemory workingMemory,
                                                  boolean leftTupleMemoryEnabled, LeftInputAdapterNode liaNode) {                
        LeftTuple lt = sink.createLeftTuple( factHandle,
                                             this.sink,
                                             leftTupleMemoryEnabled );
        lt.setPropagationContext( context );
        

        doPropagateAssertLeftTuple( context,
                                    workingMemory,
                                    lt );        
    }

    public BaseNode getMatchingNode(BaseNode candidate) {
        if ( candidate.equals( sink ) ) {
            return (BaseNode) sink;
        }
        return null;
    }

    public LeftTupleSink[] getSinks() {
    	if ( array == null ) {
    		array = new LeftTupleSink[]{this.sink};
    	}
        return array;
    }
    
    public LeftTupleSinkNode getFirstLeftTupleSink() {
        return ( LeftTupleSinkNode ) sink;
    }

    public LeftTupleSinkNode getLastLeftTupleSink() {
        return ( LeftTupleSinkNode ) sink;
    }

    public int size() {
        return (this.sink != null) ? 1 : 0;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.sink = (LeftTupleSink) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( this.sink );
    }
    
    public void doPropagateAssertLeftTuple(PropagationContext context,
                                              InternalWorkingMemory workingMemory,
                                              LeftTuple leftTuple,
                                              LeftTupleSink sink) {
        sink.assertLeftTuple( leftTuple, context, workingMemory );
    }

    /**
     * This is a hook method that may be overriden by subclasses. Please keep it
     * package protected.
     */
    protected void doPropagateAssertLeftTuple(PropagationContext context,
                                              InternalWorkingMemory workingMemory,
                                              LeftTuple newLeftTuple) {
        this.sink.assertLeftTuple( newLeftTuple,
                                   context,
                                   workingMemory );
    }
    
    protected void doPropagateModifyLeftTuple(InternalFactHandle factHandle,
                                              ModifyPreviousTuples modifyPreviousTuples,
                                              PropagationContext context,
                                              InternalWorkingMemory workingMemory) {
        this.sink.modifyLeftTuple( factHandle,
                                   modifyPreviousTuples,
                                   context,
                                   workingMemory );
    }

    /**
     * This is a hook method that may be overriden by subclasses. Please keep it
     * package protected.
     */
    protected void doPropagateRetractLeftTuple(PropagationContext context,
                                               InternalWorkingMemory workingMemory,
                                               LeftTuple child,
                                               LeftTupleSink tupleSink) {
        tupleSink.retractLeftTuple( child,
                                    context,
                                    workingMemory );
    }

    // related to true modify
    
    public void propagateModifyObject(InternalFactHandle factHandle,
                                      ModifyPreviousTuples modifyPreviousTuples,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory) {
        doPropagateModifyLeftTuple( factHandle,
                                    modifyPreviousTuples,
                                    context,
                                    workingMemory );
    }

    public LeftTuple propagateModifyChildLeftTuple(LeftTuple childLeftTuple,
                                                   RightTuple parentRightTuple,
                                                   PropagationContext context,
                                                   InternalWorkingMemory workingMemory,
                                                   boolean tupleMemoryEnabled) {
        childLeftTuple.getTupleSink().modifyLeftTuple( childLeftTuple,
                                                           context,
                                                           workingMemory );
        // re-order right to keep order consistency
        childLeftTuple.reAddRight();
        return childLeftTuple.getHandleNext();
    }

    public LeftTuple propagateModifyChildLeftTuple(LeftTuple childLeftTuple,
                                                   LeftTuple parentLeftTuple,
                                                   PropagationContext context,
                                                   InternalWorkingMemory workingMemory,
                                                   boolean tupleMemoryEnabled) {
        childLeftTuple.getTupleSink().modifyLeftTuple( childLeftTuple,
                                                           context,
                                                           workingMemory );
        // re-order right to keep order consistency
        childLeftTuple.reAddLeft();
        return childLeftTuple.getRightParentNext();
    }

    public void propagateModifyChildLeftTuple(LeftTuple leftTuple,
                                              PropagationContext context,
                                              InternalWorkingMemory workingMemory,
                                              boolean tupleMemoryEnabled) {
        // not shared, so only one child
        leftTuple.getFirstChild().getTupleSink().modifyLeftTuple( leftTuple.getFirstChild(),
                                                                      context,
                                                                      workingMemory );
    }

    public LeftTuple propagateRetractChildLeftTuple(LeftTuple childLeftTuple,
                                                    RightTuple parentRightTuple,
                                                    PropagationContext context,
                                                    InternalWorkingMemory workingMemory) {
        LeftTuple temp = childLeftTuple.getHandleNext();
        doPropagateRetractLeftTuple( context,
                                     workingMemory,
                                     childLeftTuple,
                                     childLeftTuple.getTupleSink() );
        childLeftTuple.unlinkFromRightParent();
        childLeftTuple.unlinkFromLeftParent();
        return temp;
    }

    public LeftTuple propagateRetractChildLeftTuple(LeftTuple childLeftTuple,
                                                    LeftTuple parentLeftTuple,
                                                    PropagationContext context,
                                                    InternalWorkingMemory workingMemory) {
        LeftTuple temp = childLeftTuple.getRightParentNext();
        doPropagateRetractLeftTuple( context,
                                     workingMemory,
                                     childLeftTuple,
                                     childLeftTuple.getTupleSink() );
        childLeftTuple.unlinkFromRightParent();
        childLeftTuple.unlinkFromLeftParent();
        return temp;
    }
    
    public void byPassModifyToBetaNode (final InternalFactHandle factHandle,
                                        final ModifyPreviousTuples modifyPreviousTuples,
                                        final PropagationContext context,
                                        final InternalWorkingMemory workingMemory) {
        // only called from lianode
        sink.modifyLeftTuple( factHandle, modifyPreviousTuples, context, workingMemory );
    }
}
