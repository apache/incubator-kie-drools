package org.drools.reteoo;

public class TupleIterator {
    public interface OnLeaf {
        public void execute(LeftTuple leafLeftTuple);
    }
    public static void  traverse(LeftTuple rootLeftTuple, LeftTuple leftTuple, OnLeaf onLeaf) {
        boolean down = true;
        while ( leftTuple != null ) {
            while ( down ) {
                // iterate to next leaf
                if ( leftTuple.firstChild != null ) {
                    leftTuple = leftTuple.firstChild;
                } else {
                    down = false;
                }
            }
            
            // we know we are at a leaf here
            onLeaf.execute( leftTuple );

            if ( leftTuple.getLeftParentNext() != null ) {
                // iterate to next peer
                leftTuple = leftTuple.getLeftParentNext();
                // attempt to traverse that peer's children
                if ( leftTuple.firstChild != null ) {
                    down = true;    
                }
            } else {
                // iterate to parent's next peer and set down to find next leaf
                // never go beyond the specified root node
                while (leftTuple != rootLeftTuple && leftTuple.getLeftParentNext() == null) {
                    leftTuple = leftTuple.getLeftParent();
                    // onNode
                }
                leftTuple = leftTuple.getLeftParentNext();
                
                down = true;
            }
        }        
    }
}
