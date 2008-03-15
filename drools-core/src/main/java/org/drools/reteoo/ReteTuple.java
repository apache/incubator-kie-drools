package org.drools.reteoo;

import java.util.ArrayList;
import java.util.List;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

import org.drools.base.ShadowProxy;
import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;
import org.drools.spi.Activation;
import org.drools.spi.Tuple;
import org.drools.util.Entry;

public class ReteTuple
    implements
    Tuple,
    Entry {
    private static final long        serialVersionUID = 400L;

    private int                      index;

    private InternalFactHandle handle;

    private ReteTuple                parent;

    private Activation               activation;

    private long                     recency;

    private int                      hashCode;

    private InternalFactHandle       match;

    private Entry                    next;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public ReteTuple() {
        
    }
    public ReteTuple(final InternalFactHandle handle) {
        this.recency = handle.getRecency();
        this.handle = handle;
        int h = handle.hashCode();
        h += ~(h << 9);
        h ^= (h >>> 14);
        h += (h << 4);
        h ^= (h >>> 10);
        this.hashCode = h;
    }

    public ReteTuple(final ReteTuple tuple) {
        this.index = tuple.index;
        this.parent = tuple.parent;
        this.recency = tuple.recency;
        this.handle = tuple.handle;
        this.hashCode = tuple.hashCode();
    }

    public ReteTuple(final ReteTuple parentTuple,
                     final InternalFactHandle handle) {
        this.index = parentTuple.index + 1;
        this.parent = parentTuple;
        this.recency = parentTuple.recency + handle.getRecency();
        this.handle = handle;
        this.hashCode = parentTuple.hashCode ^ (handle.hashCode() * 31);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        index   = in.readInt();
        handle  = (InternalFactHandle)in.readObject();
        parent  = (ReteTuple)in.readObject();
        activation  = (Activation)in.readObject();
        recency = in.readLong();
        hashCode    = in.readInt();
        match   = (InternalFactHandle)in.readObject();
        next    = (Entry)in.readObject();

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(index);
        out.writeObject(handle);
        out.writeObject(parent);
        out.writeObject(activation);
        out.writeLong(recency);
        out.writeInt(hashCode);
        out.writeObject(match);
        out.writeObject(next);
    }

    public InternalFactHandle get(final int index) {
        ReteTuple entry = this;
        while ( entry.index != index ) {
            entry = entry.parent;
        }
        return entry.handle;
    }

    public void setNext(final Entry next) {
        this.next = next;
    }

    public Entry getNext() {
        return this.next;
    }

    public InternalFactHandle getLastHandle() {
        return this.handle;
    }

    public InternalFactHandle get(final Declaration declaration) {
        return get( declaration.getPattern().getOffset() );
    }

    public Activation getActivation() {
        return this.activation;
    }

    /**
     * Returns the fact handles in reverse order
     */
    public InternalFactHandle[] getFactHandles() {
        final List list = new ArrayList();
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


    public InternalFactHandle getMatch() {
        return match;
    }

    public void setMatch(InternalFactHandle match) {
        this.match = match;
    }

    public void setActivation(final Activation activation) {
        this.activation = activation;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer();

        ReteTuple entry = this;
        while ( entry != null ) {
            //buffer.append( entry.handle );
            buffer.append( entry.handle + "\n" );
            entry = entry.parent;
        }
        return buffer.toString();
    }

    /**
     * We use this equals method to avoid the cast
     * @param tuple
     * @return
     */
    public boolean equals(final ReteTuple other) {
        // we know the object is never null and always of the  type ReteTuple
        if ( other == this ) {
            return true;
        }

        // A ReteTuple is  only the same if it has the same hashCode, factId and parent
        if ( (other == null) || (this.hashCode != other.hashCode) ) {
            return false;
        }

        if ( this.handle != other.handle ) {
            return false;
        }

        if ( this.parent == null ) {
            return (other.parent == null);
        } else {
            return this.parent.equals( other.parent );
        }
    }

    public boolean equals(final Object object) {
        // we know the object is never null and always of the  type ReteTuple
        return equals( (ReteTuple) object );
    }

    public int size() {
        return this.index + 1;
    }

    /**
     * Returns the ReteTuple that contains the "elements"
     * first elements in this tuple.
     *
     * Use carefully as no cloning is made during this process.
     *
     * This method is used by TupleStartEqualsConstraint when
     * joining a subnetwork tuple into the main network tuple;
     *
     * @param elements the number of elements to return, starting from
     * the begining of the tuple
     *
     * @return a ReteTuple containing the "elements" first elements
     * of this tuple or null if "elements" is greater than size;
     */
    public ReteTuple getSubTuple(final int elements) {
        ReteTuple entry = this;
        if ( elements < this.size() ) {
            final int lastindex = elements - 1;

            while ( entry.index != lastindex ) {
                entry = entry.parent;
            }
        }
        return entry;
    }

    public Object[] toObjectArray() {
        Object[] objects = new Object[ this.index + 1 ];
        ReteTuple entry = this;
        while ( entry != null ) {
            Object object = entry.getLastHandle().getObject();
            if ( object instanceof ShadowProxy ) {
                object = ((ShadowProxy)object).getShadowedObject();
            }
            objects[entry.index] = object;
            entry = entry.parent;
        }
        return objects;
    }
}
