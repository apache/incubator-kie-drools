package org.drools.core.common;

import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;

public class RightTupleSets {


    private RightTuple        insertFirst;
    private int              insertSize;

    private RightTuple        deleteFirst;
    private int              deleteSize;

    private RightTuple        updateFirst;
    private int              updateSize;

    public RightTupleSets() {
        
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
        return this.insertSize;
    }   
    
    public int updateSize() {
        return this.updateSize;
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
    
    public void addDelete(RightTuple rightTuple) {
        rightTuple.setStagedType( LeftTuple.DELETE );
        if ( deleteFirst == null ) {
            deleteFirst = rightTuple;
        } else {
            rightTuple.setStagedNext( deleteFirst );
            deleteFirst.setStagePrevious( rightTuple );
            deleteFirst = rightTuple;
        }
        deleteSize++;
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
        updateSize++;
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
        deleteSize--;
        rightTuple.clearStaged();
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
        if ( insertFirst == null ) {
            insertFirst = tupleSets.getInsertFirst();
            insertSize = tupleSets.insertSize;
        } else {
            RightTuple current = insertFirst;
            RightTuple last = null;
            while ( current != null ) {
                last = current;
                current = current.getStagedNext();
            }
            RightTuple rightTuple = tupleSets.getInsertFirst(); 
            last.setStagedNext( rightTuple );
            rightTuple.setStagePrevious( rightTuple );
            insertSize = insertSize + tupleSets.insertSize();
        }
    }   
    
    public void addAllDeletes(RightTupleSets tupleSets) {
        if ( deleteFirst == null ) {
            deleteFirst = tupleSets.getDeleteFirst();
            deleteSize = tupleSets.deleteSize;
        } else {
            RightTuple current = deleteFirst;
            RightTuple last = null;
            while ( current != null ) {
                last = current;
                current = current.getStagedNext();
            }
            RightTuple rightTuple = tupleSets.getDeleteFirst(); 
            last.setStagedNext( rightTuple );
            rightTuple.setStagePrevious( rightTuple );
            deleteSize = deleteSize + tupleSets.deleteSize();
        }
    }      

    public void addAllUpdates(RightTupleSets tupleSets) {
        if ( updateFirst == null ) {
            updateFirst = tupleSets.getUpdateFirst();
            updateSize = tupleSets.updateSize;
        } else {
            RightTuple current = updateFirst;
            RightTuple last = null;
            while ( current != null ) {
                last = current;
                current = current.getStagedNext();
            }
            RightTuple rightTuple = tupleSets.getUpdateFirst(); 
            last.setStagedNext( rightTuple );
            rightTuple.setStagePrevious( rightTuple );
            updateSize = updateSize + tupleSets.updateSize();
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
