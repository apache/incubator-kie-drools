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

package org.drools.reteoo.nodes;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.FastIterator;

public class ReteBetaNodeUtils {

//    public static Object getBetaMemoryFromRightInput( final BetaNode betaNode, final InternalWorkingMemory workingMemory ) {
//        BetaMemory memory;
//        if ( NodeTypeEnums.AccumulateNode == betaNode.getType()) {
//            memory = ((AccumulateMemory)workingMemory.getNodeMemory( betaNode )).getBetaMemory();
//        } else {
//            memory = (BetaMemory) workingMemory.getNodeMemory( betaNode );
//        }
//
//        return memory;
//    }

    public static void assertObject(final BetaNode betaNode,
                                    final InternalFactHandle factHandle,
                                    final PropagationContext pctx,
                                    final InternalWorkingMemory wm) {
        RightTuple rightTuple = betaNode.createRightTuple(factHandle,
                                                          betaNode,
                                                          pctx);


        betaNode.assertRightTuple(rightTuple, pctx, wm);

    }

    public static void attach(final BetaNode betaNode, BuildContext context) {
        betaNode.getRawConstraints().init(context, betaNode.getType());
        betaNode.setUnificationJoin();

        betaNode.getRightInput().addObjectSink(betaNode);
        betaNode.getLeftTupleSource().addTupleSink(betaNode, context);

        if (context == null ) {
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
            final PropagationContext propagationContext = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION,
                                                                                               null, null, null);

            betaNode.getRightInput().updateSink(betaNode,
                                                propagationContext,
                                                workingMemory);

            betaNode.getLeftTupleSource().updateSink(betaNode,
                                                propagationContext,
                                                workingMemory);
        }
    }

    public static boolean doRemove(BetaNode betaNode,
                                   final RuleRemovalContext context,
                                   final ReteooBuilder builder,
                                   final InternalWorkingMemory[] workingMemories) {


        if (!betaNode.isInUse() || context.getCleanupAdapter() != null) {
            for (InternalWorkingMemory workingMemory : workingMemories) {
                BetaMemory memory;
                Object object = workingMemory.getNodeMemory(betaNode);

                // handle special cases for Accumulate to make sure they tidy up their specific data
                // like destroying the local FactHandles
                if (object instanceof AccumulateMemory) {
                    memory = ((AccumulateMemory) object).getBetaMemory();
                } else {
                    memory = (BetaMemory) object;
                }

                if ( betaNode.isRightInputIsRiaNode() ) {
                    // right input is RIAN, because RIAN needs sink memory, we must clear it's memory first
                    // but only if the sink size is 1, i.e. once this is removed, the rian is not in use
                    ReteRightInputAdapterNode rian = (ReteRightInputAdapterNode) betaNode.getRightInput();
                    if ( rian.getSinkPropagator().size() == 1 ) {
                        rian.removeMemory( workingMemory );
                    }
                }

                FastIterator it = memory.getLeftTupleMemory().fullFastIterator();
                for (LeftTuple leftTuple = betaNode.getFirstLeftTuple(memory.getLeftTupleMemory(), it); leftTuple != null; ) {
                    LeftTuple tmp = (LeftTuple) it.next(leftTuple);
                    if (context.getCleanupAdapter() != null) {
                        LeftTuple child;
                        while ((child = leftTuple.getFirstChild()) != null) {
                            if (child.getLeftTupleSink() == betaNode) {
                                // this is a match tuple on collect and accumulate nodes, so just unlink it
                                child.unlinkFromLeftParent();
                                child.unlinkFromRightParent();
                            } else {
                                // the cleanupAdapter will take care of the unlinking
                                context.getCleanupAdapter().cleanUp(child, workingMemory);
                            }
                        }
                    }
                    memory.getLeftTupleMemory().remove(leftTuple);
                    leftTuple.unlinkFromLeftParent();
                    leftTuple.unlinkFromRightParent();
                    leftTuple = tmp;
                }

                // handle special cases for Accumulate to make sure they tidy up their specific data
                // like destroying the local FactHandles
                if (object instanceof AccumulateMemory) {
                    ((ReteAccumulateNode) betaNode).doRemove(workingMemory, (AccumulateMemory) object);
                }

                if (!betaNode.isInUse()) {
                    it = memory.getRightTupleMemory().fullFastIterator();
                    for (RightTuple rightTuple = betaNode.getFirstRightTuple(memory.getRightTupleMemory(), it); rightTuple != null; ) {
                        RightTuple tmp = (RightTuple) it.next(rightTuple);
                        if (rightTuple.getBlocked() != null) {
                            // special case for a not, so unlink left tuple from here, as they aren't in the left memory
                            for (LeftTuple leftTuple = rightTuple.getBlocked(); leftTuple != null; ) {
                                LeftTuple temp = leftTuple.getBlockedNext();

                                leftTuple.setBlocker(null);
                                leftTuple.setBlockedPrevious(null);
                                leftTuple.setBlockedNext(null);
                                leftTuple.unlinkFromLeftParent();
                                leftTuple = temp;
                            }
                        }
                        memory.getRightTupleMemory().remove(rightTuple);
                        rightTuple.unlinkFromRightParent();
                        rightTuple = tmp;
                    }
                    workingMemory.clearNodeMemory(betaNode);
                }
            }
            context.setCleanupAdapter(null);
        }

        if (!betaNode.isInUse()) {
            betaNode.getLeftTupleSource().removeTupleSink(betaNode);
            betaNode.getRightInput().removeObjectSink(betaNode);
            return true;
        }
        return false;
    }

    public static void modifyObject(BetaNode betaNode,
                                    InternalFactHandle factHandle,
                                    ModifyPreviousTuples modifyPreviousTuples,
                                    PropagationContext context,
                                    InternalWorkingMemory wm) {
        RightTuple rightTuple = modifyPreviousTuples.peekRightTuple();

        // if the peek is for a different OTN we assume that it is after the current one and then this is an assert
        while (rightTuple != null &&
               ((BetaNode) rightTuple.getRightTupleSink()).getRightInputOtnId().before(betaNode.getRightInputOtnId())) {
            modifyPreviousTuples.removeRightTuple();

            // we skipped this node, due to alpha hashing, so retract now
            rightTuple.setPropagationContext(context);
            rightTuple.getRightTupleSink().retractRightTuple(rightTuple,
                                                             context,
                                                             wm);
            rightTuple = modifyPreviousTuples.peekRightTuple();
        }

        if (rightTuple != null && ((BetaNode) rightTuple.getRightTupleSink()).getRightInputOtnId().equals(betaNode.getRightInputOtnId())) {
            modifyPreviousTuples.removeRightTuple();
            rightTuple.reAdd();
            if (rightTuple.getStagedType() != LeftTuple.INSERT) {
                // things staged as inserts, are left as inserts and use the pctx associated from the time of insertion
                rightTuple.setPropagationContext(context);
            }
            if (context.getModificationMask().intersects( betaNode.getRightInferredMask())) {
                // RightTuple previously existed, so continue as modify
                betaNode.modifyRightTuple(rightTuple,
                                          context,
                                          wm);
            }
        } else {
            if (context.getModificationMask().intersects( betaNode.getRightInferredMask())) {
                // RightTuple does not exist for this node, so create and continue as assert
                betaNode.assertObject(factHandle,
                                      context,
                                      wm);
            }
        }
    }


}
