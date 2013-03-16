package org.drools.core.common;

import org.drools.core.reteoo.LeftTuple;

public class LeftTupleSets {

    private LeftTuple        insertFirst;
    private int              insertSize;

    private LeftTuple        deleteFirst;
    private int              deleteSize;

    private LeftTuple        updateFirst;
    private int              updateSize;

    public LeftTupleSets() {
        
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
        if ( tupleSets.getInsertFirst() != null ) {
            if ( insertFirst == null ) {
                insertFirst = tupleSets.getInsertFirst();
                insertSize = tupleSets.insertSize;
            } else {
                LeftTuple current = insertFirst;
                LeftTuple last = null;
                while ( current != null ) {
                    last = current;
                    current = current.getStagedNext();
                }
                LeftTuple leftTuple = tupleSets.getInsertFirst();
                last.setStagedNext( leftTuple );
                leftTuple.setStagePrevious( leftTuple );
                insertSize = insertSize + tupleSets.insertSize();
            }
            tupleSets.insertSize = 0;
            tupleSets.insertFirst = null;
        }
    }
    
    public void addAllDeletes(LeftTupleSets tupleSets) {
        if ( tupleSets.getDeleteFirst() != null ) {
            if ( deleteFirst == null ) {
                deleteFirst = tupleSets.getDeleteFirst();
                deleteSize = tupleSets.deleteSize;
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
                deleteSize = deleteSize + tupleSets.deleteSize();
            }
            tupleSets.deleteFirst = null;
            tupleSets.deleteSize = 0;
        }
    }      

    public void addAllUpdates(LeftTupleSets tupleSets) {
        if ( tupleSets.getUpdateFirst() != null ) {
            if ( updateFirst == null ) {
                updateFirst = tupleSets.getUpdateFirst();
                updateSize = tupleSets.updateSize;
            } else {
                LeftTuple current = updateFirst;
                LeftTuple last = null;
                while ( current != null ) {
                    last = current;
                    current = current.getStagedNext();
                }
                LeftTuple leftTuple = tupleSets.getUpdateFirst();
                last.setStagedNext( leftTuple );
                leftTuple.setStagePrevious( leftTuple );
                updateSize = updateSize + tupleSets.updateSize();
            }
            tupleSets.updateFirst = null;
            tupleSets.updateSize = 0;
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

    public boolean isEmpty() {
        return getInsertFirst() == null && getDeleteFirst() == null && getUpdateFirst() == null;
    }
    
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
