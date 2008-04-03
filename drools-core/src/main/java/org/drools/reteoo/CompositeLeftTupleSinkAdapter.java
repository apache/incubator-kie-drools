package org.drools.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public class CompositeLeftTupleSinkAdapter
    implements
    LeftTupleSinkPropagator {
    private LeftTupleSinkNodeList sinks;

    public CompositeLeftTupleSinkAdapter() {
        this.sinks = new LeftTupleSinkNodeList();
    }

    public void addTupleSink(final LeftTupleSink sink) {
        this.sinks.add( (LeftTupleSinkNode) sink );
    }

    public void removeTupleSink(final LeftTupleSink sink) {
        this.sinks.remove( (LeftTupleSinkNode) sink );
    }

    public void propagateAssertLeftTuple(final LeftTuple leftTuple,
                                         final RightTuple rightTuple,
                                         final PropagationContext context,
                                         final InternalWorkingMemory workingMemory,
                                         final boolean leftTupleMemoryEnabled) {

        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            sink.assertLeftTuple( new LeftTuple( leftTuple,
                                                 rightTuple,
                                                 sink,
                                                 leftTupleMemoryEnabled ),
                                  context,
                                  workingMemory );
        }
    }

    public void propagateAssertLeftTuple(final LeftTuple tuple,
                                         final PropagationContext context,
                                         final InternalWorkingMemory workingMemory,
                                         final boolean leftTupleMemoryEnabled) {
        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            sink.assertLeftTuple( new LeftTuple( tuple,
                                                 sink,
                                                 leftTupleMemoryEnabled ),
                                  context,
                                  workingMemory );
        }
    }
    
    public void createAndPropagateAssertLeftTuple(final InternalFactHandle factHandle,
                                                  final PropagationContext context,
                                                  final InternalWorkingMemory workingMemory,
                                                  final boolean leftTupleMemoryEnabled) {
        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            sink.assertLeftTuple( new LeftTuple( factHandle,
                                                 sink,
                                                 leftTupleMemoryEnabled),
                                  context,
                                  workingMemory );
        }
    }
    

    public void propagateRetractLeftTuple(final LeftTuple leftTuple,
                                          final PropagationContext context,
                                          final InternalWorkingMemory workingMemory) {
        LeftTuple child = leftTuple.getBetaChildren();
        while ( child != null ) {
            LeftTuple temp = child.getLeftParentNext();
            child.getSink().retractLeftTuple( child,
                                              context,
                                              workingMemory );
            child.unlinkFromRightParent();
            child.unlinkFromLeftParent();
            child = temp;
        }
    }

    public void propagateRetractLeftTupleDestroyRightTuple(final LeftTuple leftTuple,
                                                           final PropagationContext context,
                                                           final InternalWorkingMemory workingMemory) {
        LeftTuple child = leftTuple.getBetaChildren();
        while ( child != null ) {
            LeftTuple temp = child.getLeftParentNext();
            child.getSink().retractLeftTuple( child,
                                              context,
                                              workingMemory );
            workingMemory.getFactHandleFactory().destroyFactHandle( child.getRightParent().getFactHandle() );
            child.unlinkFromRightParent();
            child.unlinkFromLeftParent();
            child = temp;
        }
    }

    public void propagateRetractRightTuple(final RightTuple rightTuple,
                                           final PropagationContext context,
                                           final InternalWorkingMemory workingMemory) {
        LeftTuple child = rightTuple.getBetaChildren();
        while ( child != null ) {
            LeftTuple temp = child.getRightParentNext();
            child.getSink().retractLeftTuple( child,
                                              context,
                                              workingMemory );
            child.unlinkFromLeftParent();
            child.unlinkFromRightParent();
            child = temp;
        }
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
        this.sinks = (LeftTupleSinkNodeList) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.sinks );
    }
}
