package org.drools.reteoo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListEntry;

public class CompositeTupleSinkAdapter
    implements
    TupleSinkPropagator {
    private TupleSinkNodeList sinks;

    public CompositeTupleSinkAdapter() {
        this.sinks = new TupleSinkNodeList();
    }

    public void addTupleSink(TupleSink sink) {
        this.sinks.add( (TupleSinkNode) sink );
    }

    public void removeTupleSink(TupleSink sink) {
        this.sinks.remove( (TupleSinkNode) sink );
    }

    public void propagateAssertTuple(ReteTuple tuple,
                                     InternalFactHandle handle,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory) {

        for ( TupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextTupleSinkNode() ) {
            sink.assertTuple( new ReteTuple( tuple,
                                             handle ),
                              context,
                              workingMemory );
        }
    }

    public void propagateAssertTuple(ReteTuple tuple,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory) {
        for ( TupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextTupleSinkNode() ) {
            sink.assertTuple( new ReteTuple( tuple ),
                              context,
                              workingMemory );
        }
    }

    public LinkedList createAndPropagateAssertTupleWithMemory(InternalFactHandle handle,
                                                              PropagationContext context,
                                                              InternalWorkingMemory workingMemory) {
        LinkedList list = new LinkedList();
        // while this is the root fact, one branch propagates into one or more TerminalNodes, so we 
        // have to remember the activations.
        for ( TupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextTupleSinkNode() ) {
            ReteTuple tuple = new ReteTuple( handle );
            list.add( new LinkedListEntry( tuple ) );
            sink.assertTuple( tuple,
                              context,
                              workingMemory );
        }
        return list;
    }

    public void createAndPropagateAssertTuple(InternalFactHandle handle,
                                              PropagationContext context,
                                              InternalWorkingMemory workingMemory) {
        // This is the root fact, so we don't need to clone it.
        ReteTuple tuple = new ReteTuple( handle );
        for ( TupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextTupleSinkNode() ) {
            sink.assertTuple( tuple,
                              context,
                              workingMemory );
        }
    }

    public void createAndPropagateRetractTuple(LinkedList list,
                                               PropagationContext context,
                                               InternalWorkingMemory workingMemory) {
        LinkedListEntry entry = (LinkedListEntry) list.getFirst();
        for ( TupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextTupleSinkNode() ) {
            sink.retractTuple( (ReteTuple) entry.getObject(),
                               context,
                               workingMemory );
            entry = (LinkedListEntry) entry.getNext();
        }
    }

    public void createAndPropagateModifyTuple(LinkedList list,
                                              PropagationContext context,
                                              InternalWorkingMemory workingMemory) {
        LinkedListEntry entry = (LinkedListEntry) list.getFirst();

        for ( TupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextTupleSinkNode() ) {
            sink.modifyTuple( (ReteTuple) entry.getObject(),
                              context,
                              workingMemory );
            entry = (LinkedListEntry) entry.getNext();
        }
    }

    public void createAndPropagateRetractTuple(ReteTuple tuple,
                                               PropagationContext context,
                                               InternalWorkingMemory workingMemory) {
        for ( TupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextTupleSinkNode() ) {
            sink.retractTuple( tuple,
                               context,
                               workingMemory );
        }
    }

    public void createAndPropagateModifyTuple(ReteTuple tuple,
                                              PropagationContext context,
                                              InternalWorkingMemory workingMemory) {
        for ( TupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextTupleSinkNode() ) {
            sink.modifyTuple( tuple,
                              context,
                              workingMemory );
        }
    }

    public TupleSink[] getSinks() {
        TupleSink[] sinkArray = new TupleSink[this.sinks.size()];

        int i = 0;
        for ( TupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextTupleSinkNode() ) {
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
