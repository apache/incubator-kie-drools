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

package org.drools.core.reteoo;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.spi.PropagationContext;

public class ModifyPreviousTuples {
    private LeftTuple                       leftTuple;
    private RightTuple                      rightTuple;
    private boolean                         unlinkingEnabled;

    public ModifyPreviousTuples(LeftTuple leftTuple,                                
                                RightTuple rightTuple, 
                                boolean unlinkingEnabled) {
        this.leftTuple = leftTuple;
        this.rightTuple = rightTuple;
        this.unlinkingEnabled = unlinkingEnabled;
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
    
    public void retractTuples(PropagationContext pctx,
                              InternalWorkingMemory wm) {
        // retract any remaining LeftTuples
        if ( this.leftTuple != null ) {
            for ( LeftTuple current = this.leftTuple; current != null; current = (LeftTuple) current.getLeftParentNext() ) {
                if ( unlinkingEnabled ) {
                    LeftInputAdapterNode liaNode = (LeftInputAdapterNode) current.getLeftTupleSink().getLeftTupleSource();
                    if (liaNode != null) { // can be null only in tests
                        LiaNodeMemory lm = ( LiaNodeMemory )  wm.getNodeMemory( liaNode );
                        LeftInputAdapterNode.doDeleteObject( current, pctx, lm.getSegmentMemory(), wm, liaNode, true, lm );
                    }
                } else {
                    current.getLeftTupleSink().retractLeftTuple( current,
                                                                 pctx,
                                                                 wm );                    
                }
            }
        }
        
        // retract any remaining RightTuples
        if (this.rightTuple != null ) {
            for ( RightTuple current = this.rightTuple; current != null; current = (RightTuple) current.getHandleNext() ) {
                BetaNode node = ( BetaNode ) current.getRightTupleSink();
                if ( unlinkingEnabled) {
                    BetaMemory bm = BetaNode.getBetaMemory( node, wm );
                    current.setPropagationContext( pctx );
                    BetaNode.doDeleteRightTuple( current, node, wm, bm );
                } else {
                    node.retractRightTuple( current,
                                            pctx,
                                            wm );
                }
            }
        }
    }

}
