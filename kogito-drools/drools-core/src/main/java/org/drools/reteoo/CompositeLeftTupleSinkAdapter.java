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

    public CompositeLeftTupleSinkAdapter( final RuleBasePartitionId partitionId ) {
        super( partitionId );
        this.sinks = new LeftTupleSinkNodeList();
    }

    public void addTupleSink( final LeftTupleSink sink ) {
        this.sinks.add( (LeftTupleSinkNode) sink );
    }

    public void removeTupleSink( final LeftTupleSink sink ) {
        this.sinks.remove( (LeftTupleSinkNode) sink );
    }

    public void propagateAssertLeftTuple( final LeftTuple leftTuple, final RightTuple rightTuple,
                                          final PropagationContext context, final InternalWorkingMemory workingMemory,
                                          final boolean leftTupleMemoryEnabled ) {

        for( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            LeftTuple newLeftTuple = new LeftTuple( leftTuple, rightTuple, sink, leftTupleMemoryEnabled );
            doPropagateAssertLeftTuple( context, workingMemory, sink, newLeftTuple );
        }
    }

    public void propagateAssertLeftTuple( final LeftTuple tuple, final PropagationContext context,
                                          final InternalWorkingMemory workingMemory,
                                          final boolean leftTupleMemoryEnabled ) {
        for( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            doPropagateAssertLeftTuple( context, workingMemory, sink,
                                        new LeftTuple( tuple, sink, leftTupleMemoryEnabled ) );
        }
    }

    public void createAndPropagateAssertLeftTuple( final InternalFactHandle factHandle,
                                                   final PropagationContext context,
                                                   final InternalWorkingMemory workingMemory,
                                                   final boolean leftTupleMemoryEnabled ) {
        for( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            doPropagateAssertLeftTuple( context, workingMemory, sink,
                                        new LeftTuple( factHandle, sink, leftTupleMemoryEnabled ) );
        }
    }


    public void propagateRetractLeftTuple( final LeftTuple leftTuple, final PropagationContext context,
                                           final InternalWorkingMemory workingMemory ) {
        LeftTuple child = leftTuple.getBetaChildren();
        while( child != null ) {
            LeftTuple temp = child.getLeftParentNext();
            doPropagateRetractLeftTuple( context, workingMemory, child, child.getLeftTupleSink() );
            child.unlinkFromRightParent();
            child.unlinkFromLeftParent();
            child = temp;
        }
    }

    public void propagateRetractLeftTupleDestroyRightTuple( final LeftTuple leftTuple, final PropagationContext context,
                                                            final InternalWorkingMemory workingMemory ) {
        LeftTuple child = leftTuple.getBetaChildren();
        while( child != null ) {
            LeftTuple temp = child.getLeftParentNext();
            doPropagateRetractLeftTuple( context, workingMemory, child, child.getLeftTupleSink() );
            workingMemory.getFactHandleFactory().destroyFactHandle( child.getRightParent().getFactHandle() );
            child.unlinkFromRightParent();
            child.unlinkFromLeftParent();
            child = temp;
        }
    }

    public void propagateRetractRightTuple( final RightTuple rightTuple, final PropagationContext context,
                                            final InternalWorkingMemory workingMemory ) {
        LeftTuple child = rightTuple.getBetaChildren();
        while( child != null ) {
            LeftTuple temp = child.getRightParentNext();
            doPropagateRetractLeftTuple( context, workingMemory, child, child.getLeftTupleSink() );
            child.unlinkFromLeftParent();
            child.unlinkFromRightParent();
            child = temp;
        }
    }

    public BaseNode getMatchingNode( BaseNode candidate ) {
        for( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            if( candidate.equals( sink ) ) {
                return (BaseNode) sink;
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public LeftTupleSink[] getSinks() {
        final LeftTupleSink[] sinkArray = new LeftTupleSink[this.sinks.size()];

        int i = 0;
        for( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            sinkArray[i++] = sink;
        }

        return sinkArray;
    }

    public int size() {
        return this.sinks.size();
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        super.readExternal( in );
        this.sinks = (LeftTupleSinkNodeList) in.readObject();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( this.sinks );
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
    protected void doPropagateAssertLeftTuple( PropagationContext context, InternalWorkingMemory workingMemory,
                                               LeftTupleSinkNode sink, LeftTuple leftTuple ) {
        sink.assertLeftTuple( leftTuple, context, workingMemory );
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
    protected void doPropagateRetractLeftTuple( PropagationContext context, InternalWorkingMemory workingMemory,
                                                LeftTuple leftTuple, LeftTupleSink sink ) {
        sink.retractLeftTuple( leftTuple, context, workingMemory );
    }


}
