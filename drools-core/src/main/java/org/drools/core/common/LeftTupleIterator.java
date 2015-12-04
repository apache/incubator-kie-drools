/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.common;

import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AccumulateNode.AccumulateContext;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.spi.Tuple;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;

public class LeftTupleIterator
    implements
    Iterator<LeftTuple> {
    protected InternalWorkingMemory wm;

    protected LeftTupleSink         node;

    protected   LeftTuple           currentLeftTuple;

    private   java.util.Iterator<InternalFactHandle> otnIterator;

    LeftTupleIterator() {

    }

    private LeftTupleIterator(InternalWorkingMemory wm,
                              LeftTupleSink         node) {
        this.wm = wm;
        this.node = node;        
        setFirstLeftTupleForNode();
    }
    
    public static Iterator<LeftTuple> iterator( InternalWorkingMemory wm,
                                                LeftTupleSink         node) {
        return new LeftTupleIterator(wm, node);
    }

    public void setFirstLeftTupleForNode() {
        LeftTupleSource source = node.getLeftTupleSource();

        this.currentLeftTuple = getFirstLeftTuple( source,
                                                   node,
                                                   wm );
    }

    public LeftTuple getFirstLeftTuple(LeftTupleSource source,
                                       LeftTupleSink sink,
                                       InternalWorkingMemory wm) {
        if ( wm.getKnowledgeBase().getConfiguration().isPhreakEnabled() && source instanceof AccumulateNode ) {
            AccumulateMemory accmem = (AccumulateMemory) wm.getNodeMemory( (MemoryFactory) source );
            BetaMemory memory = accmem.getBetaMemory();
            
            FastIterator localIt = memory.getLeftTupleMemory().fullFastIterator();
            Tuple leftTuple = BetaNode.getFirstTuple( memory.getLeftTupleMemory(), localIt );
            if( leftTuple != null ) {
                AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
                return accctx.getResultLeftTuple();
            }
            return null;
        }
        if ( source instanceof JoinNode || source instanceof NotNode || source instanceof FromNode ||source instanceof AccumulateNode ) {
            BetaMemory memory;
            FastIterator localIt;
            if ( source instanceof FromNode ) {
                memory = ((FromMemory) wm.getNodeMemory( (MemoryFactory) source )).getBetaMemory();
            } else if ( source instanceof AccumulateNode ) {
                memory = ((AccumulateMemory) wm.getNodeMemory( (MemoryFactory) source )).getBetaMemory();
            } else {
                memory = (BetaMemory) wm.getNodeMemory( (MemoryFactory) source );
            }

            localIt = memory.getLeftTupleMemory().fullFastIterator();
            Tuple leftTuple = BetaNode.getFirstTuple( memory.getLeftTupleMemory(), localIt );

            while ( leftTuple != null ) {
                for ( LeftTuple childleftTuple = leftTuple.getFirstChild(); childleftTuple != null; childleftTuple = childleftTuple.getLeftParentNext() ) {
                    if ( childleftTuple.getTupleSink() == sink ) {
                        return childleftTuple;
                    }
                }
                leftTuple = (LeftTuple) localIt.next( leftTuple );
            }
        }
        if ( source instanceof ExistsNode ) {
            BetaMemory memory = (BetaMemory) wm.getNodeMemory( (MemoryFactory) source );
            FastIterator localIt = memory.getRightTupleMemory().fullFastIterator();

            RightTuple rightTuple = (RightTuple) BetaNode.getFirstTuple( memory.getRightTupleMemory(), localIt );

            while ( rightTuple != null ) {
                if ( rightTuple.getBlocked() != null ) {
                    for ( LeftTuple leftTuple = rightTuple.getBlocked(); leftTuple != null; leftTuple = leftTuple.getBlockedNext() ) {
                        for ( LeftTuple childleftTuple = leftTuple.getFirstChild(); childleftTuple != null; childleftTuple = childleftTuple.getLeftParentNext() ) {
                            if ( childleftTuple.getTupleSink() == sink ) {
                                return childleftTuple;
                            }
                        }
                    }

                }
                rightTuple = (RightTuple) localIt.next( rightTuple );
            }
        } else if ( source instanceof LeftInputAdapterNode ) {
            ObjectSource os = ((LeftInputAdapterNode) source).getParentObjectSource();
            while ( !(os instanceof ObjectTypeNode) ) {
                os = os.getParentObjectSource();
            }

            ObjectTypeNode otn = (ObjectTypeNode) os;
            otnIterator = wm.getNodeMemory( otn ).iterator();

            while (otnIterator.hasNext()) {
                InternalFactHandle handle = otnIterator.next();
                for ( LeftTuple leftTuple = handle.getFirstLeftTuple(); leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
                    if ( leftTuple.getTupleSink() == sink ) {
                        return leftTuple;
                    }
                }
            }
        } else if ( source instanceof EvalConditionNode || source instanceof QueryElementNode ) {

            LeftTuple parentLeftTuple = getFirstLeftTuple( source.getLeftTupleSource(),
                                                           (LeftTupleSink) source,
                                                           wm );

            while ( parentLeftTuple != null ) {
                for ( LeftTuple leftTuple = parentLeftTuple.getFirstChild(); leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
                    if ( leftTuple.getTupleSink() == sink ) {
                        return leftTuple;
                    }
                }
                
                parentLeftTuple = getNextLeftTuple(  source.getLeftTupleSource(),
                                                     (LeftTupleSink) source,
                                                     parentLeftTuple,
                                                     wm );
            }
        }
        return null;
    }

    public LeftTuple getNextLeftTuple(LeftTupleSource source,
                                      LeftTupleSink sink,
                                      LeftTuple leftTuple,
                                      InternalWorkingMemory wm) {

        if ( otnIterator != null ) {
            LeftTuple leftParent = leftTuple.getLeftParent();
            
            while ( leftTuple != null ) {
                leftTuple = leftTuple.getLeftParentNext();
                
                for ( ; leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
                    // Iterate to find the next left tuple for this sink, skip tuples for other sinks due to sharing split
                    if ( leftTuple.getTupleSink() == sink ) {
                        return leftTuple;
                    }
                }
            }
                                    
            // We have a parent LeftTuple so try there next
            if ( leftParent != null ) {
                // we know it has to be evalNode query element node
                while ( leftParent != null ) {
                    leftParent = getNextLeftTuple( source.getLeftTupleSource(),
                                                   (LeftTupleSink) source,
                                                   leftParent,
                                                   wm );

                    if ( leftParent != null ) {
                        for ( leftTuple = leftParent.getFirstChild(); leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
                            if ( leftTuple.getTupleSink() == sink ) {
                                return leftTuple;
                            }
                        }
                    }
                }
                return null;
            }

            // We have exhausted the current FactHandle, now try the next
            while ( otnIterator.hasNext() ) {
                InternalFactHandle handle = otnIterator.next();
                for ( leftTuple = handle.getFirstLeftTuple(); leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
                    if ( leftTuple.getTupleSink() == sink ) {
                        return leftTuple;
                    }
                }
            }
            // We've exhausted this OTN so set the iterator to null
            otnIterator = null;

        } else if ( wm.getKnowledgeBase().getConfiguration().isPhreakEnabled() && source instanceof AccumulateNode ) {
            // when using phreak, accumulate result tuples will not link to leftParent, but to parent instead 
            BetaMemory memory = ((AccumulateMemory) wm.getNodeMemory( (MemoryFactory) source )).getBetaMemory();
            FastIterator localIt = memory.getLeftTupleMemory().fullFastIterator( leftTuple.getParent() );

            LeftTuple childLeftTuple = leftTuple;
            leftTuple = childLeftTuple.getParent();

            while ( leftTuple != null ) {
                if ( childLeftTuple == null ) {
                    childLeftTuple = leftTuple.getFirstChild();
                } else {
                    childLeftTuple = childLeftTuple.getLeftParentNext();
                }
                for ( ; childLeftTuple != null; childLeftTuple = childLeftTuple.getLeftParentNext() ) {
                    if ( childLeftTuple.getTupleSink() == sink ) {
                        return childLeftTuple;
                    }
                }
                leftTuple = (LeftTuple) localIt.next( leftTuple );
            }

        } else if ( source instanceof JoinNode || source instanceof NotNode|| source instanceof FromNode || source instanceof AccumulateNode ) {
            BetaMemory memory;
            FastIterator localIt;
            if ( source instanceof FromNode ) {
                memory = ((FromMemory) wm.getNodeMemory( (MemoryFactory) source )).getBetaMemory();
            } else if ( source instanceof AccumulateNode ) {
                memory = ((AccumulateMemory) wm.getNodeMemory( (MemoryFactory) source )).getBetaMemory();
            } else {
                memory = (BetaMemory) wm.getNodeMemory( (MemoryFactory) source );
            }
            
            localIt = memory.getLeftTupleMemory().fullFastIterator( leftTuple.getLeftParent() );

            LeftTuple childLeftTuple = leftTuple;
            leftTuple = childLeftTuple.getLeftParent();

            while ( leftTuple != null ) {
                if ( childLeftTuple == null ) {
                    childLeftTuple = leftTuple.getFirstChild();
                } else {
                    childLeftTuple = childLeftTuple.getLeftParentNext();
                }
                for ( ; childLeftTuple != null; childLeftTuple = childLeftTuple.getLeftParentNext() ) {
                    if ( childLeftTuple.getTupleSink() == sink ) {
                        return childLeftTuple;
                    }
                }
                leftTuple = (LeftTuple) localIt.next( leftTuple );
            }
        }
        if ( source instanceof ExistsNode ) {
            BetaMemory memory = (BetaMemory) wm.getNodeMemory( (MemoryFactory) source );

            RightTuple rightTuple = leftTuple.getLeftParent().getBlocker();
            FastIterator localIt = memory.getRightTupleMemory().fullFastIterator( rightTuple );

            for ( LeftTuple childleftTuple = leftTuple.getLeftParentNext(); childleftTuple != null; childleftTuple = childleftTuple.getLeftParentNext() ) {
                if ( childleftTuple.getTupleSink() == sink ) {
                    return childleftTuple;
                }
            }

            leftTuple = leftTuple.getLeftParent();

            // now move onto next RightTuple                                                
            while ( rightTuple != null ) {
                if ( rightTuple.getBlocked() != null ) {
                    if ( leftTuple != null ) {
                        leftTuple = leftTuple.getBlockedNext();
                    } else {
                        leftTuple = rightTuple.getBlocked();
                    }
                    for ( ; leftTuple != null; leftTuple = leftTuple.getBlockedNext() ) {
                        for ( LeftTuple childleftTuple = leftTuple.getFirstChild(); childleftTuple != null; childleftTuple = childleftTuple.getLeftParentNext() ) {
                            if ( childleftTuple.getTupleSink() == sink ) {
                                return childleftTuple;
                            }
                        }
                    }

                }
                rightTuple = (RightTuple) localIt.next( rightTuple );
            }

        } else if ( source instanceof EvalConditionNode || source instanceof QueryElementNode ) {
            LeftTuple childLeftTuple = leftTuple;
            if ( leftTuple != null ) {
                leftTuple = leftTuple.getLeftParent();

                while ( leftTuple != null ) {
                    if ( childLeftTuple != null ) {
                        childLeftTuple = childLeftTuple.getLeftParentNext();
                    } else {
                        childLeftTuple = leftTuple.getFirstChild();
                    }
                    for ( ; childLeftTuple != null; childLeftTuple = childLeftTuple.getLeftParentNext() ) {
                        if ( childLeftTuple.getTupleSink() == sink ) {
                            return childLeftTuple;
                        }
                    }

                    if ( source instanceof EvalConditionNode ) {
                        leftTuple = getNextLeftTuple( source.getLeftTupleSource(),
                                                      (LeftTupleSink) source,
                                                      leftTuple,
                                                      wm );
                    } else {
                        leftTuple = getNextLeftTuple( source.getLeftTupleSource(),
                                                      (LeftTupleSink) source,
                                                      leftTuple,
                                                      wm );
                    } 
                }
            }
        }
        return null;
    }

    public void setNextLeftTuple() {
        LeftTupleSource source = node.getLeftTupleSource();
        currentLeftTuple = getNextLeftTuple( source,
                                             node,
                                             currentLeftTuple,
                                             wm );
    }

    public LeftTuple next() {
        LeftTuple leftTuple = null;
        if ( this.currentLeftTuple != null ) {
            leftTuple = currentLeftTuple;
            setNextLeftTuple();
        }

        return leftTuple;
    }
}
