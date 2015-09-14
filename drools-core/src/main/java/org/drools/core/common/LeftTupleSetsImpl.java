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

import org.drools.core.reteoo.LeftTuple;

public class LeftTupleSetsImpl implements LeftTupleSets {

    private LeftTuple insertFirst;
    private LeftTuple deleteFirst;
    private LeftTuple updateFirst;

    public LeftTupleSetsImpl() { }

    LeftTupleSetsImpl(LeftTuple insertFirst, LeftTuple updateFirst, LeftTuple deleteFirst) {
        this.insertFirst = insertFirst;
        this.updateFirst = updateFirst;
        this.deleteFirst = deleteFirst;
    }

    public LeftTuple getInsertFirst() {
        return this.insertFirst;
    }

    public LeftTuple getDeleteFirst() {
        return this.deleteFirst;
    }

    public LeftTuple getUpdateFirst() {
        return this.updateFirst;
    }

    public void resetAll() {
        insertFirst = null;
        deleteFirst = null;
        updateFirst = null;
    }

    public boolean addInsert(LeftTuple leftTuple) {
        if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
            // do nothing, it's already staged as an update, which means it's already scheduled for eval too.
            return false;
        }

        leftTuple.setStagedType( LeftTuple.INSERT );
        if ( insertFirst == null ) {
            insertFirst = leftTuple;
            return true;
        }
        leftTuple.setStagedNext( insertFirst );
        insertFirst.setStagePrevious( leftTuple );
        insertFirst = leftTuple;
        return false;
    }

    public boolean addDelete(LeftTuple leftTuple) {
        switch ( leftTuple.getStagedType() ) {
            // handle clash with already staged entries
            case LeftTuple.INSERT:
                removeInsert( leftTuple );
                return deleteFirst == null;
            case LeftTuple.UPDATE:
                removeUpdate( leftTuple );
                break;
        }

        leftTuple.setStagedType( LeftTuple.DELETE );
        if ( deleteFirst == null ) {
            deleteFirst = leftTuple;
            return true;
        }
        leftTuple.setStagedNext( deleteFirst );
        deleteFirst.setStagePrevious( leftTuple );
        deleteFirst = leftTuple;
        return false;
    }


    public boolean addUpdate(LeftTuple leftTuple) {
        if (leftTuple.getStagedType() == LeftTuple.INSERT) {
            // do nothing, it's already staged as insert, which means it's already scheduled for eval too.
            return false;
        }

        leftTuple.setStagedType( LeftTuple.UPDATE );
        if ( updateFirst == null ) {
            updateFirst = leftTuple;
            return true;
        }
        leftTuple.setStagedNext( updateFirst );
        updateFirst.setStagePrevious( leftTuple );
        updateFirst = leftTuple;
        return false;
    }

    public void removeInsert(LeftTuple leftTuple) {
        leftTuple.setStagedType( LeftTuple.NONE );
        if ( leftTuple == insertFirst ) {
            LeftTuple next = leftTuple.getStagedNext();
            if ( next != null ) {
                next.setStagePrevious( null );
            }
            insertFirst = next;
        } else {
            LeftTuple next = leftTuple.getStagedNext();
            LeftTuple previous = leftTuple.getStagedPrevious();
            if ( next != null ) {
                next.setStagePrevious( previous );
            }
            previous.setStagedNext( next );
        }
        leftTuple.clearStaged();
    }

    public void removeDelete(LeftTuple leftTuple) {
        leftTuple.setStagedType( LeftTuple.NONE );
        if ( leftTuple == deleteFirst ) {
            LeftTuple next = leftTuple.getStagedNext();
            if ( next != null ) {
                next.setStagePrevious( null );
            }
            deleteFirst = next;
        } else {
            LeftTuple next = leftTuple.getStagedNext();
            LeftTuple previous = leftTuple.getStagedPrevious();
            if ( next != null ) {
                next.setStagePrevious( previous );
            }
            previous.setStagedNext( next );

        }
        leftTuple.clearStaged();
    }

    public void removeUpdate(LeftTuple leftTuple) {
        leftTuple.setStagedType( LeftTuple.NONE );
        if ( leftTuple == updateFirst ) {
            LeftTuple next = leftTuple.getStagedNext();
            if ( next != null ) {
                next.setStagePrevious( null );
            }
            updateFirst = next;
        } else {
            LeftTuple next = leftTuple.getStagedNext();
            LeftTuple previous = leftTuple.getStagedPrevious();
            if ( next != null ) {
                next.setStagePrevious( previous );
            }
            previous.setStagedNext( next );
        }
        leftTuple.clearStaged();
    }

    public void addAllInserts(LeftTupleSets tupleSets) {
        LeftTupleSetsImpl tupleSetsImpl = (LeftTupleSetsImpl) tupleSets;
        if ( tupleSetsImpl.getInsertFirst() != null ) {
            if ( insertFirst == null ) {
                insertFirst = tupleSetsImpl.getInsertFirst();
            } else {
                LeftTuple current = insertFirst;
                LeftTuple last = null;
                while ( current != null ) {
                    last = current;
                    current = current.getStagedNext();
                }
                LeftTuple leftTuple = tupleSetsImpl.getInsertFirst();
                last.setStagedNext( leftTuple );
                leftTuple.setStagePrevious( last );
            }
            tupleSetsImpl.insertFirst = null;
        }
    }

    public void addAllDeletes(LeftTupleSets tupleSets) {
        if ( tupleSets.getDeleteFirst() != null ) {
            if ( deleteFirst == null ) {
                deleteFirst = tupleSets.getDeleteFirst();
            } else {
                LeftTuple current = deleteFirst;
                LeftTuple last = null;
                while ( current != null ) {
                    last = current;
                    current = current.getStagedNext();
                }
                LeftTuple leftTuple = tupleSets.getDeleteFirst();
                last.setStagedNext( leftTuple );
                leftTuple.setStagePrevious( last );
            }
            ((LeftTupleSetsImpl) tupleSets).deleteFirst = null;
        }
    }

    public void addAllUpdates(LeftTupleSets tupleSets) {
        LeftTupleSetsImpl tupleSetsImpl = (LeftTupleSetsImpl) tupleSets;
        if ( tupleSetsImpl.getUpdateFirst() != null ) {
            if ( updateFirst == null ) {
                updateFirst = tupleSetsImpl.getUpdateFirst();
            } else {
                LeftTuple current = updateFirst;
                LeftTuple last = null;
                while ( current != null ) {
                    last = current;
                    current = current.getStagedNext();
                }
                LeftTuple leftTuple = tupleSetsImpl.getUpdateFirst();
                last.setStagedNext( leftTuple );
                leftTuple.setStagePrevious( last );
            }
            tupleSetsImpl.updateFirst = null;
        }
    }

    public void addAll(LeftTupleSets source) {
        addAllInserts( source );
        addAllDeletes( source );
        addAllUpdates( source );
    }

    @Override
    public LeftTupleSets takeAll() {
        LeftTupleSets clone = new LeftTupleSetsImpl(insertFirst, updateFirst, deleteFirst);
        resetAll();
        return clone;
    }

    /**
     * clear also ensures all contained LeftTuples are cleared
     * reset does not touch any contained tuples
     */
    public void clear() {
        for ( LeftTuple leftTuple = getInsertFirst(); leftTuple != null; ) {
            LeftTuple next =  leftTuple.getStagedNext();
            leftTuple.clearStaged();
            leftTuple = next;
        }

        for ( LeftTuple leftTuple = getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next =  leftTuple.getStagedNext();
            leftTuple.clearStaged();
            leftTuple = next;
        }

        for ( LeftTuple leftTuple = getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next =  leftTuple.getStagedNext();
            leftTuple.clearStaged();
            leftTuple = next;
        }

        resetAll();
    }

    @Override
    public boolean isEmpty() {
        return insertFirst == null && deleteFirst == null && updateFirst == null;
    }

    @Override
    public String toStringSizes() {
        return "TupleSets[hasInsert=" + (insertFirst != null) + ", hasDelete=" + (deleteFirst != null) + ", hasUpdate=" + (updateFirst != null) + "]";
    }

    public String toString() {
        StringBuilder sbuilder = new StringBuilder();

        sbuilder.append( "Inserted:\n" );
        for ( LeftTuple leftTuple = getInsertFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext() ) {
            sbuilder.append( " " ).append( leftTuple ).append( "\n" );
        }

        sbuilder.append( "Deleted:\n" );
        for ( LeftTuple leftTuple = getDeleteFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext() ) {
            sbuilder.append( " " ).append( leftTuple ).append( "\n" );
        }

        sbuilder.append( "Updated:\n" );
        for ( LeftTuple leftTuple = getUpdateFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext() ) {
            sbuilder.append( " " ).append( leftTuple ).append( "\n" );
        }

        return sbuilder.toString();
    }
}
