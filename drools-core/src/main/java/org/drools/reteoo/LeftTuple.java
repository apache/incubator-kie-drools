package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.LeftTupleList;
import org.drools.rule.Declaration;
import org.drools.spi.Tuple;

public interface LeftTuple extends Entry, Tuple {

    public  void reAdd();
    public  void reAddLeft();

    public  void reAddRight();

    public  void unlinkFromLeftParent();

    public  void unlinkFromRightParent();

    public  int getIndex();

    public  LeftTupleSink getLeftTupleSink();

    /* Had to add the set method because sink adapters must override 
     * the tuple sink set when the tuple was created.
     */
    public  void setLeftTupleSink(LeftTupleSink sink);

    public  LeftTuple getLeftParent();

    public  void setLeftParent(LeftTuple leftParent);

    public  LeftTuple getLeftParentPrevious();

    public  void setLeftParentPrevious(LeftTuple leftParentLeft);

    public  LeftTuple getLeftParentNext();

    public  void setLeftParentNext(LeftTuple leftParentright);

    public  RightTuple getRightParent();

    public  void setRightParent(RightTuple rightParent);

    public  LeftTuple getRightParentPrevious();

    public  void setRightParentPrevious(LeftTuple rightParentLeft);

    public  LeftTuple getRightParentNext();

    public  void setRightParentNext(LeftTuple rightParentRight);

    public  InternalFactHandle get(final int index);

    public  LeftTupleList getMemory();

    public  void setMemory(LeftTupleList memory);

    public  Entry getPrevious();

    public  void setPrevious(Entry previous);

    public  void setNext(final Entry next);

    public  Entry getNext();

    public  InternalFactHandle getLastHandle();

    public  InternalFactHandle get(final Declaration declaration);

    /**
     * Returns the fact handles in reverse order
     */
    public  InternalFactHandle[] getFactHandles();

    public  InternalFactHandle[] toFactHandles();

    public  void setBlocker(RightTuple blocker);

    public  RightTuple getBlocker();

    public  LeftTuple getBlockedPrevious();

    public  void setBlockedPrevious(LeftTuple blockerPrevious);

    public  LeftTuple getBlockedNext();

    public  void setBlockedNext(LeftTuple blockerNext);

    public  Object getObject();

    public  void setObject(final Object object);

    public  String toString();

    public  int hashCode();

    /**
     * We use this equals method to avoid the cast
     * @param tuple
     * @return
     */
    public  boolean equals(final LeftTuple other);

    public  boolean equals(final Object object);

    public  int size();

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
    public  LeftTuple getSubTuple(final int elements);

    public  Object[] toObjectArray();

    public  LeftTuple getParent();

    public  String toTupleTree(int indent);

    public  void increaseActivationCountForEvents();

    public  void decreaseActivationCountForEvents();
    
    public InternalFactHandle getHandle();

    public void setHandle(InternalFactHandle handle);

    public LeftTuple getFirstChild();

    public void setFirstChild(LeftTuple firstChild);
    
    public LeftTuple getLastChild();

    public void setLastChild(LeftTuple lastChild);

    public LeftTupleSink getSink();

    public void setSink(LeftTupleSink sink);

    public void setIndex(int index);

    public void setParent(LeftTuple parent);    
    
    boolean isExpired();

}