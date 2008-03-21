package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
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
                                         final InternalWorkingMemory workingMemory) {
        this.sink.assertLeftTuple( new LeftTuple( leftTuple,
                                                  rightTuple,
                                                  this.sink ),
                                   context,
                                   workingMemory );
    }

    public void propagateAssertLeftTuple(final LeftTuple tuple,
                                         final PropagationContext context,
                                         final InternalWorkingMemory workingMemory) {
        this.sink.assertLeftTuple( new LeftTuple( tuple,
                                                  this.sink ),
                                   context,
                                   workingMemory );
    }

    //    public void propagateNotRetractLeftTuple(final LeftTuple leftTuple,
    //                                          final PropagationContext context,
    //                                          final InternalWorkingMemory workingMemory) {
    //            LeftTuple child = leftTuple.getBetaChildren();
    //            while ( child != null ) {
    //                //LeftTuple temp = leftTuple.getRightParentNext();
    //                //child.unlinkFromParents();
    //                //child.unlinkFromLeftParent();
    //                child.getSink().retractTuple( child,
    //                                              context,
    //                                              workingMemory );
    //                child = child.getLeftParentNext();
    //                //child = temp;
    //            }
    //            leftTuple.setBetaChildren( null );
    //        }    

    public void propagateRetractLeftTuple(final LeftTuple leftTuple,
                                          final PropagationContext context,
                                          final InternalWorkingMemory workingMemory) {
        LeftTuple child = leftTuple.getBetaChildren();
        while ( child != null ) {
            LeftTuple temp = child.getLeftParentNext();
            //child.unlinkFromParents();
            child.getSink().retractLeftTuple( child,
                                              context,
                                              workingMemory );
            child.unlinkFromRightParent();
            //child = child.getLeftParentNext();
            child = temp;
        }
        leftTuple.setBetaChildren( null );
    }

    public void propagateRetractRightTuple(final RightTuple rightTuple,
                                           final PropagationContext context,
                                           final InternalWorkingMemory workingMemory) {
        LeftTuple child = rightTuple.getBetaChildren();
        while ( child != null ) {
            LeftTuple temp = child.getRightParentNext();
            //child.unlinkFromParents();
            child.getSink().retractLeftTuple( child,
                                              context,
                                              workingMemory );
            child.unlinkFromLeftParent();
            //child = child.getRightParentNext();
            child = temp;
        }
        rightTuple.setBetaChildren( null );
    }

    public void createAndPropagateAssertLeftTuple(final InternalFactHandle factHandle,
                                                  final PropagationContext context,
                                                  final InternalWorkingMemory workingMemory) {
        this.sink.assertLeftTuple( new LeftTuple( factHandle,
                                                  this.sink ),
                                   context,
                                   workingMemory );
    }

    public LeftTupleSink[] getSinks() {
        return new LeftTupleSink[]{this.sink};
    }

    public int size() {
        return (this.sink != null) ? 1 : 0;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        // @todo

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // @todo        
    }
}
