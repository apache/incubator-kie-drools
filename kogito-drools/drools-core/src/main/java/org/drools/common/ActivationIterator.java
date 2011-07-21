package org.drools.common;

import org.drools.KnowledgeBase;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashSet;
import org.drools.core.util.ObjectHashSet.ObjectEntry;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.AccumulateNode;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.EvalConditionNode;
import org.drools.reteoo.ExistsNode;
import org.drools.reteoo.FromNode;
import org.drools.reteoo.FromNode.FromMemory;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.spi.Activation;

public class ActivationIterator
    implements
    Iterator {
    private InternalWorkingMemory wm;

    private Iterator              nodeIter;

    private RuleTerminalNode      node;

    private LeftTuple             currentLeftTuple;

    // Iterates object type nodes
    private ObjectEntry           factHandleEntry;
    Iterator                      otnIterator;

    ActivationIterator() {

    }

    private ActivationIterator(InternalWorkingMemory wm,
                               KnowledgeBase kbase) {
        this.wm = wm;

        nodeIter = TerminalNodeIterator.iterator( kbase );

        // Find the first node with Activations an set it.
        while ( (node = (RuleTerminalNode) nodeIter.next()) != null && currentLeftTuple == null ) {
            setFirstLeftTupleForNode();
            if ( currentLeftTuple != null ) {
                break;
            }
        }
    }

    public static ActivationIterator iterator(StatefulKnowledgeSession ksession) {
        return new ActivationIterator( ((StatefulKnowledgeSessionImpl) ksession).getInternalWorkingMemory(),
                                       ksession.getKnowledgeBase() );
    }

    public void setFirstLeftTupleForNode() {
        LeftTupleSource source = node.getLeftTupleSource();

        this.currentLeftTuple = getFirstLeftTuple( source,
                                                   (LeftTupleSink) node,
                                                   wm );

        //        while ( !(source instanceof BetaNode) ) {
        //            //source = source.get
        //        }
        //
        //        if ( source instanceof LeftInputAdapterNode ) {
        //            parentIsFactHandle = true;
        //            ObjectTypeNode otn = null;
        //            ObjectSource os = ((LeftInputAdapterNode) source).getParentObjectSource();
        //            while ( !(os instanceof ObjectTypeNode) ) {
        //                os = os.getParentObjectSource();
        //            }
        //            otn = (ObjectTypeNode) os;
        //
        //            ObjectHashSet memory = (ObjectHashSet) wm.getNodeMemory( otn );
        //            Iterator it = memory.iterator();
        //
        //            for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
        //                InternalFactHandle handle = (InternalFactHandle) entry.getValue();
        //                for ( this.currentLeftTuple = handle.getFirstLeftTuple(); this.currentLeftTuple != null; this.currentLeftTuple = this.currentLeftTuple.getLeftParentNext() ) {
        //                    if ( this.currentLeftTuple.getLeftTupleSink() == node ) {
        //                        return;
        //                    }
        //                }
        //            }
        //
        //        } else {
        //            //LeftTuple leftTuple = getChildLeftTuple();
        //            
        //            parentIsFactHandle = false;
        //
        //            if ( source instanceof EvalConditionNode ) {
        //                EvalConditionNode node = (EvalConditionNode) source;
        //                node.getLeftTupleSource();
        //
        //            }
        //
        //            BetaMemory memory;
        //            if ( source instanceof AccumulateNode ) {
        //                memory = ((AccumulateMemory) wm.getNodeMemory( (NodeMemory) source )).betaMemory;
        //            } else {
        //                memory = (BetaMemory) wm.getNodeMemory( (NodeMemory) source );
        //            }
        //            it = memory.getLeftTupleMemory().fullFastIterator();
        //            for ( this.currentLeftTuple = BetaNode.getFirstLeftTuple( memory.getLeftTupleMemory(),
        //                                                                      it ); this.currentLeftTuple != null; this.currentLeftTuple = (LeftTuple) it.next( this.currentLeftTuple ) ) {
        //                for ( LeftTuple child = currentLeftTuple.getFirstChild(); child != null; child = child.getLeftParentNext() ) {
        //                    if ( child.getLeftTupleSink() == node ) {
        //                        currentLeftTuple = child;
        //                        return;
        //                    }
        //                }
        //            }
        //        }
    }

    public LeftTuple getFirstLeftTuple(LeftTupleSource source,
                                       LeftTupleSink sink,
                                       InternalWorkingMemory wm) {
        if ( source instanceof JoinNode || source instanceof NotNode || source instanceof FromNode  || source instanceof AccumulateNode ) {
            
            BetaMemory memory;
            FastIterator localIt;
            if ( source instanceof FromNode ) {
                memory = ((FromMemory) wm.getNodeMemory( (NodeMemory) source )).betaMemory;
            } else if ( source instanceof AccumulateNode ) {
                memory = ((AccumulateMemory) wm.getNodeMemory( (NodeMemory) source )).betaMemory;
            } else {
                memory = (BetaMemory) wm.getNodeMemory( (NodeMemory) source );                
            }

            localIt = memory.getLeftTupleMemory().fullFastIterator();
            LeftTuple leftTuple = BetaNode.getFirstLeftTuple( memory.getLeftTupleMemory(),
                                                              localIt );

            while ( leftTuple != null ) {
                for ( LeftTuple childleftTuple = leftTuple.getFirstChild(); childleftTuple != null; childleftTuple = childleftTuple.getLeftParentNext() ) {
                    if ( childleftTuple.getLeftTupleSink() == sink ) {
                        return childleftTuple;
                    }
                }
                leftTuple = (LeftTuple) localIt.next( leftTuple );
            }
        }
        if ( source instanceof ExistsNode ) {
            BetaMemory memory = (BetaMemory) wm.getNodeMemory( (NodeMemory) source );
            FastIterator localIt = memory.getRightTupleMemory().fullFastIterator();

            RightTuple rightTuple = BetaNode.getFirstRightTuple( memory.getRightTupleMemory(),
                                                                 localIt );

            
            while ( rightTuple != null ) {
                if ( rightTuple.getBlocked() != null ) {
                    for ( LeftTuple leftTuple = rightTuple.getBlocked(); leftTuple != null; leftTuple = leftTuple.getBlockedNext() ) {
                        for ( LeftTuple childleftTuple = leftTuple.getFirstChild(); childleftTuple != null; childleftTuple = childleftTuple.getLeftParentNext() ) {
                            if ( childleftTuple.getLeftTupleSink() == sink ) {
                                return childleftTuple;
                            }
                        }                        
                    }
                 
                }
                rightTuple = ( RightTuple ) localIt.next( rightTuple );
            }
        } else if ( source instanceof LeftInputAdapterNode ) {
            ObjectTypeNode otn = null;
            ObjectSource os = ((LeftInputAdapterNode) source).getParentObjectSource();
            while ( !(os instanceof ObjectTypeNode) ) {
                os = os.getParentObjectSource();
            }
            otn = (ObjectTypeNode) os;

            ObjectHashSet memory = (ObjectHashSet) wm.getNodeMemory( otn );
            otnIterator = memory.iterator();

            for ( factHandleEntry = (ObjectEntry) otnIterator.next(); factHandleEntry != null; factHandleEntry = (ObjectEntry) otnIterator.next() ) {
                InternalFactHandle handle = (InternalFactHandle) factHandleEntry.getValue();
                for ( LeftTuple leftTuple = handle.getFirstLeftTuple(); leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
                    if ( leftTuple.getLeftTupleSink() == sink ) {
                        return leftTuple;
                    }
                }
            }
        } else if ( source instanceof EvalConditionNode ) {
            LeftTuple parentLeftTuple = getFirstLeftTuple( ((EvalConditionNode) source).getLeftTupleSource(),
                                                           (LeftTupleSink) source,
                                                           wm );
            while ( parentLeftTuple != null ) {
                for ( LeftTuple leftTuple = parentLeftTuple.getFirstChild(); leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
                    if ( leftTuple.getLeftTupleSink() == sink ) {
                        return leftTuple;
                    }
                }
                parentLeftTuple = getNextLeftTuple( ((EvalConditionNode) source).getLeftTupleSource(),
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

        if ( factHandleEntry != null ) {
            LeftTuple leftParent = leftTuple.getLeftParent();
            // We have a parent LeftTuple so try there next
            if ( leftParent != null ) {
                // we know it has to be evalNode
                while ( leftParent != null ) {
                    leftParent = getNextLeftTuple( ((EvalConditionNode) source).getLeftTupleSource(),
                                                   (EvalConditionNode) source,
                                                   leftParent,
                                                   wm );
                    if ( leftParent != null ) {
                        for ( leftTuple = leftParent.getFirstChild(); leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
                            if ( leftTuple.getLeftTupleSink() == sink ) {
                                return leftTuple;
                            }
                        }
                    }
                }
                return null;
            }

            if ( factHandleEntry == null ) {
                // we've exhausted this OTN
                return null;
            }

            // We have exhausted the current FactHandle, now try the next                     
            for ( factHandleEntry = (ObjectEntry) otnIterator.next(); factHandleEntry != null; factHandleEntry = (ObjectEntry) otnIterator.next() ) {
                InternalFactHandle handle = (InternalFactHandle) factHandleEntry.getValue();
                for ( leftTuple = handle.getFirstLeftTuple(); leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
                    if ( leftTuple.getLeftTupleSink() == sink ) {
                        return leftTuple;
                    }
                }
            }
            // We've exhausted this OTN so set the iterator to null
            factHandleEntry = null;
            otnIterator = null;

        } else if ( source instanceof JoinNode || source instanceof NotNode  || source instanceof FromNode || source instanceof AccumulateNode ) {
            BetaMemory memory;
            FastIterator localIt;
            if ( source instanceof FromNode ) {
                memory = ((FromMemory) wm.getNodeMemory( (NodeMemory) source )).betaMemory;
            } else if ( source instanceof AccumulateNode ) {
                memory = ((AccumulateMemory) wm.getNodeMemory( (NodeMemory) source )).betaMemory;
            } else {
                memory = (BetaMemory) wm.getNodeMemory( (NodeMemory) source );                
            }
            
            localIt = memory.getLeftTupleMemory().fullFastIterator();         

            LeftTuple childLeftTuple = leftTuple;
            if ( childLeftTuple != null ) {
                leftTuple = childLeftTuple.getLeftParent();

                while ( leftTuple != null ) {
                    if ( childLeftTuple == null ) {
                        childLeftTuple = leftTuple.getFirstChild();
                    } else {
                        childLeftTuple = childLeftTuple.getLeftParentNext();
                    }
                    for ( ; childLeftTuple != null; childLeftTuple = childLeftTuple.getLeftParentNext() ) {
                        if ( childLeftTuple.getLeftTupleSink() == sink ) {
                            return childLeftTuple;
                        }
                    }
                    leftTuple = (LeftTuple) localIt.next( leftTuple );
                }
            }
        }
        if ( source instanceof ExistsNode ) {
            BetaMemory memory = (BetaMemory) wm.getNodeMemory( (NodeMemory) source );
            FastIterator localIt = memory.getRightTupleMemory().fullFastIterator();
            
            for ( LeftTuple childleftTuple = leftTuple.getLeftParentNext(); childleftTuple != null; childleftTuple = childleftTuple.getLeftParentNext() ) {
                if ( childleftTuple.getLeftTupleSink() == sink ) {
                    return childleftTuple;
                }
            }
            
            // now move onto next RightTuple
            RightTuple rightTuple = leftTuple.getLeftParent().getBlocker();                                    
            rightTuple = ( RightTuple ) localIt.next( rightTuple );
            while ( rightTuple != null ) {
                if ( rightTuple.getBlocked() != null ) {
                    for ( leftTuple = rightTuple.getBlocked(); leftTuple != null; leftTuple = leftTuple.getBlockedNext() ) {
                        for ( LeftTuple childleftTuple = leftTuple.getFirstChild(); childleftTuple != null; childleftTuple = childleftTuple.getLeftParentNext() ) {
                            if ( childleftTuple.getLeftTupleSink() == sink ) {
                                return childleftTuple;
                            }
                        }
                    }
                 
                }
                rightTuple = ( RightTuple ) localIt.next( rightTuple );
            }
          
            //            RightTuple rightTuple = BetaNode.getFirstRightTuple( memory.getRightTupleMemory(),
            //                                                                 localIt );
            //
            //            if ( rightTuple.getBlocked() != null ) {
            //                for ( LeftTuple childleftTuple = rightTuple.getBlocked(); childleftTuple != null; childleftTuple = childleftTuple.getBlockedNext() ) {
            //                    if ( childleftTuple.getLeftTupleSink() == sink ) {
            //                        return childleftTuple;
            //                    }
            //                }
            //            }
        } else if ( source instanceof EvalConditionNode ) {
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
                        if ( childLeftTuple.getLeftTupleSink() == sink ) {
                            return childLeftTuple;
                        }
                    }

                    leftTuple = getNextLeftTuple( ((EvalConditionNode) source).getLeftTupleSource(),
                                                  (LeftTupleSink) source,
                                                  leftTuple,
                                                  wm );
                }
            }
        }
        return null;
    }

    public void setNextLeftTuple() {
        LeftTupleSource source = node.getLeftTupleSource();
        currentLeftTuple = getNextLeftTuple( source,
                                             (LeftTupleSink) node,
                                             currentLeftTuple,
                                             wm );

        //        if ( parentIsFactHandle ) {
        //            for ( currentLeftTuple = currentLeftTuple.getLeftParentNext(); currentLeftTuple != null; currentLeftTuple = currentLeftTuple.getLeftParentNext() ) {
        //                if ( currentLeftTuple.getLeftTupleSink() == node ) {
        //                    break;
        //                }
        //            }
        //        } else {
        //            // in beta nodes we always need to back up to the TerminalNode parent, and iterate all it's children first
        //            if ( currentLeftTuple.getLeftParentNext() != null ) {
        //                for ( LeftTuple child = currentLeftTuple.getLeftParentNext(); child != null; child = child.getLeftParentNext() ) {
        //                    if ( child.getLeftTupleSink() == node ) {
        //                        currentLeftTuple = child;
        //                        return;
        //                    }
        //                }
        //            }
        //            currentLeftTuple = currentLeftTuple.getLeftParent();
        //            for ( currentLeftTuple = (LeftTuple) it.next( currentLeftTuple ); this.currentLeftTuple != null; this.currentLeftTuple = (LeftTuple) it.next( this.currentLeftTuple ) ) {
        //                for ( LeftTuple child = currentLeftTuple.getFirstChild(); child != null; child = child.getLeftParentNext() ) {
        //                    if ( child.getLeftTupleSink() == node ) {
        //                        currentLeftTuple = child;
        //                        return;
        //                    }
        //                }
        //            }
        //        }
    }

    public Object next() {
        Activation acc = null;
        if ( this.currentLeftTuple != null ) {
            acc = (Activation) currentLeftTuple.getObject();
            setNextLeftTuple();

            if ( this.currentLeftTuple == null ) {
                while ( (node = (RuleTerminalNode) nodeIter.next()) != null && currentLeftTuple == null ) {
                    setFirstLeftTupleForNode();
                    if ( currentLeftTuple != null ) {
                        break;
                    }
                }
            }

            //            // If we are at the end of the current node then find the next node that is not empty 
            //            if ( this.currentLeftTuple == null ) {
            //                // Find the first node with Activations an set it.
            //                while ( (node = (RuleTerminalNode) nodeIter.next()) != null && currentLeftTuple == null ) {
            //                    setFirstLeftTupleForNode();
            //                    if ( currentLeftTuple != null ) {
            //                        break;
            //                    }
            //                }
            //            }
        }

        return acc;
    }

}
