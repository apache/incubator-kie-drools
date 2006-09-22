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
                                             handle), context, workingMemory );
        }
    }

    public void propagateAssertTuple(ReteTuple tuple,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory) {
        for ( TupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextTupleSinkNode() ) {
            sink.assertTuple( new ReteTuple( tuple), context, workingMemory );
        }
    }

    public void createAndPropagateAssertTuple(InternalFactHandle handle,
                                           PropagationContext context,
                                           InternalWorkingMemory workingMemory) {
        for ( TupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextTupleSinkNode() ) {
            sink.assertTuple( new ReteTuple( handle ), context, workingMemory );
        }
    }
    
    public void createAndPropagateRetractTuple(InternalFactHandle handle,
                                              PropagationContext context,
                                              InternalWorkingMemory workingMemory) {
           for ( TupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextTupleSinkNode() ) {
               sink.retractTuple( new ReteTuple( handle ), context, workingMemory );
           }
    }
    
    public void createAndPropagateModifyTuple(InternalFactHandle handle,
                                               PropagationContext context,
                                               InternalWorkingMemory workingMemory) {
            for ( TupleSinkNode sink = this.sinks.getFirst(); sink != null; sink = sink.getNextTupleSinkNode() ) {
                sink.modifyTuple( new ReteTuple( handle ), context, workingMemory );
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
