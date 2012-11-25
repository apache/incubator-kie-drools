package org.drools.common;

import org.drools.reteoo.LeftTuple;

public class StagedLeftTuples {

    private LeftTuple        insertFirst;
    private int              insertSize;

    private LeftTuple        deleteFirst;

    private LeftTuple        updateFirst;

    public StagedLeftTuples() {
        
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

    public void setInsert(LeftTuple leftTuple,
                          int size) {
        insertFirst = leftTuple;
        insertSize = size;
    }

    public void setDelete(LeftTuple leftTuple) {
        deleteFirst = leftTuple;
    }

    public void setUpdate(LeftTuple leftTuple) {
        updateFirst = leftTuple;
    }

    public void addAllInserts(LeftTuple leftTuple) {
        if ( insertFirst == null ) {
            insertFirst = leftTuple;
        } else {
            LeftTuple current = insertFirst;
            LeftTuple last = null;
            while ( current != null ) {
                last = current;
                current = current.getStagedNext();
            }
            last.setStagedNext( leftTuple );
            leftTuple.setStagePrevious( leftTuple );
        }
    }
    
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

    public void splitInsert(LeftTuple leftTuple,
                            int count) {
        insertFirst = leftTuple;
        leftTuple.setStagePrevious( null );
        insertSize = insertSize - count;
    }

    public int insertSize() {
        return this.insertSize;
    }

    
    public void addAllDeletes(LeftTuple leftTuple) {
        if ( deleteFirst == null ) {
            deleteFirst = leftTuple;
        } else {
            LeftTuple current = deleteFirst;
            LeftTuple last = null;
            while ( current != null ) {
                last = current;
                current = current.getStagedNext();
            }
            last.setStagedNext( leftTuple );
            leftTuple.setStagePrevious( leftTuple );
        }
    }
    
    public void addDelete(LeftTuple leftTuple) {
        leftTuple.setStagedType( LeftTuple.DELETE );
        if ( deleteFirst == null ) {
            deleteFirst = leftTuple;
        } else {
            leftTuple.setStagedNext( deleteFirst );
            deleteFirst.setStagePrevious( leftTuple );
            deleteFirst = leftTuple;
        }
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

    public void addAllUpdates(LeftTuple leftTuple) {
        if ( updateFirst == null ) {
            updateFirst = leftTuple;
        } else {
            LeftTuple current = updateFirst;
            LeftTuple last = null;
            while ( current != null ) {
                last = current;
                current = current.getStagedNext();
            }
            last.setStagedNext( leftTuple );
            leftTuple.setStagePrevious( leftTuple );
        }
    }    
    
    public void addUpdate(LeftTuple leftTuple) {
        leftTuple.setStagedType( LeftTuple.UPDATE );
        if ( updateFirst == null ) {
            updateFirst = leftTuple;
        } else {
            leftTuple.setStagedNext( updateFirst );
            updateFirst.setStagePrevious( leftTuple );
            updateFirst = leftTuple;
        }
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
    
    public void clear() {
        for ( LeftTuple leftTuple = getInsertFirst(); leftTuple != null; ) {
            LeftTuple next =  leftTuple.getStagedNext();
            leftTuple.clearStaged();
            leftTuple = next;
        }
        setInsert( null, 0 );
        
        for ( LeftTuple leftTuple = getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next =  leftTuple.getStagedNext();
            leftTuple.clearStaged();
            leftTuple = next;
        }
        setDelete( null);    
        
        for ( LeftTuple leftTuple = getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next =  leftTuple.getStagedNext();
            leftTuple.clearStaged();
            leftTuple = next;
        }
        setUpdate( null);             
    }    
    
    public void merge(StagedLeftTuples source) {
        addAllInserts( source.getInsertFirst() );
        addAllDeletes( source.getDeleteFirst() );
        addAllUpdates( source.getUpdateFirst() );
    }
    
    public String toString() {
        StringBuilder sbuilder = new StringBuilder();

        sbuilder.append( "Inserted:\n" );
        for ( LeftTuple leftTuple = getInsertFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext() ) {
            sbuilder.append( leftTuple + "\n" );
        }
        
        sbuilder.append( "Deleted:\n" );
        for ( LeftTuple leftTuple = getDeleteFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext() ) {
            sbuilder.append( leftTuple + "\n" );
        }
        
        sbuilder.append( "Updated:\n" );
        for ( LeftTuple leftTuple = getUpdateFirst(); leftTuple != null; leftTuple = leftTuple.getStagedNext() ) {
            sbuilder.append( leftTuple + "\n" );
        }        
        
        return sbuilder.toString();
    }
}