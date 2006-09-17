package org.drools.reteoo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;
import org.drools.util.BaseMultiLinkedListNode;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListEntry;
import org.drools.util.LinkedListNode;

public class ReteTuple extends BaseMultiLinkedListNode
    implements
    Tuple {
    private static final long serialVersionUID = -4221694077704683140L;

    private int                      index;

    private final InternalFactHandle handle;

    private ReteTuple                parent;

    private final TupleSink          sink;

    private LinkedList               children;

    /** The <code>Map</code> of <code>FactHandleImpl</code> matches */
    private Map                      matches = Collections.EMPTY_MAP;

    private Activation               activation;

    private long                     recency;    

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public ReteTuple(final ReteTuple parentEntry) {
        this( parentEntry, null );
    }
    public ReteTuple(final InternalFactHandle handle,
                     final TupleSink sink) {
        this.index = 0;
        this.parent = null;
        this.recency = handle.getRecency();
        this.handle = handle;
        this.sink = sink;
    }

    public ReteTuple(final ReteTuple entry,
                     final TupleSink sink) {
        this.index = entry.index;
        this.parent = entry.parent;
        this.recency = entry.recency;
        this.handle = entry.handle;
        this.sink = sink;
    }

    public ReteTuple(final ReteTuple parentEntry,
                     final InternalFactHandle handle,
                     final TupleSink sink) {
        this.index = parentEntry.index + 1;
        this.parent = parentEntry;
        this.recency = parentEntry.recency + handle.getRecency();
        this.handle = handle;
        this.sink = sink;
    }

    public void addChildEntry(ReteTuple entry) {
        if ( this.children == null ) {
            this.children = new LinkedList();
        }
        this.children.add( new LinkedListEntry( entry ) );
    }

    public void modifyChildEntries(PropagationContext context,
                                   InternalWorkingMemory workingMemory) {
        for ( LinkedListNode node = this.children.getFirst(); node != null; node = node.getNext() ) {
            ReteTuple tuple = (ReteTuple) ((LinkedListEntry) node).getObject();
            tuple.modifyTuple( context,
                               workingMemory );
        }
    }

    public void retractChildEntries(PropagationContext context,
                                    InternalWorkingMemory workingMemory) {
        for ( LinkedListNode node = this.children.getFirst(); node != null; node = node.getNext() ) {
            ReteTuple tuple = (ReteTuple) ((LinkedListEntry) node).getObject();
            tuple.retractTuple( context,
                                workingMemory );
        }
    }

    public void addTupleMatch(InternalFactHandle handle,
                              TupleMatch tupleMatch) {
        if ( this.matches == Collections.EMPTY_MAP ) {
            this.matches = new HashMap();
        }
        this.matches.put( handle,
                          tupleMatch );
    }

    public void clearChildEntries() {
        this.children.clear();
    }

    public void clearTupleMatches() {
        this.matches.clear();
    }

    public InternalFactHandle get(int index) {
        ReteTuple entry = this;
        while ( entry.index != index ) {
            entry = entry.parent;
        }
        return entry.handle;
    }
    
    public InternalFactHandle getLastHandle() {
        return this.handle;
    }

    public InternalFactHandle get(Declaration declaration) {
        return get( declaration.getColumn().getIndex() );
    }

    public Activation getActivation() {
        return this.activation;
    }

    public LinkedList getChildEntries() {
        return this.children;
    }

    public InternalFactHandle[] getFactHandles() {
        List list = new ArrayList();
        ReteTuple entry = this;
        while ( entry != null ) {
            list.add( entry.handle );
            entry = entry.parent;
        }

        return (InternalFactHandle[]) list.toArray( new InternalFactHandle[list.size()] );
    }

    public long getRecency() {
        return this.recency;
    }

    public TupleMatch getTupleMatch(DefaultFactHandle handle) {
        return (TupleMatch) this.matches.get( handle );
    }

    public Map getTupleMatches() {
        return this.matches;
    }

    public TupleSink getTupleSink() {
        return this.sink;
    }

    public int matchesSize() {
        return this.matches.size();
    }

    public TupleMatch removeMatch(InternalFactHandle handle) {
        return (TupleMatch) this.matches.remove( handle );
    }

    public void retractTuple(PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        this.parent = null;
        this.sink.retractTuple( this,
                                context,
                                workingMemory );
    }

    public void assertTuple(PropagationContext context,
                            InternalWorkingMemory workingMemory) {
        this.sink.assertTuple( this,
                               context,
                               workingMemory );
    }

    public void modifyTuple(PropagationContext context,
                            InternalWorkingMemory workingMemory) {
        this.sink.modifyTuple( this,
                               context,
                               workingMemory );
    }

    public void setActivation(Activation activation) {
        this.activation = activation;
    }
        
    
    public int hashCode() {
        return this.handle.hashCode();
    }
    
    public boolean equals(Object object) {
        if ( object == this ) {
            return true;
        }
        
        if ( object == null || object.getClass() == getClass() ) {
            return false;
        }
        
        ReteTuple other = ( ReteTuple ) object;
        
        return ( this.handle.getId() == other.handle.getId() );            
    }
}
