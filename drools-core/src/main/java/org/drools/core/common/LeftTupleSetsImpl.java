package org.drools.core.common;

import org.drools.core.reteoo.LeftTuple;

public class LeftTupleSetsImpl implements LeftTupleSets {

    private LeftTuple        insertFirst;
    private int              insertSize;

    private LeftTuple        deleteFirst;
    private int              deleteSize;

    private LeftTuple        updateFirst;
    private int              updateSize;

    public LeftTupleSetsImpl() {

    }

    @Override
    public LeftTuple getInsertFirst() {
        return this.insertFirst;
    }

    @Override
    public LeftTuple getDeleteFirst() {
        return this.deleteFirst;
    }

    @Override
    public LeftTuple getUpdateFirst() {
        return this.updateFirst;
    }

    @Override
    public void resetInsert() {
        insertFirst = null;
        insertSize = 0;
    }

    @Override
    public void resetDelete() {
        deleteFirst = null;
        deleteSize = 0;
    }

    @Override
    public void resetUpdate() {
        updateFirst = null;
        updateSize = 0;
    }

    @Override
    public void resetAll() {
        resetInsert();
        resetDelete();
        resetUpdate();
    }

    @Override
    public int insertSize() {
        return this.insertSize;
    }

    @Override
    public int deleteSize() {
        return this.insertSize;
    }

    @Override
    public int updateSize() {
        return this.updateSize;
    }

    @Override
    public void addInsert(LeftTuple leftTuple) {
        leftTuple.setStagedType( LeftTuple.INSERT );
        if ( insertFirst == null ) {
            insertFirst = leftTuple;
        } else {
            leftTuple.setStagedNext( insertFirst );
            insertFirst.setStagePrevious( leftTuple );
            insertFirst = leftTuple;
        }
        insertSize++;
    }

    @Override
    public void addDelete(LeftTuple leftTuple) {
        leftTuple.setStagedType( LeftTuple.DELETE );
        if ( deleteFirst == null ) {
            deleteFirst = leftTuple;
        } else {
            leftTuple.setStagedNext( deleteFirst );
            deleteFirst.setStagePrevious( leftTuple );
            deleteFirst = leftTuple;
        }
        deleteSize++;
    }


    @Override
    public void addUpdate(LeftTuple leftTuple) {
        leftTuple.setStagedType( LeftTuple.UPDATE );
        if ( updateFirst == null ) {
            updateFirst = leftTuple;
        } else {
            leftTuple.setStagedNext( updateFirst );
            updateFirst.setStagePrevious( leftTuple );
            updateFirst = leftTuple;
        }
        updateSize++;
    }

