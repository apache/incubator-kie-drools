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
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;
import org.drools.util.AbstractBaseLinkedListNode;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListNode;
import org.drools.util.LinkedListEntry;

/**
 * <code>TupleMatch</code> maintains a reference to the parent <code>ReteTuple</code> and a <code>List</code> of all resulting joins. 
 * This is a List rather than a single instance reference because we need to create a join for each TupleSink branches.
 * A reference is also maintained to the <code>ObjectMatches</code> instance; this is so the <code>FactHandleImpl</code> that 
 * is used in the join can be referenced, and also any other <code>TupleMatch</code>es the <code>FactHandleImpl</code> is joined with.
 * 
 * @see CompositeTupleMatch
 * @see ObjectMatches
 * @see ReteTuple
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public class CompositeTupleMatch extends AbstractBaseLinkedListNode
    implements
    TupleMatch {
    private ReteTuple     tuple;

    private LinkedList    list;

    private ObjectMatches objectMatches;

    /**
     * Construct a <code>TupleMatch</code> with references to the parent <code>ReteTuple</code> and 
     * <code>FactHandleImpl</code>, via ObjecMatches.
     * 
     * @param tuple
     * @param objectMatches
     */
    public CompositeTupleMatch(final ReteTuple tuple,
                               final ObjectMatches objectMatches) {
        this.tuple = tuple;
        this.objectMatches = objectMatches;
        this.list = new LinkedList();
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.ITupleMatch#getTuple()
     */
    public ReteTuple getTuple() {
        return this.tuple;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.ITupleMatch#getObjectMatches()
     */
    public ObjectMatches getObjectMatches() {
        return this.objectMatches;
    }

    public void addJoinedTuple(ReteTuple joined) {
        this.list.add( new LinkedListEntry( joined ) );
    }

    public void propagateRetractTuple(final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
        for ( LinkedListNode node = this.list.getFirst(); node != null; node = node.getNext() ) {
            ReteTuple joined = (ReteTuple) ((LinkedListEntry) node).getObject();
            joined.retractTuple( context,
                                 workingMemory );
        }
    }

    public void propagateModifyTuple(final PropagationContext context,
                                     final InternalWorkingMemory workingMemory) {
        for ( LinkedListNode node = this.list.getFirst(); node != null; node = node.getNext() ) {
            ReteTuple joined = (ReteTuple) ((LinkedListEntry) node).getObject();
            joined.modifyTuple( context,
                                workingMemory );
        }
    }

    public ReteTuple getTupleForSink(TupleSink sink) {
        for ( LinkedListNode node = this.list.getFirst(); node != null; node = node.getNext() ) {
            ReteTuple joined = (ReteTuple) ((LinkedListEntry) node).getObject();
            if ( sink.equals( joined.getTupleSink() ) ) {
                return joined;
            }
        }
        return null;
    }

}