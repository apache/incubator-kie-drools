package org.drools.common;

import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RightTuple;

public class StagedRightTuples {

    private RightTuple        insertFirst;
    private int              insertSize;

    private RightTuple        deleteFirst;

    private RightTuple        updateFirst;

    public StagedRightTuples() {
        
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

    public void setInsert(RightTuple rightTuple,
                          int size) {
        insertFirst = rightTuple;
        insertSize = size;
    }

    public void setDelete(RightTuple rightTuple) {
        deleteFirst = rightTuple;
    }

    public void setUpdate(RightTuple rightTuple) {
        updateFirst = rightTuple;
    }

    public void addInsert(RightTuple rightTuple) {
        rightTuple.setStagedType( LeftTuple.INSERT );
        if ( insertFirst == null ) {
            insertFirst = rightTuple;
        } else {
            rightTuple.setStagedNext( insertFirst );
            insertFirst.setStagePrevious( rightTuple );
            insertFirst = rightTuple;
        }
        insertSize++;
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
        insertSize--;
        rightTuple.clearStaged();
    }

    public void splitInsert(RightTuple rightTuple,
                            int count) {
        insertFirst = rightTuple;
        rightTuple.setStagePrevious( null );
        insertSize = insertSize - count;
    }

    public int insertSize() {
        return this.insertSize;
    }

    public void addDelete(RightTuple rightTuple) {
        rightTuple.setStagedType( LeftTuple.DELETE );
        if ( deleteFirst == null ) {
            deleteFirst = rightTuple;
        } else {
            rightTuple.setStagedNext( deleteFirst );
            deleteFirst.setStagePrevious( rightTuple );
            deleteFirst = rightTuple;
        }
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
    }

    public void addUpdate(RightTuple rightTuple) {
        rightTuple.setStagedType( LeftTuple.UPDATE );
        if ( updateFirst == null ) {
            updateFirst = rightTuple;
        } else {
            rightTuple.setStagedNext( updateFirst );
            updateFirst.setStagePrevious( rightTuple );
            updateFirst = rightTuple;
        }
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
    }
    
    public void clear() {
        for ( RightTuple rightTuple = getInsertFirst(); rightTuple != null; ) {
            RightTuple next =  rightTuple.getStagedNext();
            rightTuple.clearStaged();
            rightTuple = next;
        }
        setInsert( null, 0 );
        
        for ( RightTuple rightTuple = getDeleteFirst(); rightTuple != null; ) {
            RightTuple next =  rightTuple.getStagedNext();
            rightTuple.clearStaged();
            rightTuple = next;
        }
        setDelete( null);    
        
        for ( RightTuple rightTuple = getUpdateFirst(); rightTuple != null; ) {
            RightTuple next =  rightTuple.getStagedNext();
            rightTuple.clearStaged();
            rightTuple = next;
        }
        setUpdate( null);             
    }    
    
    public String toString() {
        StringBuilder sbuilder = new StringBuilder();

        sbuilder.append( "Inserted:\n" );
        for ( RightTuple rightTuple = getInsertFirst(); rightTuple != null; rightTuple = rightTuple.getStagedNext() ) {
            sbuilder.append( rightTuple + "\n" );
        }
        
        sbuilder.append( "Deleted:\n" );
        for ( RightTuple rightTuple = getDeleteFirst(); rightTuple != null; rightTuple = rightTuple.getStagedNext() ) {
            sbuilder.append( rightTuple + "\n" );
        }
        
        sbuilder.append( "Updated:\n" );
        for ( RightTuple rightTuple = getUpdateFirst(); rightTuple != null; rightTuple = rightTuple.getStagedNext() ) {
            sbuilder.append( rightTuple + "\n" );
        }        
        
        return sbuilder.toString();
    }
}