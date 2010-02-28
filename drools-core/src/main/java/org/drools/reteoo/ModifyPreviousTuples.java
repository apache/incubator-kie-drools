package org.drools.reteoo;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public class ModifyPreviousTuples {
    private LeftTuple                       leftTuple;
    private RightTuple                      rightTuple;

    private Map<RightTupleSink, RightTuple> rightTuples;
    private Map<LeftTupleSink, LeftTuple>   leftTuples;

    public ModifyPreviousTuples(LeftTuple leftTuple,
                                RightTuple rightTuple) {
        this.leftTuple = leftTuple;
        this.rightTuple = rightTuple;
    }

    public Map<RightTupleSink, RightTuple> getRightTuples() {
        return rightTuples;
    }

    public void setRightTuples(Map<RightTupleSink, RightTuple> rightTuples) {
        this.rightTuples = rightTuples;
    }

    public Map<LeftTupleSink, LeftTuple> getLeftTuples() {
        return leftTuples;
    }

    public void setLeftTuples(Map<LeftTupleSink, LeftTuple> leftTuples) {
        this.leftTuples = leftTuples;
    }

    public LeftTuple removeLeftTuple(LeftTupleSink sink) {
        if ( this.leftTuple == null ) {
            return null;
        }
        if ( this.leftTuples == null ) {
            if ( this.leftTuple.getLeftTupleSink() == sink ) {
                LeftTuple current = this.leftTuple;
                current.setLeftParentPrevious( null );
                this.leftTuple = current.getLeftParentNext();
                current.setLeftParentNext( null );
                return current;
            } else {
                this.leftTuples = new IdentityHashMap<LeftTupleSink, LeftTuple>();
                for ( ; leftTuple != null; leftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
                    this.leftTuples.put( leftTuple.getLeftTupleSink(),
                                         leftTuple );
                }
            }
        }
        return this.leftTuples.remove( sink );
    }

    public RightTuple removeRightTuple(RightTupleSink sink) {
        if ( this.rightTuple == null ) {
            return null;
        }        
        if ( this.rightTuples == null ) {
            if ( this.rightTuple.getRightTupleSink() == sink ) {
                RightTuple current = this.rightTuple;
                current.setHandlePrevious( null );
                this.rightTuple = current.getHandleNext();
                current.setHandleNext( null );

                return current;
            } else {
                this.rightTuples = new IdentityHashMap<RightTupleSink, RightTuple>();
                for ( ; rightTuple != null; rightTuple = (RightTuple) rightTuple.getHandleNext() ) {
                    this.rightTuples.put( rightTuple.getRightTupleSink(),
                                          rightTuple );
                }
            }
        }
        return this.rightTuples.remove( sink );
    }
    
    public void retractTuples(PropagationContext context,
                              InternalWorkingMemory workingMemory) {
        // retract any remaining LeftTuples
        if ( this.leftTuples == null ) {
            for ( LeftTuple current = this.leftTuple; current != null; current = (LeftTuple) current.getLeftParentNext() ) {
                current.getLeftTupleSink().retractLeftTuple( current,
                                                             context,
                                                             workingMemory );
            }            
        } else {
            for ( Entry<LeftTupleSink, LeftTuple> entry : this.leftTuples.entrySet() ) {
                entry.getKey().retractLeftTuple( entry.getValue(),
                                                 context,
                                                 workingMemory );
            }
        }        
        
        // retract any remaining RightTuples
        if (this.rightTuples == null ) {
            for ( RightTuple current = this.rightTuple; current != null; current = (RightTuple) current.getHandleNext() ) {
                current.getRightTupleSink().retractRightTuple( current,
                                                               context,
                                                               workingMemory );
            }              
        } else {
            for ( Entry<RightTupleSink, RightTuple> entry : this.rightTuples.entrySet() ) {
                entry.getKey().retractRightTuple( entry.getValue(),
                                                  context,
                                                  workingMemory );
            }
        }  
    }

}
