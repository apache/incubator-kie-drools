package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class CompositeTupleSinkAdapter
    implements
    LeftTupleSinkPropagator {
    private LeftTupleSinkNodeList sinks;

    public CompositeTupleSinkAdapter() {
        this.sinks = new LeftTupleSinkNodeList();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        sinks   = (LeftTupleSinkNodeList)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(sinks);
    }

    public void addTupleSink(final LeftTupleSink sink) {
        this.sinks.add( (LeftTupleSinkNode) sink );
    }

    public void removeTupleSink(final LeftTupleSink sink) {
        this.sinks.remove( (LeftTupleSinkNode) sink );
    }

    public void propagateAssertLeftTuple(final LeftTuple tuple,
                                     final InternalFactHandle handle,
                                     final PropagationContext context,
                                     final InternalWorkingMemory workingMemory) {

        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            sink.assertLeftTuple( new LeftTuple( tuple,
                                             handle ),
                              context,
                              workingMemory );
        }
    }

    public void propagateAssertLeftTuple(final LeftTuple tuple,
                                     final PropagationContext context,
                                     final InternalWorkingMemory workingMemory) {
        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            sink.assertLeftTuple( new LeftTuple( tuple ),
                              context,
                              workingMemory );
        }
    }

    public void propagateRetractLeftTuple(final LeftTuple tuple,
                                      final InternalFactHandle handle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            sink.retractLeftTuple( new LeftTuple( tuple,
                                              handle ),
                               context,
                               workingMemory );
        }
    }

    public void propagateRetractLeftTuple(final LeftTuple tuple,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            sink.retractLeftTuple( new LeftTuple( tuple ),
                               context,
                               workingMemory );
        }
    }

    public void createAndPropagateAssertLeftTuple(final InternalFactHandle handle,
                                              final PropagationContext context,
                                              final InternalWorkingMemory workingMemory) {
        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            sink.assertLeftTuple( new LeftTuple( handle ),
                              context,
                              workingMemory );
        }
    }

    public void createAndPropagateRetractLeftTuple(final InternalFactHandle handle,
                                               final PropagationContext context,
                                               final InternalWorkingMemory workingMemory) {
        for ( LeftTupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextLeftTupleSinkNode() ) {
            sink.retractLeftTuple( new LeftTuple( handle ),
                               context,
                               workingMemory );
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

    //    public void propagateNewTupleSink(TupleMatch tupleMatch,
    //                                      PropagationContext context,
    //                                      InternalWorkingMemory workingMemory) {
    //
    //        final TupleSink sink = sinks.getLast();
    //        final ReteTuple tuple = new ReteTuple( tupleMatch.getTuple(),
    //                                               tupleMatch.getObjectMatches().getFactHandle(),
    //                                               sink );
    //        tupleMatch.addJoinedTuple( tuple );
    //        tuple.assertTuple( context,
    //                           workingMemory );
    //    }
    //
    //    public void propagateNewTupleSink(ReteTuple tuple,
    //                                      PropagationContext context,
    //                                      InternalWorkingMemory workingMemory) {
    //
    //        final TupleSink sink = sinks.getLast();
    //        ReteTuple child = new ReteTuple( tuple,
    //                                         sink );
    //        tuple.addChildEntry( child );
    //        child.assertTuple( context,
    //                           workingMemory );
    //    }
    //
    //    public void propagateNewTupleSink(InternalFactHandle handle,
    //                                      LinkedList list,
    //                                      PropagationContext context,
    //                                      InternalWorkingMemory workingMemory) {
    //        TupleSink sink = this.sinks.getLast();
    //        ReteTuple tuple = new ReteTuple( handle,
    //                                         sink );
    //        list.add( new LinkedListEntry( tuple ) );
    //        tuple.assertTuple( context,
    //                           workingMemory );
    //    }
    //
    //    /**
    //     * @inheritDoc
    //     */
    //    public List getPropagatedTuples(final Map memory,
    //                                    final InternalWorkingMemory workingMemory,
    //                                    final TupleSink sink) {
    //        int index = 0;
    //        for ( TupleSinkNode node = this.sinks.getFirst(); node != null; node = node.getNextTupleSinkNode() ) {
    //            if ( node.equals( sink ) ) {
    //                break;
    //            }
    //            index++;
    //        }
    //
    //        final List propagatedTuples = new ArrayList( memory.size() );
    //
    //        for ( final Iterator it = memory.values().iterator(); it.hasNext(); ) {
    //            final LinkedList tuples = (LinkedList) it.next();
    //            LinkedListEntry wrapper = (LinkedListEntry) tuples.getFirst();
    //            for ( int i = 0; i < index; i++ ) {
    //                wrapper = (LinkedListEntry) wrapper.getNext();
    //            }
    //            propagatedTuples.add( wrapper.getObject() );
    //        }
    //
    //        return propagatedTuples;
    //    }

    public int size() {
        return this.sinks.size();
    }
}