    @Override
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
        insertSize--;
        leftTuple.clearStaged();
    }

    @Override
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
        deleteSize--;
        leftTuple.clearStaged();
    }

    @Override
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
        updateSize--;
    }

    public void addAllInserts(LeftTupleSets tupleSets) {
        LeftTupleSetsImpl tupleSetsImpl = (LeftTupleSetsImpl) tupleSets;
        if ( tupleSetsImpl.getInsertFirst() != null ) {
            if ( insertFirst == null ) {
                insertFirst = tupleSetsImpl.getInsertFirst();
                insertSize = tupleSetsImpl.insertSize;
            } else {
                LeftTuple current = insertFirst;
                LeftTuple last = null;
                while ( current != null ) {
                    last = current;
                    current = current.getStagedNext();
                }
                LeftTuple leftTuple = tupleSetsImpl.getInsertFirst();
                last.setStagedNext( leftTuple );
                leftTuple.setStagePrevious( leftTuple );
                insertSize = insertSize + tupleSetsImpl.insertSize();
            }
            tupleSetsImpl.insertSize = 0;
            tupleSetsImpl.insertFirst = null;
        }
    }

    public void addAllDeletes(LeftTupleSets tupleSets) {
        LeftTupleSetsImpl tupleSetsImpl = (LeftTupleSetsImpl) tupleSets;
        if ( tupleSetsImpl.getDeleteFirst() != null ) {
            if ( deleteFirst == null ) {
                deleteFirst = tupleSetsImpl.getDeleteFirst();
                deleteSize = tupleSetsImpl.deleteSize;
            } else {
                LeftTuple current = deleteFirst;
                LeftTuple last = null;
                while ( current != null ) {
                    last = current;
                    current = current.getStagedNext();
                }
                LeftTuple leftTuple = tupleSets.getDeleteFirst();
                last.setStagedNext( leftTuple );
                leftTuple.setStagePrevious( leftTuple );
                deleteSize = deleteSize + tupleSetsImpl.deleteSize();
            }
            tupleSetsImpl.deleteFirst = null;
            tupleSetsImpl.deleteSize = 0;
        }
    }

    public void addAllUpdates(LeftTupleSets tupleSets) {
        LeftTupleSetsImpl tupleSetsImpl = (LeftTupleSetsImpl) tupleSets;
        if ( tupleSetsImpl.getUpdateFirst() != null ) {
            if ( updateFirst == null ) {
                updateFirst = tupleSetsImpl.getUpdateFirst();
                updateSize = tupleSetsImpl.updateSize;
            } else {
                LeftTuple current = updateFirst;
                LeftTuple last = null;
                while ( current != null ) {
                    last = current;
                    current = current.getStagedNext();
                }
                LeftTuple leftTuple = tupleSetsImpl.getUpdateFirst();
                last.setStagedNext( leftTuple );
                leftTuple.setStagePrevious( leftTuple );
                updateSize = updateSize + tupleSetsImpl.updateSize();
            }
            tupleSetsImpl.updateFirst = null;
            tupleSetsImpl.updateSize = 0;
        }
    }

    public void addAll(LeftTupleSets source) {
        addAllInserts( source );
        addAllDeletes( source );
        addAllUpdates( source );
    }

//    public void splitInsert(LeftTuple leftTuple,
//                            int count) {
//        insertFirst = leftTuple;
//        leftTuple.setStagePrevious( null );
//        insertSize = insertSize - count;
//    }
//
//    public void splitDelete(LeftTuple leftTuple,
//                            int count) {
//        deleteFirst = leftTuple;
//        leftTuple.setStagePrevious( null );
//        deleteSize = deleteSize - count;
//    }
//
//    public void splitUpdate(LeftTuple leftTuple,
//                            int count) {
//        updateFirst = leftTuple;
//        leftTuple.setStagePrevious( null );
//        updateSize = updateSize - count;
//    }

    @Override
    public LeftTupleSets takeAll() {
        LeftTupleSetsImpl clone = new LeftTupleSetsImpl();
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

    /**
     * clear also ensures all contained LeftTuples are cleared
     * reset does not touch any contained tuples
     */
    @Override
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
        return getInsertFirst() == null && getDeleteFirst() == null && getUpdateFirst() == null;
    }

    @Override
    public String toStringSizes() {
        return "TupleSets[insertSize=" + insertSize + ", deleteSize=" + deleteSize + ", updateSize=" + updateSize + "]";
    }

    public String toString() {
        StringBuilder sbuilder = new StringBuilder();

        sbuilder.append( "Inserted:\n" );
        for ( LeftTuple leftTuple = getInsertFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext() ) {
            sbuilder.append( " " + leftTuple + "\n" );
        }

        sbuilder.append( "Deleted:\n" );
        for ( LeftTuple leftTuple = getDeleteFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext() ) {
            sbuilder.append( " " + leftTuple + "\n" );
        }

        sbuilder.append( "Updated:\n" );
        for ( LeftTuple leftTuple = getUpdateFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext() ) {
            sbuilder.append( " " + leftTuple + "\n" );
        }

        return sbuilder.toString();
    }
}
