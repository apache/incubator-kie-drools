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

package org.drools.core.common;

import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;

public class RightTupleSetsImpl implements RightTupleSets {


    protected RightTuple        insertFirst;
    protected volatile int      insertSize;

    protected RightTuple        deleteFirst;
    protected volatile int      deleteSize;

    protected RightTuple        updateFirst;
    protected volatile int      updateSize;

    private final BetaMemory bm;

    public RightTupleSetsImpl(BetaMemory bm) {
        this.bm = bm;
    }

    public RightTuple getInsertFirst() {
        return this.insertFirst;
    }

    public RightTuple getDeleteFirst() {
        return this.deleteFirst;
    }

    public RightTuple getUpdateFirst() {
        return this.updateFirst;
    }

    public void resetInsert() {
        insertFirst = null;
        insertSize = 0;
    }

    public void resetDelete() {
        deleteFirst = null;
        deleteSize = 0;
    }

    public void resetUpdate() {
        updateFirst = null;
        updateSize = 0;
    }

    public void resetAll() {
        resetInsert();
        resetDelete();
        resetUpdate();
    }

    public int insertSize() {
        return this.insertSize;
    }

    public int deleteSize() {
        return this.deleteSize;
    }

    public int updateSize() {
        return this.updateSize;
    }

    public boolean addInsert(RightTuple rightTuple) {
        if ( isEmpty() ) {
            bm.setNodeDirtyWithoutNotify();
        }

        rightTuple.setStagedType( LeftTuple.INSERT );
        if ( insertFirst == null ) {
            insertFirst = rightTuple;
        } else {
            rightTuple.setStagedNext( insertFirst );
            insertFirst.setStagePrevious( rightTuple );
            insertFirst = rightTuple;
        }
        return (insertSize++ == 0);
    }

    public boolean addDelete(RightTuple rightTuple) {
        if ( isEmpty() ) {
            bm.setNodeDirtyWithoutNotify();
        }

        switch ( rightTuple.getStagedType() ) {
            // handle clash with already staged entries
            case LeftTuple.INSERT:
                removeInsert( rightTuple );
                return deleteSize == 0;
            case LeftTuple.UPDATE:
                removeUpdate( rightTuple );
                break;
        }

        rightTuple.setStagedType( LeftTuple.DELETE );
        if ( deleteFirst == null ) {
            deleteFirst = rightTuple;
        } else {
            rightTuple.setStagedNext( deleteFirst );
            deleteFirst.setStagePrevious( rightTuple );
            deleteFirst = rightTuple;
        }
        return (deleteSize++ == 0);
    }


    public boolean addUpdate(RightTuple rightTuple) {
        if ( isEmpty() ) {
            bm.setNodeDirtyWithoutNotify();
        }

        if (rightTuple.getStagedType() != LeftTuple.NONE) {
            // do nothing, it's already staged as insert or an update, which means it's already scheduled for eval too.
            return false;
        }

        rightTuple.setStagedType( LeftTuple.UPDATE );
        if ( updateFirst == null ) {
            updateFirst = rightTuple;
        } else {
            rightTuple.setStagedNext( updateFirst );
            updateFirst.setStagePrevious( rightTuple );
            updateFirst = rightTuple;
        }
        return (updateSize++ == 0);
    }

    public void removeInsert(RightTuple rightTuple) {
        rightTuple.setStagedType( LeftTuple.NONE );
        if ( rightTuple == insertFirst ) {
            RightTuple next = rightTuple.getStagedNext();
            if ( next != null ) {
                next.setStagePrevious( null );
            }
            insertFirst = next;
        } else {
            RightTuple next = rightTuple.getStagedNext();
            RightTuple previous = rightTuple.getStagedPrevious();
            if ( next != null ) {
                next.setStagePrevious( previous );
            }
            previous.setStagedNext( next );
        }
        rightTuple.clearStaged();
        insertSize--;
    }

    public void removeDelete(RightTuple rightTuple) {
        rightTuple.setStagedType( LeftTuple.NONE );
        if ( rightTuple == deleteFirst ) {
            RightTuple next = rightTuple.getStagedNext();
            if ( next != null ) {
                next.setStagePrevious( null );
            }
            deleteFirst = next;
        } else {
            RightTuple next = rightTuple.getStagedNext();
            RightTuple previous = rightTuple.getStagedPrevious();
            if ( next != null ) {
                next.setStagePrevious( previous );
            }
            previous.setStagedNext( next );

        }
        rightTuple.clearStaged();
        deleteSize--;
    }

    public void removeUpdate(RightTuple rightTuple) {
        rightTuple.setStagedType( LeftTuple.NONE );
        if ( rightTuple == updateFirst ) {
            RightTuple next = rightTuple.getStagedNext();
            if ( next != null ) {
                next.setStagePrevious( null );
            }
            updateFirst = next;
        } else {
            RightTuple next = rightTuple.getStagedNext();
            RightTuple previous = rightTuple.getStagedPrevious();
            if ( next != null ) {
                next.setStagePrevious( previous );
            }
            previous.setStagedNext( next );
        }
        rightTuple.clearStaged();
        updateSize--;
    }

