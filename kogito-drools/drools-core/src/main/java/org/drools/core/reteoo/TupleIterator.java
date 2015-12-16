/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

public class TupleIterator {
    public interface OnLeaf {
        public void execute(LeftTuple leafLeftTuple);
    }
    public static void  traverse(LeftTuple rootLeftTuple, LeftTuple leftTuple, OnLeaf onLeaf) {
        boolean down = true;
        while ( leftTuple != null ) {
            while ( down ) {
                // iterate to next leaf
                if ( leftTuple.getFirstChild() != null ) {
                    leftTuple = leftTuple.getFirstChild();
                } else {
                    down = false;
                }
            }
            
            // we know we are at a leaf here
            onLeaf.execute( leftTuple );

            if ( leftTuple.getHandleNext() != null ) {
                // iterate to next peer
                leftTuple = leftTuple.getHandleNext();
                // attempt to traverse that peer's children
                if ( leftTuple.getFirstChild() != null ) {
                    down = true;
                }
            } else {
                // iterate to parent's next peer and set down to find next leaf
                // never go beyond the specified root node
                while (leftTuple != rootLeftTuple && leftTuple.getHandleNext() == null) {
                    leftTuple = leftTuple.getLeftParent();
                    // onNode
                }
                leftTuple = leftTuple.getHandleNext();
                
                down = true;
            }
        }
    }
}
