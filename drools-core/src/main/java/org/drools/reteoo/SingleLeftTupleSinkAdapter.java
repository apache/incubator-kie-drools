package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.BaseNode;
import org.drools.spi.PropagationContext;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class SingleLeftTupleSinkAdapter
    implements
    LeftTupleSinkPropagator {
    private LeftTupleSink sink;

    public SingleLeftTupleSinkAdapter() {

    }

    public SingleLeftTupleSinkAdapter(final LeftTupleSink sink) {
        this.sink = sink;
    }

    public void propagateAssertLeftTuple(final LeftTuple leftTuple,
                                         final RightTuple rightTuple,
                                         final PropagationContext context,
                                         final InternalWorkingMemory workingMemory,
                                         boolean leftTupleMemoryEnabled) {
        this.sink.assertLeftTuple( new LeftTuple( leftTuple,
                                                  rightTuple,
                                                  this.sink,
                                                  leftTupleMemoryEnabled ),
                                   context,
                                   workingMemory );
    }

    public void propagateAssertLeftTuple(final LeftTuple tuple,
                                         final PropagationContext context,
                                         final InternalWorkingMemory workingMemory,
                                         boolean leftTupleMemoryEnabled) {
        this.sink.assertLeftTuple( new LeftTuple( tuple,
                                                  this.sink,
                                                  leftTupleMemoryEnabled ),
                                   context,
                                   workingMemory );
    }

    public void propagateRetractLeftTuple(final LeftTuple leftTuple,
                                          final PropagationContext context,
                                          final InternalWorkingMemory workingMemory) {
        LeftTuple child = leftTuple.getBetaChildren();
        while ( child != null ) {
            LeftTuple temp = child.getLeftParentNext();
            child.getLeftTupleSink().retractLeftTuple( child,
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
            child.getLeftTupleSink().retractLeftTuple( child,
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
            child.getLeftTupleSink().retractLeftTuple( child,
                                              context,
                                              workingMemory );
            child.unlinkFromLeftParent();
            child.unlinkFromRightParent();
            child = temp;
        }
    }

    public void createAndPropagateAssertLeftTuple(final InternalFactHandle factHandle,
                                                  final PropagationContext context,
                                                  final InternalWorkingMemory workingMemory,
                                                  boolean leftTupleMemoryEnabled) {
        this.sink.assertLeftTuple( new LeftTuple( factHandle,
                                                  this.sink,
                                                  leftTupleMemoryEnabled ),
                                   context,
                                   workingMemory );
    }

    public BaseNode getMatchingNode(BaseNode candidate) {
        if (candidate.equals(sink)) {
            return (BaseNode)sink;
        }
        return null;
    }

    public LeftTupleSink[] getSinks() {
        return new LeftTupleSink[]{this.sink};
    }

    public int size() {
        return (this.sink != null) ? 1 : 0;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.sink = ( LeftTupleSink) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.sink );
    }
}