    public void addAllInserts(RightTupleSets tupleSets) {
        RightTupleSetsImpl tupleSetsImpl = (RightTupleSetsImpl) tupleSets;
        if ( insertFirst == null ) {
            insertFirst = tupleSetsImpl.getInsertFirst();
            insertSize = tupleSetsImpl.insertSize;
        } else {
            RightTuple current = insertFirst;
            RightTuple last = null;
            while ( current != null ) {
                last = current;
                current = current.getStagedNext();
            }
            RightTuple rightTuple = tupleSetsImpl.getInsertFirst();
            last.setStagedNext( rightTuple );
            rightTuple.setStagePrevious( last );
            insertSize = insertSize + tupleSetsImpl.insertSize();
        }
    }

    public void addAllDeletes(RightTupleSets tupleSets) {
        RightTupleSetsImpl tupleSetsImpl = (RightTupleSetsImpl) tupleSets;
        if ( deleteFirst == null ) {
            deleteFirst = tupleSetsImpl.getDeleteFirst();
            deleteSize = tupleSetsImpl.deleteSize;
        } else {
            RightTuple current = deleteFirst;
            RightTuple last = null;
            while ( current != null ) {
                last = current;
                current = current.getStagedNext();
            }
            RightTuple rightTuple = tupleSetsImpl.getDeleteFirst();
            last.setStagedNext( rightTuple );
            rightTuple.setStagePrevious( last );
            deleteSize = deleteSize + tupleSetsImpl.deleteSize();
        }
    }

    public void addAllUpdates(RightTupleSets tupleSets) {
        RightTupleSetsImpl tupleSetsImpl = (RightTupleSetsImpl) tupleSets;
        if ( updateFirst == null ) {
            updateFirst = tupleSetsImpl.getUpdateFirst();
            updateSize = tupleSetsImpl.updateSize;
        } else {
            RightTuple current = updateFirst;
            RightTuple last = null;
            while ( current != null ) {
                last = current;
                current = current.getStagedNext();
            }
            RightTuple rightTuple = tupleSetsImpl.getUpdateFirst();
            last.setStagedNext( rightTuple );
            rightTuple.setStagePrevious( last );
            updateSize = updateSize + tupleSetsImpl.updateSize();
        }
    }

    public void addAll(RightTupleSets source) {
        addAllInserts( source );
        addAllDeletes( source );
        addAllUpdates( source );
    }

//    public void splitInsert(RightTuple rightTuple,
//                            int count) {
//        insertFirst = rightTuple;
//        rightTuple.setStagePrevious( null );
//        insertSize = insertSize - count;
//    }
//
//    public void splitDelete(RightTuple rightTuple,
//                            int count) {
//        deleteFirst = rightTuple;
//        rightTuple.setStagePrevious( null );
//        deleteSize = deleteSize - count;
//    }
//
//    public void splitUpdate(RightTuple rightTuple,
//                            int count) {
//        updateFirst = rightTuple;
//        rightTuple.setStagePrevious( null );
//        updateSize = updateSize - count;
//    }

    public void clear() {
        for ( RightTuple rightTuple = getInsertFirst(); rightTuple != null; ) {
            RightTuple next =  rightTuple.getStagedNext();
            rightTuple.clearStaged();
            rightTuple = next;
        }

        for ( RightTuple rightTuple = getDeleteFirst(); rightTuple != null; ) {
            RightTuple next =  rightTuple.getStagedNext();
            rightTuple.clearStaged();
            rightTuple = next;
        }

        for ( RightTuple rightTuple = getUpdateFirst(); rightTuple != null; ) {
            RightTuple next =  rightTuple.getStagedNext();
            rightTuple.clearStaged();
            rightTuple = next;
        }

        resetAll();
    }

    public RightTupleSets takeAll() {
        if ( !isEmpty() ) {
            bm.setNodeDirtyWithoutNotify();
        }

        RightTupleSetsImpl clone = new RightTupleSetsImpl(bm);
        clone.insertSize = this.insertSize;
        clone.deleteSize = this.deleteSize;
        clone.updateSize = this.updateSize;
        clone.insertFirst = this.insertFirst;
        clone.deleteFirst = this.deleteFirst;
        clone.updateFirst = this.updateFirst;

        this.insertSize = 0;
        this.deleteSize = 0;
        this.updateSize = 0;
        this.insertFirst = null;
        this.deleteFirst = null;
        this.updateFirst = null;

        return clone;
    }

    public String toStringSizes() {
        return "TupleSets[insertSize=" + insertSize + ", deleteSize=" + deleteSize + ", updateSize=" + updateSize + "]";
    }

    public String toString() {
        StringBuilder sbuilder = new StringBuilder();

        sbuilder.append( "Inserted:\n" );
        for ( RightTuple rightTuple = getInsertFirst(); rightTuple != null; rightTuple = rightTuple.getStagedNext() ) {
            sbuilder.append( " " + rightTuple + "\n" );
        }

        sbuilder.append( "Deleted:\n" );
        for ( RightTuple rightTuple = getDeleteFirst(); rightTuple != null; rightTuple = rightTuple.getStagedNext() ) {
            sbuilder.append( " " + rightTuple + "\n" );
        }

        sbuilder.append( "Updated:\n" );
        for ( RightTuple rightTuple = getUpdateFirst(); rightTuple != null; rightTuple = rightTuple.getStagedNext() ) {
            sbuilder.append( " " + rightTuple + "\n" );
        }

        return sbuilder.toString();
    }

    public boolean isEmpty() {
        return getInsertFirst() == null && getDeleteFirst() == null && getUpdateFirst() == null;
    }
}
