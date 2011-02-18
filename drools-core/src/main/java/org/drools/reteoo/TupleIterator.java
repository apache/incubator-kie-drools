/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
