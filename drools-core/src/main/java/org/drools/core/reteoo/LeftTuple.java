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

import org.drools.core.spi.Tuple;

public interface LeftTuple extends Tuple {

    void reAddLeft();
    void reAddRight();

    LeftTupleSink getTupleSink();

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

    void clearBlocker();
    void setBlocker(RightTuple blocker);
    RightTuple getBlocker();

    LeftTuple getBlockedPrevious();
    void setBlockedPrevious(LeftTuple blockerPrevious);

    LeftTuple getBlockedNext();
    void setBlockedNext(LeftTuple blockerNext);

    /**
     * We use this equals method to avoid the cast
     */
    boolean equals(final LeftTuple other);

    Object[] toObjectArray();

    LeftTuple getParent();

    String toTupleTree(int indent);

    void setIndex(int index);

    void setParent(LeftTuple parent);

    void setPeer(LeftTuple peer);
    LeftTuple getPeer();

    LeftTuple getStagedPrevious();
    LeftTuple getStagedNext();
}
