/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.reteoo;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.index.LeftTupleList;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;

public interface LeftTuple
        extends
        Entry,
        Tuple {

    static final short NONE   = 0;
    static final short INSERT = 1;
    static final short UPDATE = 2;
    static final short DELETE = 3;

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

    public short getStagedType();

    public void setStagedType(short stagedType);

    public LeftTuple getStagedNext();

    public void setStagedNext(LeftTuple stageNext);

    public LeftTuple getStagedPrevious();

    public void setStagePrevious(LeftTuple stagePrevious);

    public void clearStaged();

    void clearBlocker();
    
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

    LeftTuple skipEmptyHandles();

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

    public LeftTuple getRootLeftTuple();

    public PropagationContext getPropagationContext();

    public void setPropagationContext(PropagationContext propagationContext);

    void clear();

    void setPeer(LeftTuple peer);
    
    LeftTuple getPeer();

}
