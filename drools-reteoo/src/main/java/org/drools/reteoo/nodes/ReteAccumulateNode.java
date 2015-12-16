/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufMessages.ActionQueue.Action;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.LeftTupleSourceUtils;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Accumulate;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.reteoo.common.RetePropagationContext;

import java.io.IOException;

public class ReteAccumulateNode extends AccumulateNode {

    public ReteAccumulateNode() {
    }

    public ReteAccumulateNode(int id, LeftTupleSource leftInput, ObjectSource rightInput, AlphaNodeFieldConstraint[] resultConstraints,
                              BetaConstraints sourceBinder, BetaConstraints resultBinder, Accumulate accumulate, boolean unwrapRightObject, BuildContext context) {
        super(id, leftInput, rightInput, resultConstraints, sourceBinder, resultBinder, accumulate, unwrapRightObject, context);
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTupleSourceUtils.doModifyLeftTuple(factHandle, modifyPreviousTuples, context, workingMemory,
                                               this, getLeftInputOtnId(), getLeftInferredMask());
    }

    public void assertObject( final InternalFactHandle factHandle,
                              final PropagationContext pctx,
                              final InternalWorkingMemory wm ) {
        ReteBetaNodeUtils.assertObject(this, factHandle, pctx, wm);
    }

    public void attach(BuildContext context) {
        ReteBetaNodeUtils.attach(this, context);
    }

    public void doRemove(InternalWorkingMemory workingMemory, AccumulateMemory object) { }

    public boolean doRemove(RuleRemovalContext context, ReteooBuilder builder, InternalWorkingMemory[] workingMemories) {
        return ReteBetaNodeUtils.doRemove(this, context, builder, workingMemories);
    }

    public void modifyObject(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory) {
        ReteBetaNodeUtils.modifyObject(this, factHandle, modifyPreviousTuples, context, workingMemory);
    }

    /**
     * @inheritDoc
     *
     *  When a new tuple is asserted into an AccumulateNode, do this:
     *
     *  1. Select all matching objects from right memory
     *  2. Execute the initialization code using the tuple + matching objects
     *  3. Execute the accumulation code for each combination of tuple+object
     *  4. Execute the return code
     *  5. Create a new CalculatedObjectHandle for the resulting object and add it to the tuple
     *  6. Propagate the tuple
     *
     *  The initialization, accumulation and return codes, in JBRules, are assembled
     *  into a generated method code and called once for the whole match, as you can see
     *  below:
     *
     *   Object result = this.accumulator.accumulate( ... );
     */
    public void assertLeftTuple( final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory ) {

        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );

        AccumulateContext accresult = new AccumulateContext();

