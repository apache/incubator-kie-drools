package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.LeftTupleList;
import org.drools.spi.Tuple;

public interface LeftTuple extends Entry, Tuple {

    void reAdd();

    void reAddLeft();

    void reAddRight();

    void unlinkFromLeftParent();

    void unlinkFromRightParent();

    int getIndex();

    LeftTupleSink getLeftTupleSink();

    /* Had to add the set method because sink adapters must override 
     * the tuple sink set when the tuple was created.
     */
    void setLeftTupleSink(LeftTupleSink sink);

    LeftTuple getLeftParent();

    void setLeftParent(LeftTuple leftParent);

    LeftTuple getLeftParentPrevious();

    void setLeftParentPrevious(LeftTuple leftParentLeft);

    LeftTuple getLeftParentNext();

    void setLeftParentNext(LeftTuple leftParentright);

    RightTuple getRightParent();

    void setRightParent(RightTuple rightParent);

    LeftTuple getRightParentPrevious();

    void setRightParentPrevious(LeftTuple rightParentLeft);

    LeftTuple getRightParentNext();

    void setRightParentNext(LeftTuple rightParentRight);

    LeftTupleList getMemory();

    void setMemory(LeftTupleList memory);

    Entry getPrevious();

    void setPrevious(Entry previous);

    InternalFactHandle getLastHandle();

    void setBlocker(RightTuple blocker);

    RightTuple getBlocker();

    LeftTuple getBlockedPrevious();

    void setBlockedPrevious(LeftTuple blockerPrevious);

    LeftTuple getBlockedNext();

    void setBlockedNext(LeftTuple blockerNext);

    Object getObject();

    void setObject(final Object object);

    /**
     * We use this equals method to avoid the cast
     *
     * @param tuple
     * @return
     */
    boolean equals(final LeftTuple other);

    /**
     * Returns the ReteTuple that contains the "elements"
     * first elements in this tuple.
     * <p/>
     * Use carefully as no cloning is made during this process.
     * <p/>
     * This method is used by TupleStartEqualsConstraint when
     * joining a subnetwork tuple into the main network tuple;
     *
     * @param elements the number of elements to return, starting from
     *                 the begining of the tuple
     * @return a ReteTuple containing the "elements" first elements
     *         of this tuple or null if "elements" is greater than size;
     */
    LeftTuple getSubTuple(final int elements);

    Object[] toObjectArray();

    LeftTuple getParent();

    String toTupleTree(int indent);

    void increaseActivationCountForEvents();

    void decreaseActivationCountForEvents();

    InternalFactHandle getHandle();

    void setHandle(InternalFactHandle handle);

    LeftTuple getFirstChild();

    void setFirstChild(LeftTuple firstChild);

    LeftTuple getLastChild();

    void setLastChild(LeftTuple lastChild);

    LeftTupleSink getSink();

    void setSink(LeftTupleSink sink);

    void setIndex(int index);

    void setParent(LeftTuple parent);
    
    boolean isExpired();

}