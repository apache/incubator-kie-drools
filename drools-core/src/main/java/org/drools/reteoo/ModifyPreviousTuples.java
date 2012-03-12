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

import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public class ModifyPreviousTuples {
    private LeftTuple                       leftTuple;
    private RightTuple                      rightTuple;

    public ModifyPreviousTuples(LeftTuple leftTuple,
                                RightTuple rightTuple) {
        this.leftTuple = leftTuple;
        this.rightTuple = rightTuple;
    }
    
    public LeftTuple peekLeftTuple() {
        return this.leftTuple;
    }
    
    public RightTuple peekRightTuple() {
        return this.rightTuple;
    }

    public void removeLeftTuple() {
        LeftTuple current = this.leftTuple;
        current.setLeftParentPrevious( null );
        this.leftTuple = current.getLeftParentNext();
        current.setLeftParentNext( null );        
    }
    
    public void removeRightTuple() {
        RightTuple current = this.rightTuple;
        current.setHandlePrevious( null );
        this.rightTuple = current.getHandleNext();
        current.setHandleNext( null );       
    }        
    
    public void retractTuples(PropagationContext context,
                              InternalWorkingMemory workingMemory) {
        // retract any remaining LeftTuples
        if ( this.leftTuple != null ) {
            for ( LeftTuple current = this.leftTuple; current != null; current = (LeftTuple) current.getLeftParentNext() ) {
                current.getLeftTupleSink().retractLeftTuple( current,
                                                             context,
                                                             workingMemory );
            }
        }
        
        // retract any remaining RightTuples
        if (this.rightTuple != null ) {
            for ( RightTuple current = this.rightTuple; current != null; current = (RightTuple) current.getHandleNext() ) {
                current.getRightTupleSink().retractRightTuple( current,
                                                               context,
                                                               workingMemory );
            }
        }
    }

}