        boolean useLeftMemory = true;
        if ( !this.tupleMemoryEnabled ) {
            // This is a hack, to not add closed DroolsQuery objects
            Object object = leftTuple.get( 0 ).getObject();
            if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
                useLeftMemory = false;
            }
        }

        if ( useLeftMemory ) {
            memory.getBetaMemory().getLeftTupleMemory().add( leftTuple );
            leftTuple.setContextObject( accresult );
        }

        accresult.context = this.accumulate.createContext();

        this.accumulate.init(memory.workingMemoryContext,
                             accresult.context,
                             leftTuple,
                             workingMemory);

        this.constraints.updateFromTuple( memory.getBetaMemory().getContext(),
                                          workingMemory,
                                          leftTuple );
        TupleMemory rightMemory = memory.getBetaMemory().getRightTupleMemory();

        FastIterator rightIt = getRightIterator( rightMemory );

        for ( RightTuple rightTuple = getFirstRightTuple( leftTuple,
                                                          rightMemory,
                                                          (InternalFactHandle) context.getFactHandle(),
                                                          rightIt ); rightTuple != null; rightTuple = (RightTuple) rightIt.next( rightTuple ) ) {
            InternalFactHandle handle = rightTuple.getFactHandle();
            if ( this.constraints.isAllowedCachedLeft( memory.getBetaMemory().getContext(),
                                                       handle ) ) {

                // add a match
                addMatch( leftTuple,
                          rightTuple,
                          null,
                          null,
                          workingMemory,
                          memory,
                          accresult,
                          useLeftMemory );
            }
        }

        this.constraints.resetTuple( memory.getBetaMemory().getContext() );

        if ( accresult.getAction() == null ) {
            evaluateResultConstraints( ActivitySource.LEFT,
                                       leftTuple,
                                       context,
                                       workingMemory,
                                       memory,
                                       accresult,
                                       useLeftMemory );
        } // else evaluation is already scheduled, so do nothing

    }

    /**
     * @inheritDoc
     *
     * As the accumulate node will always generate a resulting tuple,
     * we must always destroy it
     */
    public void retractLeftTuple( final LeftTuple leftTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory ) {
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );


        memory.getBetaMemory().getLeftTupleMemory().remove( leftTuple );

        final AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
        if ( accctx.getAction() != null ) {
            // there is a scheduled activation, we must cancel it
            context.removeInsertAction( accctx.getAction() );
        }
        leftTuple.setContextObject( null );

        removePreviousMatchesForLeftTuple( leftTuple,
                                           workingMemory,
                                           memory,
                                           accctx );

        if ( accctx.propagated ) {
            // if tuple was previously propagated, retract it and destroy result fact handle
            this.sink.propagateRetractLeftTupleDestroyRightTuple( leftTuple,
                                                                  context,
                                                                  workingMemory );
        } else {
            // if not propagated, just destroy the result fact handle
            // workingMemory.getFactHandleFactory().destroyFactHandle( accctx.result.getFactHandle() );
        }
    }

    /**
     * @inheritDoc
     *
     *  When a new object is asserted into an AccumulateNode, do this:
     *
     *  1. Select all matching tuples from left memory
     *  2. For each matching tuple, call a modify tuple
     */
    public void assertRightTuple( final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory ) {
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );

        memory.getBetaMemory().getRightTupleMemory().add( rightTuple );

        if ( memory.getBetaMemory().getLeftTupleMemory() == null || memory.getBetaMemory().getLeftTupleMemory().size() == 0 ) {
            // do nothing here, as we know there are no left tuples at this stage in sequential mode or for a query.
            // unless it's an "Open Query" and thus that will have left memory, so continue as normal
            return;
        }

        this.constraints.updateFromFactHandle( memory.getBetaMemory().getContext(),
                                               workingMemory,
                                               rightTuple.getFactHandle() );

        TupleMemory leftMemory =  memory.getBetaMemory().getLeftTupleMemory();

        FastIterator leftIt = getLeftIterator( leftMemory );

        for ( LeftTuple leftTuple = getFirstLeftTuple( rightTuple, leftMemory, leftIt ); leftTuple != null; leftTuple = (LeftTuple) leftIt.next( leftTuple ) ) {
            if ( this.constraints.isAllowedCachedRight( memory.getBetaMemory().getContext(),
                                                        leftTuple ) ) {
                final AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
                addMatch( leftTuple,
                          rightTuple,
                          null,
                          null,
                          workingMemory,
                          memory,
                          accctx,
                          true );
                if ( accctx.getAction() == null ) {
                    // schedule a test to evaluate the constraints, this is an optimisation for sub networks
                    // We set Source to LEFT, even though this is a right propagation, because it might end up
                    // doing multiple right propagations anyway
                    EvaluateResultConstraints action = new EvaluateResultConstraints( ActivitySource.LEFT,
                                                                                      leftTuple,
                                                                                      context,
                                                                                      workingMemory,
                                                                                      memory,
                                                                                      accctx,
                                                                                      true,
                                                                                      this );
                    accctx.setAction( action );
                    context.addInsertAction( action );
                }
            }
        }

        this.constraints.resetFactHandle( memory.getBetaMemory().getContext() );
    }

    /**
     *  @inheritDoc
     *
     *  If an object is retract, call modify tuple for each
     *  tuple match.
     */
    public void retractRightTuple( final RightTuple rightTuple,
                                   final PropagationContext pctx,
                                   final InternalWorkingMemory workingMemory ) {
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );


        BetaMemory bm = memory.getBetaMemory();

        final InternalFactHandle origin = (InternalFactHandle) pctx.getFactHandle();
        if ( pctx.getType() == PropagationContext.EXPIRATION ) {
            ((RetePropagationContext) pctx).setFactHandle(null);
        }

        bm.getRightTupleMemory().remove( rightTuple );

        removePreviousMatchesForRightTuple( rightTuple,
                                            pctx,
                                            workingMemory,
                                            memory,
                                            rightTuple.getFirstChild() );

        if ( pctx.getType() == PropagationContext.EXPIRATION ) {
            ((RetePropagationContext) pctx).setFactHandle( origin );
        }
        rightTuple.unlinkFromRightParent();

    }

    public void modifyLeftTuple( LeftTuple leftTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory ) {
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );
        final AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();

        BetaMemory bm = memory.getBetaMemory();

        // Add and remove to make sure we are in the right bucket and at the end
        // this is needed to fix for indexing and deterministic iteration
        bm.getLeftTupleMemory().removeAdd( leftTuple );

        this.constraints.updateFromTuple( bm.getContext(),
                                          workingMemory,
                                          leftTuple );
        LeftTuple childLeftTuple = getFirstMatch( leftTuple,
                                                  accctx,
                                                  false );

        TupleMemory rightMemory = bm.getRightTupleMemory();

        FastIterator rightIt = getRightIterator( rightMemory );

        RightTuple rightTuple = getFirstRightTuple( leftTuple,
                                                    rightMemory,
                                                    (InternalFactHandle) context.getFactHandle(),
                                                    rightIt );

        // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
        // if rightTuple is null, we assume there was a bucket change and that bucket is empty
        if ( childLeftTuple != null && rightMemory.isIndexed() && !rightIt.isFullIterator() &&  (rightTuple == null || (rightTuple.getMemory() !=  childLeftTuple.getRightParent().getMemory())) ) {
            // our index has changed, so delete all the previous matchings
            removePreviousMatchesForLeftTuple( leftTuple,
                                               workingMemory,
                                               memory,
                                               accctx );

            childLeftTuple = null; // null so the next check will attempt matches for new bucket
        }

        // we can't do anything if RightTupleMemory is empty
        if ( rightTuple != null ) {
            if ( childLeftTuple == null ) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for ( ; rightTuple != null; rightTuple = (RightTuple) rightIt.next( rightTuple ) ) {
                    final InternalFactHandle handle = rightTuple.getFactHandle();
                    if ( this.constraints.isAllowedCachedLeft( bm.getContext(),
                                                               handle ) ) {
                        // add a new match
                        addMatch( leftTuple,
                                  rightTuple,
                                  null,
                                  null,
                                  workingMemory,
                                  memory,
                                  accctx,
                                  true );
                    }
                }
            } else {
                boolean isDirty = false;
                // in the same bucket, so iterate and compare
                for ( ; rightTuple != null; rightTuple = (RightTuple) rightIt.next( rightTuple ) ) {
                    final InternalFactHandle handle = rightTuple.getFactHandle();

                    if ( this.constraints.isAllowedCachedLeft( bm.getContext(),
                                                               handle ) ) {
                        if ( childLeftTuple == null || childLeftTuple.getRightParent() != rightTuple ) {
                            // add a new match
                            addMatch( leftTuple,
                                      rightTuple,
                                      childLeftTuple,
                                      null,
                                      workingMemory,
                                      memory,
                                      accctx,
                                      true );
                        } else {
                            // we must re-add this to ensure deterministic iteration
                            LeftTuple temp = childLeftTuple.getHandleNext();
                            childLeftTuple.reAddRight();
                            childLeftTuple = temp;
                        }
                    } else if ( childLeftTuple != null && childLeftTuple.getRightParent() == rightTuple ) {
                        LeftTuple temp = childLeftTuple.getHandleNext();
                        // remove the match
                        removeMatch( rightTuple,
                                     childLeftTuple,
                                     workingMemory,
                                     memory,
                                     accctx,
                                     false );
                        childLeftTuple = temp;
                        // the next line means that when a match is removed from the current leftTuple
                        // and the accumulate does not support the reverse operation, then the whole
                        // result is dirty (since removeMatch above is not recalculating the total)
                        // and we need to do this later
                        isDirty = !accumulate.supportsReverse();
                    }
                    // else do nothing, was false before and false now.
                }
                if ( isDirty ) {
                    reaccumulateForLeftTuple( leftTuple,
                                              workingMemory,
                                              memory,
                                              accctx );
                }
            }
        }

        this.constraints.resetTuple( memory.getBetaMemory().getContext() );
        if ( accctx.getAction() == null ) {
            evaluateResultConstraints( ActivitySource.LEFT,
                                       leftTuple,
                                       context,
                                       workingMemory,
                                       memory,
                                       accctx,
                                       true );
        } // else evaluation is already scheduled, so do nothing
    }

    public void modifyRightTuple( RightTuple rightTuple,
                                  PropagationContext context,
                                  InternalWorkingMemory workingMemory ) {
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );

        BetaMemory bm = memory.getBetaMemory();


        // Add and remove to make sure we are in the right bucket and at the end
        // this is needed to fix for indexing and deterministic iteration
        bm.getRightTupleMemory().removeAdd( rightTuple );

        if ( bm.getLeftTupleMemory() == null || bm.getLeftTupleMemory().size() == 0 ) {
            // do nothing here, as we know there are no left tuples at this stage in sequential mode.
            return;
        }

        LeftTuple childLeftTuple = rightTuple.getFirstChild();

        TupleMemory leftMemory = bm.getLeftTupleMemory();

        FastIterator leftIt = getLeftIterator( leftMemory );

        LeftTuple leftTuple = getFirstLeftTuple( rightTuple, leftMemory, leftIt );

        this.constraints.updateFromFactHandle( bm.getContext(),
                                               workingMemory,
                                               rightTuple.getFactHandle() );

        // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
        // We assume a bucket change if leftTuple == null
        if ( childLeftTuple != null && leftMemory.isIndexed() && !leftIt.isFullIterator() && (leftTuple == null || (leftTuple.getMemory() != childLeftTuple.getLeftParent().getMemory())) ) {
            // our index has changed, so delete all the previous matches
            removePreviousMatchesForRightTuple( rightTuple,
                                                context,
                                                workingMemory,
                                                memory,
                                                childLeftTuple );
            childLeftTuple = null; // null so the next check will attempt matches for new bucket
        }

        // if LeftTupleMemory is empty, there are no matches to modify
        if ( leftTuple != null ) {
            if ( childLeftTuple == null ) {
                // either we are indexed and changed buckets or
                // we had no children before, but there is a bucket to potentially match, so try as normal assert
                for ( ; leftTuple != null; leftTuple = ( LeftTuple ) leftIt.next( leftTuple ) ) {
                    if ( this.constraints.isAllowedCachedRight( bm.getContext(),
                                                                leftTuple ) ) {
                        final AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
                        // add a new match
                        addMatch( leftTuple,
                                  rightTuple,
                                  null,
                                  null,
                                  workingMemory,
                                  memory,
                                  accctx,
                                  true );
                        if ( accctx.getAction() == null ) {
                            // schedule a test to evaluate the constraints, this is an optimisation for sub networks
                            // We set Source to LEFT, even though this is a right propagation, because it might end up
                            // doing multiple right propagations anyway
                            EvaluateResultConstraints action = new EvaluateResultConstraints( ActivitySource.LEFT,
                                                                                              leftTuple,
                                                                                              context,
                                                                                              workingMemory,
                                                                                              memory,
                                                                                              accctx,
                                                                                              true,
                                                                                              this );
                            accctx.setAction( action );
                            context.addInsertAction( action );
                        }
                    }
                }
            } else {
                // in the same bucket, so iterate and compare
                for ( ; leftTuple != null; leftTuple = (LeftTuple) leftIt.next( leftTuple ) ) {
                    if ( this.constraints.isAllowedCachedRight( bm.getContext(),
                                                                leftTuple ) ) {
                        final AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
                        LeftTuple temp = null;
                        if ( childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple ) {
                            temp = childLeftTuple.getRightParentNext();
                            // we must re-add this to ensure deterministic iteration
                            childLeftTuple.reAddLeft();
                            removeMatch( rightTuple,
                                         childLeftTuple,
                                         workingMemory,
                                         memory,
                                         accctx,
                                         true );
                            childLeftTuple = temp;
                        }
                        // add a new match
                        addMatch( leftTuple,
                                  rightTuple,
                                  null,
                                  childLeftTuple,
                                  workingMemory,
                                  memory,
                                  accctx,
                                  true );
                        if ( temp != null ) {
                            childLeftTuple = temp;
                        }
                        if ( accctx.getAction() == null ) {
                            // schedule a test to evaluate the constraints, this is an optimisation for sub networks
                            // We set Source to LEFT, even though this is a right propagation, because it might end up
                            // doing multiple right propagations anyway
                            EvaluateResultConstraints action = new EvaluateResultConstraints( ActivitySource.LEFT,
                                                                                              leftTuple,
                                                                                              context,
                                                                                              workingMemory,
                                                                                              memory,
                                                                                              accctx,
                                                                                              true,
                                                                                              this );
                            accctx.setAction( action );
                            context.addInsertAction( action );
                        }
                    } else if ( childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple ) {

                        LeftTuple temp = childLeftTuple.getRightParentNext();
                        final AccumulateContext accctx = (AccumulateContext)leftTuple.getContextObject();
                        // remove the match
                        removeMatch( rightTuple,
                                     childLeftTuple,
                                     workingMemory,
                                     memory,
                                     accctx,
                                     true );
                        if ( accctx.getAction() == null ) {
                            // schedule a test to evaluate the constraints, this is an optimisation for sub networks
                            // We set Source to LEFT, even though this is a right propagation, because it might end up
                            // doing multiple right propagations anyway
                            EvaluateResultConstraints action = new EvaluateResultConstraints( ActivitySource.LEFT,
                                                                                              leftTuple,
                                                                                              context,
                                                                                              workingMemory,
                                                                                              memory,
                                                                                              accctx,
                                                                                              true,
                                                                                              this );
                            accctx.setAction( action );
                            context.addInsertAction( action );
                        }

                        childLeftTuple = temp;
                    }
                    // else do nothing, was false before and false now.
                }
            }
        }

        this.constraints.resetFactHandle( bm.getContext() );
    }

    /**
     * Evaluate result constraints and propagate assert in case they are true
     */
    public void evaluateResultConstraints( final ActivitySource source,
                                           final LeftTuple leftTuple,
                                           final PropagationContext context,
                                           final InternalWorkingMemory workingMemory,
                                           final AccumulateMemory memory,
                                           final AccumulateContext accctx,
                                           final boolean useLeftMemory ) {

        // get the actual result
        Object result = accumulate.getResult(memory.workingMemoryContext,
                                             accctx.context,
                                             leftTuple,
                                             workingMemory);
        if (result == null) {
            return;
        }

        if ( accctx.result == null ) {
            final InternalFactHandle handle = createResultFactHandle( context,
                                                                      workingMemory,
                                                                      leftTuple,
                                                                      result );
            accctx.setResultFactHandle( handle );
            accctx.result = createRightTuple( handle,
                                              this,
                                              context );
        } else {
            accctx.result.getFactHandle().setObject( result );
        }

        // First alpha node filters
        boolean isAllowed = true;
        for ( int i = 0, length = this.resultConstraints.length; isAllowed && i < length; i++ ) {
            if ( !this.resultConstraints[i].isAllowed( accctx.result.getFactHandle(),
                                                       workingMemory ) ) {
                isAllowed = false;
            }
        }
        if ( isAllowed ) {
            this.resultBinder.updateFromTuple( memory.resultsContext,
                                               workingMemory,
                                               leftTuple );
            if ( !this.resultBinder.isAllowedCachedLeft( memory.resultsContext,
                                                         accctx.result.getFactHandle() ) ) {
                isAllowed = false;
            }
            this.resultBinder.resetTuple( memory.resultsContext );
        }

        if ( accctx.propagated ) {
            // temporarily break the linked list to avoid wrong interactions
            LeftTuple[] matchings = splitList( leftTuple,
                                               accctx,
                                               false );
            if ( isAllowed ) {
                // modify
                if ( ActivitySource.LEFT.equals( source ) ) {
                    this.sink.propagateModifyChildLeftTuple( leftTuple.getFirstChild(),
                                                             leftTuple,
                                                             context,
                                                             workingMemory,
                                                             useLeftMemory );
                } else {
                    this.sink.propagateModifyChildLeftTuple( leftTuple.getFirstChild(),
                                                             accctx.result,
                                                             context,
                                                             workingMemory,
                                                             useLeftMemory );
                }
            } else {
                // retract
                // we can't use the expiration context here, because it wouldn't cancel existing activations. however, isAllowed is false so activations should not fire
                PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
                PropagationContext cancelContext = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), org.kie.api.runtime.rule.PropagationContext.DELETION, (RuleImpl) context.getRule(),
                                                                                        context.getLeftTupleOrigin(), (InternalFactHandle) context.getFactHandle());
                this.sink.propagateRetractLeftTuple( leftTuple,
                                                     cancelContext,
                                                     workingMemory );
                accctx.propagated = false;
            }
            // restore the matchings list
            restoreList( leftTuple,
                         matchings );
        } else if ( isAllowed ) {
            // temporarily break the linked list to avoid wrong interactions
            LeftTuple[] matchings = splitList( leftTuple,
                                               accctx,
                                               false );
            // assert
            this.sink.propagateAssertLeftTuple( leftTuple,
                                                accctx.result,
                                                null,
                                                null,
                                                context,
                                                workingMemory,
                                                useLeftMemory );
            accctx.propagated = true;
            // restore the matchings list
            restoreList( leftTuple,
                         matchings );
        }

    }

    public void updateSink( final LeftTupleSink sink,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory ) {
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );

        final Iterator tupleIter = memory.getBetaMemory().getLeftTupleMemory().iterator();
        for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
            AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
            if ( accctx.propagated ) {
                // temporarily break the linked list to avoid wrong interactions
                LeftTuple[] matchings = splitList( leftTuple,
                                                   accctx,
                                                   true );
                sink.assertLeftTuple( sink.createLeftTuple( leftTuple,
                                                            accctx.result,
                                                            null,
                                                            null,
                                                            sink,
                                                            true ),
                                      context,
                                      workingMemory );
                restoreList( leftTuple,
                             matchings );
            }
        }
    }


    private void addMatch( final LeftTuple leftTuple,
                           final RightTuple rightTuple,
                           final LeftTuple currentLeftChild,
                           final LeftTuple currentRightChild,
                           final InternalWorkingMemory workingMemory,
                           final AccumulateMemory memory,
                           final AccumulateContext accresult,
                           final boolean useLeftMemory ) {
        LeftTuple tuple = leftTuple;
        InternalFactHandle handle = rightTuple.getFactHandle();
        if ( this.unwrapRightObject ) {
            // if there is a subnetwork, handle must be unwrapped
            tuple = (LeftTuple) handle.getObject();
            //handle = tuple.getLastHandle();
        }
        this.accumulate.accumulate( memory.workingMemoryContext,
                                    accresult.context,
                                    tuple,
                                    handle,
                                    workingMemory );

        // in sequential mode, we don't need to keep record of matched tuples
        if ( useLeftMemory ) {
            // linking left and right by creating a new left tuple
            createLeftTuple( leftTuple,
                             rightTuple,
                             currentLeftChild,
                             currentRightChild,
                             this,
                             true );
        }
    }

    /**
     * Removes a match between left and right tuple
     */
    private void removeMatch( final RightTuple rightTuple,
                              final LeftTuple match,
                              final InternalWorkingMemory workingMemory,
                              final AccumulateMemory memory,
                              final AccumulateContext accctx,
                              final boolean reaccumulate ) {
        // save the matching tuple
        LeftTuple leftTuple = match.getLeftParent();

        // removing link between left and right
        match.unlinkFromLeftParent();
        match.unlinkFromRightParent();

        // if there is a subnetwork, we need to unwrap the object from inside the tuple
        InternalFactHandle handle = rightTuple.getFactHandle();
        LeftTuple tuple = leftTuple;
        if ( this.unwrapRightObject ) {
            tuple = (LeftTuple) handle.getObject();
            //handle = tuple.getLastHandle();
        }

        if ( this.accumulate.supportsReverse() ) {
            // just reverse this single match
            this.accumulate.reverse( memory.workingMemoryContext,
                                     accctx.context,
                                     tuple,
                                     handle,
                                     workingMemory );
        } else {
            // otherwise need to recalculate all matches for the given leftTuple
            if ( reaccumulate ) {
                reaccumulateForLeftTuple( leftTuple,
                                          workingMemory,
                                          memory,
                                          accctx );

            }
        }
    }

    private void reaccumulateForLeftTuple( final LeftTuple leftTuple,
                                           final InternalWorkingMemory workingMemory,
                                           final AccumulateMemory memory,
                                           final AccumulateContext accctx ) {
        this.accumulate.init( memory.workingMemoryContext,
                              accctx.context,
                              leftTuple,
                              workingMemory );
        for ( LeftTuple childMatch = getFirstMatch( leftTuple,
                                                    accctx,
                                                    false ); childMatch != null; childMatch = childMatch.getHandleNext() ) {
            InternalFactHandle childHandle = childMatch.getRightParent().getFactHandle();
            LeftTuple tuple = leftTuple;
            if ( this.unwrapRightObject ) {
                tuple = (LeftTuple) childHandle.getObject();
                childHandle = tuple.getFactHandle();
            }
            this.accumulate.accumulate( memory.workingMemoryContext,
                                        accctx.context,
                                        tuple,
                                        childHandle,
                                        workingMemory );
        }
    }

    private void removePreviousMatchesForLeftTuple( final LeftTuple leftTuple,
                                                    final InternalWorkingMemory workingMemory,
                                                    final AccumulateMemory memory,
                                                    final AccumulateContext accctx ) {
        // so we just split the list keeping the head
        LeftTuple[] matchings = splitList( leftTuple,
                                           accctx,
                                           false );
        for ( LeftTuple match = matchings[0]; match != null; match = match.getHandleNext() ) {
            // can't unlink from the left parent as it was already unlinked during the splitList call above
            match.unlinkFromRightParent();
        }
        // since there are no more matches, the following call will just re-initialize the accumulation
        this.accumulate.init( memory.workingMemoryContext,
                              accctx.context,
                              leftTuple,
                              workingMemory );
    }

    private void removePreviousMatchesForRightTuple( final RightTuple rightTuple,
                                                     final PropagationContext context,
                                                     final InternalWorkingMemory workingMemory,
                                                     final AccumulateMemory memory,
                                                     final LeftTuple firstChild ) {
        for ( LeftTuple match = firstChild; match != null; ) {
            final LeftTuple tmp = match.getRightParentNext();
            final LeftTuple parent = match.getLeftParent();
            final AccumulateContext accctx = (AccumulateContext) parent.getContextObject();
            removeMatch( rightTuple,
                         match,
                         workingMemory,
                         memory,
                         accctx,
                         true );
            if ( accctx.getAction() == null ) {
                // schedule a test to evaluate the constraints, this is an optimisation for sub networks
                // We set Source to LEFT, even though this is a right propagation, because it might end up
                // doing multiple right propagations anyway
                EvaluateResultConstraints action = new EvaluateResultConstraints( ActivitySource.LEFT,
                                                                                  parent,
                                                                                  context,
                                                                                  workingMemory,
                                                                                  memory,
                                                                                  accctx,
                                                                                  true,
                                                                                  this );
                accctx.setAction( action );
                context.addInsertAction( action );
            }
            match = tmp;
        }
    }

    protected LeftTuple[] splitList( final LeftTuple parent,
                                     final AccumulateContext accctx,
                                     final boolean isUpdatingSink ) {
        LeftTuple[] matchings = new LeftTuple[2];

        // save the matchings list
        matchings[0] = getFirstMatch( parent,
                                      accctx,
                                      isUpdatingSink );
        matchings[1] = matchings[0] != null ? parent.getLastChild() : null;

        // update the tuple for the actual propagations
        if ( matchings[0] != null ) {
            if ( parent.getFirstChild() == matchings[0] ) {
                parent.setFirstChild( null );
            }
            parent.setLastChild( matchings[0].getHandlePrevious() );
            if ( parent.getLastChild() != null ) {
                parent.getLastChild().setHandleNext( null );
                matchings[0].setHandlePrevious( null );
            }
        }

        return matchings;
    }

    private void restoreList( final LeftTuple parent,
                              final LeftTuple[] matchings ) {
        // concatenate matchings list at the end of the children list
        if ( parent.getFirstChild() == null ) {
            parent.setFirstChild( matchings[0] );
            parent.setLastChild( matchings[1] );
        } else if ( matchings[0] != null ) {
            parent.getLastChild().setHandleNext( matchings[0] );
            matchings[0].setHandlePrevious( parent.getLastChild() );
            parent.setLastChild( matchings[1] );
        }
    }

    /**
     * Skips the propagated tuple handles and return the first handle
     * in the list that correspond to a match
     */
    public LeftTuple getFirstMatch( final LeftTuple leftTuple,
                                    final AccumulateContext accctx,
                                    final boolean isUpdatingSink ) {
        // unlink all right matches
        LeftTuple child = leftTuple.getFirstChild();

        if ( accctx.propagated ) {
            // To do that, we need to skip the first N children that are in fact the propagated tuples
            int target = isUpdatingSink ? this.sink.size() - 1 : this.sink.size();
            for ( int i = 0; i < target; i++ ) {
                child = child.getHandleNext();
            }
        }
        return child;
    }

    public static class EvaluateResultConstraints
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {

        private ActivitySource        source;
        private LeftTuple             leftTuple;
        private PropagationContext    context;
        private InternalWorkingMemory workingMemory;
        private AccumulateMemory      memory;
        private AccumulateContext     accctx;
        private boolean               useLeftMemory;
        private ReteAccumulateNode        node;

        public EvaluateResultConstraints(PropagationContext context) {
            this.context = context;
        }

        public EvaluateResultConstraints(ActivitySource source,
                                         LeftTuple leftTuple,
                                         PropagationContext context,
                                         InternalWorkingMemory workingMemory,
                                         AccumulateMemory memory,
                                         AccumulateContext accctx,
                                         boolean useLeftMemory,
                                         ReteAccumulateNode node) {
            this.source = source;
            this.leftTuple = leftTuple;
            this.context = context;
            this.workingMemory = workingMemory;
            this.memory = memory;
            this.accctx = accctx;
            this.useLeftMemory = useLeftMemory;
            this.node = node;
        }

        public EvaluateResultConstraints(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public Action serialize(MarshallerWriteContext context) {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public void execute(InternalWorkingMemory workingMemory) {
            final AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
            accctx.setAction(null);
            node.evaluateResultConstraints(source,
                                           leftTuple,
                                           context,
                                           workingMemory,
                                           memory,
                                           accctx,
                                           useLeftMemory);
        }

        public ActivitySource getSource() {
            return source;
        }

        public void setSource(ActivitySource source) {
            this.source = source;
        }

        public String toString() {
            return "[ResumeInsertAction leftTuple=" + leftTuple + "]\n";
        }
    }
}
